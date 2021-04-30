package com.example.bluetoothbasedchattingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class activity_3 extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;

    ClientThread clientThread;
    ServerThread serverThread;
    SendReceive sendReceive;

    public static final String TAG= "MyActivity_Debug_Tag";

    Intent intent4;
    public static String uid="UUID_of_The_Device";
//    public static String ST="Server Thread";
//    public static String CT="Client Thread";
    public static String TSV="Thread Slection Variable";

    private final static UUID My_UUID=UUID.fromString("39bfa716-88ec-11eb-8dcd-0242ac130003");

    public static final int state_Listening=1;
    public static final int state_Connecting=2;
    public static final int state_Connected=3;
    public static final int state_Connection_Failed=4;
    public static final int state_Message_Received=5;
    public static final int state_Message_Sended=6;

    private static final String App_Name="BlueChat";

    private TextView status;
    private Button send;
    private EditText input_msg;
    private TextView username;
    private ListView chat;
    private ArrayAdapter<String> main_Chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        intent4= getIntent();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initialize_The_Fields();
        start_the_Threads_With_NULL();
//        Toast.makeText(getApplicationContext(),"All the Fields are initialized and Assigning the Null Values into Threads Successfully",Toast.LENGTH_SHORT).show();
//        My_UUID= UUID.fromString(intent4.getStringExtra(uid));
//        bluetoothDevice= intent4.getExtras().getParcelable("BD");

        if(intent4.getStringExtra(TSV).equals("Server_Thread")) {
//            Toast.makeText(getApplicationContext(), intent4.getStringExtra(TSV)+ "calling", Toast.LENGTH_SHORT).show();
            serverThread= new ServerThread();
//            Toast.makeText(getApplicationContext(),"Server Thread Object",Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Server Thread Object Successfully Created");
            serverThread.start();
        }

        if(intent4.getStringExtra(TSV).equals("Client_Thread")) {
//            bluetoothDevice= intent4.getExtras().getParcelable("BD");
            bluetoothDevice= Activity_2.device;
//            Toast.makeText(getApplicationContext(), intent4.getStringExtra(TSV)+" "+bluetoothDevice, Toast.LENGTH_SHORT).show();
            // getParcelable()Using Bundle to pass data between Android components.
            // The Bundle object which is used to pass data to Android components is a key/value store for specialized objects.
            clientThread= new ClientThread(bluetoothDevice);
            Log.i(TAG, "Client Thread Object Successfully Created");
            clientThread.start();
        }

//        String intent_Value= str.substring(str.length()-17);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str= String.valueOf(input_msg.getText());
//                if(str.getBytes().length!=0){
                if(!str.isEmpty()){
                    sendReceive.write(str.getBytes());
                    input_msg.setText(null);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        start_the_Threads_With_NULL();
//        Toast.makeText(getApplicationContext(),"All the Fields are deleted Successfully", Toast.LENGTH_SHORT).show();
    }

    private void initialize_The_Fields() {
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        status= (TextView) findViewById(R.id.User_Status_Server);
        send= (Button) findViewById(R.id.send_The_Msg);
        input_msg= (EditText) findViewById(R.id.msg_Input);
        username= (TextView) findViewById(R.id.UserName);
        username.setText("BlueChat");
        chat= (ListView) findViewById(R.id.Users_Chat);
        main_Chat= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        chat.setAdapter(main_Chat);
//        Toast.makeText(getApplicationContext(),"Initialize the Fields",Toast.LENGTH_SHORT).show();
    }

    public synchronized void start_the_Threads_With_NULL() {
        if(clientThread!=null){
            clientThread.cancel();
            clientThread= null;
        }

        if(serverThread!=null){
            serverThread.cancel();
            serverThread= null;
        }

        if(sendReceive!=null){
            sendReceive.cancel();
            sendReceive= null;
        }
//        Toast.makeText(getApplicationContext(),"Assigning the NULL Values into Threads",Toast.LENGTH_SHORT).show();
        handler.obtainMessage(state_Connection_Failed).sendToTarget();
    }



    /* A Handler allows you to send and process Message. Each Handler instance is associated with a single thread and
    that thread's message queue. When you create a new Handler it is bound to a Looper.
    handler that gets info from Bluetooth service */
    Handler handler= new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            // Let's check the type of Message
            // "what" is of int type
            switch (msg.what){
                case state_Listening:
                    status.setText("Listening");
                    Toast.makeText(getApplicationContext(),"Listening",Toast.LENGTH_SHORT).show();
                    break;
                case state_Connecting:
                    status.setText("Connecting");
                    Toast.makeText(getApplicationContext(),"Connecting",Toast.LENGTH_SHORT).show();
                    break;
                case state_Connected:
                    status.setText("Connected");
                    Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
                    break;
                case state_Connection_Failed:
                    status.setText("Connection Failed");
                    Toast.makeText(getApplicationContext(),"Not Connected",Toast.LENGTH_SHORT).show();
                    break;
                case state_Message_Received:
                    // Here we will receive the message. (Chat)
                    byte[] read_Buffer= (byte[]) msg.obj;
                    String temp_Msg= new String(read_Buffer,0,msg.arg1);
                    main_Chat.add(username.getText()+" : "+temp_Msg);
                    break;
                case state_Message_Sended:
                    byte[] user_buffer= (byte[]) msg.obj;
                    String user_Msg= new String(user_buffer);
                    main_Chat.add("Me : "+user_Msg);
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"No device found",Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    });

    // Server thread
    public class ServerThread extends Thread {
        private final BluetoothServerSocket serverSocket;   //BluetoothServerSocket is used to send or receive the data
        public ServerThread() {
            // Use a temporary object that is later assigned to serverSocket because serverSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp= bluetoothAdapter.listenUsingRfcommWithServiceRecord(App_Name, My_UUID);
            }
            catch (IOException e) {
                // Socket's listen() method failed
                Log.e(TAG,"Socket's listen() method failed",e);
            /* The printStackTrace() method in Java is a tool used to handle exceptions and errors. It is a method of
             Java's throwable class which prints the throwable along with other details like the line number and class
             name where the exception occurred. printStackTrace() is very useful in diagnosing exceptions. */
            }
            serverSocket= tmp;
//            Toast.makeText(getApplicationContext(),"Constructor of Server Thread", Toast.LENGTH_SHORT).show();
        }
        public void run() {
            BluetoothSocket socket= null;

//            Keep listening until exception occurs or a socket is returned.
            while(socket==null) {
                try {
                    Message msg= Message.obtain();
                    msg.what= state_Connecting;
                    handler.sendMessage(msg);
                    socket= serverSocket.accept();
                }
                catch (IOException e) {
//                    Socket's accept() method failed
                    try{
                        serverSocket.close();
                    } catch (IOException e1){
                        Log.e(TAG,"Close the ServerSocket Thread",e1);
                    }
                    Message msg= Message.obtain();
                    msg.what= state_Connection_Failed;
                    handler.sendMessage(msg);
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if(socket!=null) {
                    // A connection was accepted. Perform work associated with the connection in a separate thread.
                    Message msg= handler.obtainMessage(state_Connected);
                    handler.sendMessage(msg);

                    sendReceive= new SendReceive(socket);
                    sendReceive.start();

//                    Now its time to write some code for send/recieve data...
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error in ServerThread's run() Method, when socket is NULL");
                    }
                }
            }
        }
        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                serverSocket.close();
            }
            catch(IOException e) {
                Log.e(TAG,"Could not close the connect Socket",e);
            }
        } 
    }

    // Client Thread
    public class ClientThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ClientThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(My_UUID);
            }
            catch (IOException e) {
                // Socket's create() method failed
                Log.e(TAG, "Error in ClientThread's Constructor");
            }
            mmSocket = tmp;
//            Toast.makeText(getApplicationContext(),"Constructor of Client Thread",Toast.LENGTH_SHORT).show();
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                Message msg1= handler.obtainMessage(state_Connecting);
                handler.sendMessage(msg1);

//                Connect to the remote device through the socket. This call blocks until it succeeds or throws an exception.
                mmSocket.connect(); // It connect the Client Device with the Server Device

                Message msg= handler.obtainMessage(state_Connected);
                handler.sendMessage(msg);
                sendReceive= new SendReceive(mmSocket);
                sendReceive.start();
            }

            catch (IOException e) {
                Message msg= handler.obtainMessage(state_Connection_Failed);
                handler.sendMessage(msg);
                Log.e(TAG, "Error in ClientThread's run() Method");

//                Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                }
                catch (IOException closeException) {
                    // Could not close the client socket
                    Log.e(TAG,"Could not close the client socket",closeException);
                }
            }
//            The connection attempt succeeded. Perform work associated with the connection in a separate thread.

        }
        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();   // closes the connected socket and release all related internal resources
            }
            catch (IOException e) {
                // Could not close the client socket
                Log.e(TAG, "Could not close the client socket", e);
//                e.printStackTrace();
            }
        }
    }

    public class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            bluetoothSocket= socket;
            InputStream tmp_In= null;
            OutputStream tmp_Out= null;
            username.setText(bluetoothSocket.getRemoteDevice().getName()); //TODO this line gives error in Galaxy J2

//          Get the input and output streams using temp objects because member streams are final
            try {
                tmp_In = bluetoothSocket.getInputStream();
                tmp_Out = bluetoothSocket.getOutputStream();
            }
            catch(IOException e) {
                Log.e(TAG, "Error occurred when creating input or output stream",e);
            }
            inputStream= tmp_In;
            outputStream= tmp_Out;
//            Toast.makeText(getApplicationContext(),"Constructor of SendReceive Class", Toast.LENGTH_SHORT).show();
        }

        public void run() {
            //buffer store for the stream
            byte[] buffer = new byte[1024];
            int bytes; //bytes returned from read()

//          Keep listening to the InputStream until an exception occurs
            while(true) // Infinite loop is because we are always ready to receive the message
            {
                try {
                    // Read from the InputStream.
                    bytes= inputStream.read(buffer); // bytes contain the number of bytes or size of Buffer in bytes
                    Message readMsg= handler.obtainMessage(state_Message_Received, bytes,-1, buffer);
                    readMsg.sendToTarget();
                }
                catch (IOException e) {
                    Log.e(TAG, "Input stream was disconnected",e);
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
                handler.obtainMessage(state_Message_Sended, -1, -1, bytes).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Couldn't send data to the other device",e);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                bluetoothSocket.close();
                handler.obtainMessage(state_Connection_Failed).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
