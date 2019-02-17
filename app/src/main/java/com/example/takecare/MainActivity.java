package com.example.takecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {


    public class UserLocation {

        public String userID;
        public double lat;
        public double longi;

        public UserLocation() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public UserLocation(String id, double lat ,double longi) {
            this.userID = id;
            this.lat = lat;
            this.longi = longi;
        }

    }


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseApp fb;
    private FirebaseUser userLoggedIn;

    private FirebaseDatabase realtime;
    int RC_SIGN_IN;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mAuth = FirebaseAuth.getInstance();
        //FirebaseApp.initializeApp(this);
        //FirebaseApp.initializeApp(this);


            System.out.println("WAITING FOR DATABASE");
            this.db = FirebaseFirestore.getInstance();
            this.realtime = FirebaseDatabase.getInstance();


        checkLocationPermission();
        //DatabaseReference myRef = database.getReference("users");

        if(userLoggedIn==null){
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }




// Create and launch sign-in intent

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        //LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final Button helpButton = (Button) findViewById(R.id.helpFriend);
        helpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                //Intent activityChangeIntent = new Intent(MainActivity.this, category.class);
                Intent intent = new Intent(MainActivity.this, category.class);
                intent.putExtra("UserID", userLoggedIn.getUid());
                intent.putExtra("self", false);
                // currentContext.startActivity(activityChangeIntent);
                MainActivity.this.startActivity(intent);
            }
        });

        final Button helpMe = (Button) findViewById(R.id.helpMe);
        helpMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                //Intent activityChangeIntent = new Intent(MainActivity.this, help_sent_self.class);
                Intent intent = new Intent(MainActivity.this, help_sent_self.class);
                intent.putExtra("UserID", userLoggedIn.getUid());
                intent.putExtra("self", true);
                // currentContext.startActivity(activityChangeIntent);

                MainActivity.this.startActivity(intent);
            }
        });
        final Button profile = (Button) findViewById(R.id.profileEdit2);
        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(MainActivity.this, profileInfo.class);
                // currentContext.startActivity(activityChangeIntent);

                MainActivity.this.startActivity(activityChangeIntent);
            }
        });


        System.out.println("DONE");
        //myRef.setValue("Hello, World!");


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                this.userLoggedIn = user;

                MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                    @Override
                    public void gotLocation(Location location){
                        realtime.getReference("users").child(userLoggedIn.getUid()).child("lat").setValue(location.getLatitude());
                        realtime.getReference("users").child(userLoggedIn.getUid()).child("long").setValue(location.getLongitude());
                    }
                };
                MyLocation myLocation = new MyLocation();
                myLocation.getLocation(this, locationResult);
                //afterLogin(user);
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    public void afterLogin(FirebaseUser user){
        System.out.println("SUCCESS!");
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //FirebaseApp.initializeApp(this.getApplicationContext());


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    // makeUseOfNewLocation(location);
                }
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //locationManager.removeUpdates(this);
        }
    }




    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("LOCATION SERVICE ACCEPTED");
                        //Request location updates:
                        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                        LocationListener locationListener = new LocationListener() {
                            public void onLocationChanged(Location location) {
                                // Called when a new location is found by the network location provider.
                                //makeUseOfNewLocation(location);
                                UserLocation temp = new UserLocation(userLoggedIn.getUid(), location.getLatitude(), location.getLongitude());
                                realtime.getReference("users").child(temp.userID).child("lat").setValue(temp.lat);
                                realtime.getReference("users").child(temp.userID).child("long").setValue(temp.longi);
                                updateLocationDB();
                            }
                            public void onStatusChanged(String provider, int status, Bundle extras) {}

                            public void onProviderEnabled(String provider) {}

                            public void onProviderDisabled(String provider) {}
                        };
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                    }

                } else {
                    System.out.println("LOCATION SERVICE DENIED");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

//    public void getCityName(final Location location, final OnGeocoderFinishedListener listener) {
//        new AsyncTask<Void, Integer, List<Address>>() {
//            @Override
//            protected List<Address> doInBackground(Void... arg0) {
//                Geocoder coder = new Geocoder(getContext(), Locale.ENGLISH);
//                List<Address> results = null;
//                try {
//                    results = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                } catch (IOException e) {
//                    // nothing
//                }
//                return results;
//            }
//
//            @Override
//            protected void onPostExecute(List<Address> results) {
//                if (results != null && listener != null) {
//                    listener.onFinished(results);
//                }
//            }
//        }.execute();
//    }

    public void updateLocationDB(){

        //db.collection("users").document();

    }



}

