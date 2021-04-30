package com.example.bluetoothbasedchattingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class Activity_1 extends AppCompatActivity {

    TextView name_icon,name_head;
    EditText name_input,profession_input;
    Button save_details_button;
    String name,prof,msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        name_icon= (TextView)findViewById(R.id.name_Icon);
        name_head= (TextView)findViewById(R.id.name_Head);
        name_input= (EditText)findViewById(R.id.name_Input);
        profession_input= (EditText)findViewById(R.id.profession_Input);
        save_details_button= (Button)findViewById(R.id.save_Details_Button);

        save_details_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(name_input.getText())){    /*If we want to perform any action on TEXTS then, TextUtil library has
                                                                    almost all the functions. We doesn't need to made any function to
                                                                       perform any action...*/
                    Toast.makeText(getApplicationContext(),"Name is Required!",Toast.LENGTH_SHORT).show();
                    name_input.setError("Name is Required!");   /* setError() method shows the error message with RED MARK on that field,
                                                                       if required FIELD is EMPTY */
                }
                else if(TextUtils.isEmpty(profession_input.getText())){
                    Toast.makeText(getApplicationContext(),"Profession is Required!",Toast.LENGTH_SHORT).show();
                    profession_input.setError("Profession is Required!");
                }
                else {
                    prof = profession_input.getText().toString();
                    name = name_input.getText().toString();
                    name_head.setText(name);
                    name_icon.setText(name.substring(0, 1));
                    msg = "Dear " + name + ", Your Data is Saved...";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    SharedPreferences prefs= getSharedPreferences("prefs",MODE_PRIVATE);
                    SharedPreferences.Editor editor= prefs.edit();
                    editor.putBoolean("first_Start",false);
                    editor.apply();

                    open_Next_Activity();
                }
            }
        });

    }
    private void open_Next_Activity(){
        SharedPreferences prefs= getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor= prefs.edit();
        editor.putString("user_Name",name);
        editor.apply();
        Intent intent_2= new Intent(getApplicationContext(),Activity_2.class);
        new Timer().schedule(new TimerTask(){   /* We create here the object of Timer. By this, next action will perform after the delay,
                                                    that we are given according to our need. Schedule() is the method of Timer class
                                                    and TimerTask() method is implemented in it. It starts the timer from 0, and ends
                                                    after the delay (1500) and DELAY takes the time only in milliseconds, 1 SEC = 1000 MS.
                                                    */
            public void run(){
                startActivity(intent_2);
            }
        }, 1500);
    }
}