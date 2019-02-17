package com.example.takecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

import static android.content.ContentValues.TAG;
import static com.google.firebase.database.FirebaseDatabase.getInstance;


public class category extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(category.this, MainActivity.class);

                // currentContext.startActivity(activityChangeIntent);

                category.this.startActivity(activityChangeIntent);
            }
        });
        Button profile = (Button) findViewById(R.id.profileEdit);
        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(category.this, profileInfo.class);

                // currentContext.startActivity(activityChangeIntent);

                category.this.startActivity(activityChangeIntent);
            }
        });

        Button alc  = (Button) findViewById(R.id.alcohol);
        double lat;
        final double longi;
        alc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Map<String, Object> crisis = new HashMap<>();
                MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                    @Override
                    public void gotLocation(Location location){
                        FirebaseDatabase.getInstance().getReference("users").child(getIntent().getExtras().getString("UserID")).child("lat").setValue(location.getLatitude());
                        FirebaseDatabase.getInstance().getReference("users").child(getIntent().getExtras().getString("UserID")).child("long").setValue(location.getLongitude());
                    }
                };
                MyLocation myLocation = new MyLocation();
                myLocation.getLocation(getApplicationContext(), locationResult);


                //double lat = FirebaseDatabase.getInstance().getReference("users").child(getIntent().getExtras().getString("UserID")).child("lat").getKey();

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("users");
                final String id = getIntent().getExtras().getString("UserID");
// Attach a listener to read the data at our posts reference
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap data = (HashMap) dataSnapshot.getValue();
                        //crisis.put("Location", GeoPoint());
                        //crisis.put("last", "Lovelace");
                        //crisis.put("born", 1815);
                        System.out.println(((HashMap) data.get(id)).get("lat"));
                        myCallback.onCallbackValue(data);
                        //System.out.println(data[id]["long"]);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });


                //FirebaseDatabase.getInstance().getReference("users").get().addListenerForSingleValueEvent()



                //FirebaseFirestore.getInstance().collection("crises").add({"id": "TestMessage"};);

                Intent activityChangeIntent = new Intent(category.this, help_sent.class);

                // currentContext.startActivity(activityChangeIntent);

                category.this.startActivity(activityChangeIntent);
                }
//                if (getIntent().getExtras().getBoolean("self")) {
//                    Intent activityChangeIntent = new Intent(category.this, help_sent_self.class);
//                    category.this.startActivity(activityChangeIntent);
//                } else {
//                    Intent activityChangeIntent = new Intent(category.this, help_sent.class);
//                    category.this.startActivity(activityChangeIntent);
//                }

                //});

            });
        }

    }

