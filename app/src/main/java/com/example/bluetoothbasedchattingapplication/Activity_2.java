package com.example.bluetoothbasedchattingapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Activity_2 extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter;  /*The BluetoothAdapter is required for any and all Bluetooth activity. To get the
                                             BluetoothAdapter, call the static getDefaultAdapter() method. */
    BluetoothDevice[] btArray;
    public static BluetoothDevice device;

//    public static String get_The_Value_From_nameinput="get_The_Value_From_nameinput";
    public static String nameinput;
    private String list_Data;
//    public final static UUID My_UUID=UUID.fromString("39bfa716-88ec-11eb-8dcd-0242ac130003");


    SwitchCompat switchCompat;
    ListView devices_list,available_devices_list;
    TextView off_msg,paired_devices,available_devices;
    String bluetooth_off_text,bluetooth_on_text;
    Button reload_session,discoverable_device,listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        bluetooth_off_text="Turn ON Bluetooth to see a list of devices you can pair with or have already paired with.";
        bluetooth_on_text="Make sure the device you want to connect is in pairing mode. Your Phone is currently visible to nearby devices.";

        Intent intent=getIntent();
//        My_UUID= UUID.fromString("39bfa716-88ec-11eb-8dcd-0242ac130003");

        SharedPreferences prefs= getSharedPreferences("prefs",MODE_PRIVATE);
        nameinput= prefs.getString("user_Name"," ");

        Toast.makeText(this,"Welcome "+nameinput+"...",Toast.LENGTH_SHORT).show();

        initialize_The_Variable();
        Bluetooth_Status_Check();

        switchCompat.setOnClickListener(v -> {
            if(switchCompat.isChecked()){
                off_msg.setText(bluetooth_on_text);
                Bluetooth_Status_ON();
            }
            else{
                make_Invisible();   //device_list is no invisible before in this field
                off_msg.setText(bluetooth_off_text);
                Bluetooth_Status_OFF();
            }
        });

        reload_session.setOnClickListener(v -> {
            Bluetooth_Status_ON();
            Available_Devices();
        });

        // ServerThread Calling
        listener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3= new Intent(getApplicationContext(),activity_3.class);
                intent3.putExtra(activity_3.TSV,"Server_Thread");
                startActivity(intent3);
            }
        });

        discoverable_device.setOnClickListener(v -> enable_Discoverability());

        // ClientThread Calling
        devices_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent4= new Intent(getApplicationContext(),activity_3.class);
                device= btArray[position];
//                intent4.putExtra(activity_3.BD,device);
                intent4.putExtra(activity_3.TSV,"Client_Thread");
                startActivity(intent4);
            }
        });
    }


    // Initialize the Variables
    private void initialize_The_Variable() {
        switchCompat= (SwitchCompat) findViewById(R.id.switch_Btn);
        devices_list= (ListView) findViewById(R.id.devices_List);
        available_devices_list= (ListView)findViewById(R.id.available_Devices_List);
        available_devices= (TextView) findViewById(R.id.avail_Devices);
        off_msg= (TextView) findViewById(R.id.off_Msg);
        paired_devices= (TextView) findViewById(R.id.paired_Devices);
        reload_session= (Button) findViewById(R.id.relod_Session);
        discoverable_device= (Button) findViewById(R.id.discoverable_Device);
        listener= (Button) findViewById(R.id.Listener);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();     //BluetoothAdapter returns the current status of Bluetooth...
    }


    // Check the Status whether the Bluetooth is ON or OFF, when Start the Application
    private void Bluetooth_Status_Check(){
        if(bluetoothAdapter.isEnabled()){
            off_msg.setText(bluetooth_on_text);
            switchCompat.setChecked(true);
            Bluetooth_Status_ON();
        }
        else{
            off_msg.setText(bluetooth_off_text);
            switchCompat.setChecked(false);
            make_Invisible();
        }
    }


    // Visible the Attributes when needed
    private void make_Visible(){
        reload_session.setVisibility(View.VISIBLE);
        listener.setVisibility(View.VISIBLE);
        devices_list.setVisibility(View.VISIBLE);
        paired_devices.setVisibility(View.VISIBLE);
        discoverable_device.setVisibility(View.VISIBLE);
        available_devices_list.setVisibility(View.VISIBLE);
        available_devices.setVisibility(View.VISIBLE);
    }


    // Invisible the Attributes when nedded
    private void make_Invisible(){
        reload_session.setVisibility(View.INVISIBLE);
        listener.setVisibility(View.INVISIBLE);
        devices_list.setVisibility(View.INVISIBLE);
        paired_devices.setVisibility(View.INVISIBLE);
        discoverable_device.setVisibility(View.INVISIBLE);
        available_devices_list.setVisibility(View.INVISIBLE);
        available_devices.setVisibility(View.INVISIBLE);
    }


    // When we call startActivityForResult(), this method. Then, this function is automatically called after startActivityForResult();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {   /* This method called automatically by
                                                                                                 startActivityForResult(), this method */
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                off_msg.setText(bluetooth_on_text);

                make_Visible(); //available_devices_list is visible in this field
                Toast.makeText(this, "Bluetooth is Enabled..", Toast.LENGTH_SHORT).show();
                Paired_Devices();
            }
            else if (resultCode == RESULT_CANCELED) {
                off_msg.setText(bluetooth_off_text);

                make_Invisible();   //available_devices_list is INVISIBLE in this Field
                Toast.makeText(this, "Bluetooth Enabling Request is Cancelled..", Toast.LENGTH_SHORT).show();
                switchCompat.setChecked(false);
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    // Turning OFF the Bluetooth
    private void Bluetooth_Status_OFF(){
        if(bluetoothAdapter.isEnabled()){
            bluetoothAdapter.disable();
            Toast.makeText(this,"Bluetooth is Disabled..",Toast.LENGTH_SHORT).show();
        }
    }


    // Turning ON the Bluetooth or if the Bluetooth is already ON, this function will be called
    private void Bluetooth_Status_ON() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this,"BLUETOOTH does not support on this Device..",Toast.LENGTH_SHORT).show();
            // Bluetooth is not supported by the Device...
        }
        else {
            if (!bluetoothAdapter.isEnabled()) {  /* If this condition return TRUE, means Bluetooth is already ENABLED. If Bluetooth is
                                                    disabled, ! this sign converts the status in opposite sign. And after that we are
                                                    writing the code for enabling the BLUETOOTH, if it is disabled..*/

                Intent intent_1 = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);   /* If the bluetooth is off, ACTION_REQUEST_ENABLE
                                                                                            turns the Bluetooth ON...*/
                startActivityForResult(intent_1, 1);
            }
            else{
                Paired_Devices();
            }
        }
    }


    // Make the Device Discoverable to other Device, So that they both can share the data with each Other
    private void enable_Discoverability(){  /*Enable the Discoverability of Device for make the CONNECTION b/w devices */
        Intent discoverableIntent= new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,10);   /* Device can be discoverable only for
                10 seconds. By Default, Discovarable Time is 120 Seconds... */
        startActivity(discoverableIntent);
    }


    // Get the list of Paired Devices
    private void Paired_Devices() {
        Set<BluetoothDevice> paired_devices= bluetoothAdapter.getBondedDevices();
            /* getBoundedDevices() function collect the information of available devices. And return the Set of these available devices. */
        String[] str= new String[paired_devices.size()]; /* It is for store the available devices name */
        btArray= new BluetoothDevice[paired_devices.size()];
        int index=0;

        if(paired_devices.size()>0) {
            for(BluetoothDevice device : paired_devices) {    /* device variable take the device from available_devices variable(it
                                                                    stores the set of available Devices), and then we take the name of
                                                                    the device by using getName() function and store that in String Array */
                btArray[index]= device;
                str[index]=device.getName()+"\n"+device.getAddress();
                index++;
            }
            ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, str);
            /*arrayAdapter taking the values from str Array and after getting all the values from Array, it will show those in the list
                And then if we tap on any icon of list then that should be selected..*/
            devices_list.setAdapter(arrayAdapter);  // Set the values of arrayAdapter into LIST...
        }
    }


    // These attributes are used for getting the nearby Available Devices
    ArrayAdapter<String> adapter;
    ArrayList<String> stringArrayList;

    // Getting the list of Available Devices
    private void Available_Devices(){
        if(bluetoothAdapter.isDiscovering()){   //If Bluetooth is already in Discovering Mode. Then, first stop the discovery...
            bluetoothAdapter.cancelDiscovery();
        }
        stringArrayList= new ArrayList<>();
        bluetoothAdapter.startDiscovery();
        Toast.makeText(getApplicationContext(),"Searching Available Devices",Toast.LENGTH_LONG).show();
        IntentFilter intentFilter= new IntentFilter(BluetoothDevice.ACTION_FOUND);

        registerReceiver(broadcastReceiver,intentFilter);

        adapter= new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);
        available_devices_list.setAdapter(adapter);

        if(stringArrayList.isEmpty()){
            Toast.makeText(getApplicationContext(),"No Available Devices Found",Toast.LENGTH_SHORT).show();
        }
    }

    private final BroadcastReceiver broadcastReceiver= new BroadcastReceiver() {
        /* In order to receive information about each device discovered, our application must register a BroadcastReceiver for
        the ACTION_FOUND intent.*/
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(getApplicationContext(),"Broadcast Receiver",Toast.LENGTH_SHORT).show();
            String action= intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                // Discovery has found a device. Get the Bluetooth Device object and its info from the Intent.
                BluetoothDevice device= intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                stringArrayList.add(device.getName()+"\n"+device.getAddress());
                adapter.notifyDataSetChanged(); // It notifies the Adapter that Data is stored in Array.
            }
        }
    };
}