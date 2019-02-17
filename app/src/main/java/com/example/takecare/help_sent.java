package com.example.takecare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class help_sent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_sent);


        Button profile = (Button) findViewById(R.id.profileEdit3);
        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(help_sent.this, profileInfo.class);

                // currentContext.startActivity(activityChangeIntent);

                help_sent.this.startActivity(activityChangeIntent);
            }
        });

        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(help_sent.this, category.class);

                // currentContext.startActivity(activityChangeIntent);

                help_sent.this.startActivity(activityChangeIntent);
            }
        });

    }


}
