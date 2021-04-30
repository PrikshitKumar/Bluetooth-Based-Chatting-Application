package com.example.bluetoothbasedchattingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        SharedPreferences prefs= getSharedPreferences("prefs",MODE_PRIVATE);    //SharedPreferences is used for store the data
        String user_Name= prefs.getString("user_name"," ");
        boolean first_Start= prefs.getBoolean("first_Start",true);
                                                    /* first_Start stores the boolean value, when user starts the application first time
                                                        and fill the required information correctly. Then, next activity will be called.
                                                        Otherwise, it will stuck at the login page and demand for the required fields..*/

        if(first_Start){        /*If value of first_start variable is TRUE, means it is the first time of user to run the application,
                                    and open_Next_Activity() function is called...*/
            open_Next_Activity();
        }
        else {      /*If the value of first_Start variable is FALSE, means it is not the first time of run the application and now login
                        credentials are not asked by the user...*/
            open_Second_Activity();
        }
    }

    private void open_Next_Activity(){
        Intent intent= new Intent(this,Activity_1.class);   //Intents are used for share the data with another activity.
        startActivity(intent);
    }

    private void open_Second_Activity(){
        Intent intent_2= new Intent(this,Activity_2.class);
        startActivity(intent_2);
    }


    private void checkPermissions(){   /* Devices those running  on Android Version: 'Marshmallow' or above, we need dynamic Permission
                                             Request for those to discover the devices..*/
//        Context context;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If the Fine_Location Permission is not Granted, We will Request the Permission here. this block will help the user to detect the devices dynamically...
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
            /* Request Code must be greater than 0 and UNIQUE. So, Once this Permission is requested, we will get a response. So, We need to handle that.*/
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){         //If we get the same number as RequestCode from Manifest File, that RequestCode we passed earlier in check_Permissions() Function..
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                //It means our Permissions has been granted..
            }
            else{   //If Permissions are Denied due to any technical Reasons, Then again a prompt is open to the USER to Accept or Denied the Required Permmisions.
                new AlertDialog.Builder(getApplicationContext())
                        .setCancelable(false) // If we want to cancel this Alert by taking the input from User (Access or Deny), then setCancelable() should be set to False
                        .setMessage("Location Permission is Required for Proceeding Further. It is just to detect the nearby devices.\nPlease Accept it.")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkPermissions();     //Here we check for the Permissions again, whether these are correct permissions or not
                            }
                        })
                        .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.finish();   //If User Denied the Permissions then, Application will be Finished...
                            }
                        }).create(); //For Showing this AlertDialog to USER.
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}