package com.example.aviral.bluetoothtts;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.UUID;

//package com.example.aviral.bluetoothactivity;


import android.os.Handler;


public class MainActivity extends Activity {

    private BluetoothAdapter btAdapter = null;
    private static boolean D = true;
    private BluetoothSocket btSocket = null;
    private String TAG="BLUETOOTH";
    private  android.os.Handler mhandler;
    final int handlerState = 0;
    private ConnectedThread mThread;
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "98:D3:31:60:15:97";
    TextView txtReceived;
    Button TTSbtn;
    private StringBuilder data = new StringBuilder();
    private String data1 = new String("");
    TextToSpeech ttsObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtReceived = (TextView) findViewById(R.id.mText);
        TTSbtn = (Button)findViewById(R.id.btn);
        ttsObj = new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status){
                if (status!= TextToSpeech.ERROR){
                    ttsObj.setLanguage(Locale.US);
                }
            }
        });

        mhandler = new android.os.Handler() {
            @Override
            public void handleMessage (Message msg){
                if(msg.what == handlerState){
                    byte[] buff = (byte[])msg.obj;
                    Log.i("TAG:,", buff.length+"");
                    int i;
                    for ( i=0; buff[i]!=0; i++);

                    String read = new String(buff, 0, i);
                    Log.i("TAG", read.length()+"");
                    data.append(read);
                    data1=read;

                    int eol = data.indexOf(" ");
                    /*if(eol>0){
                        String dataprint = data.substring(0, eol);*/

                        /*data.delete(0, eol);

                    }*/
                }
            }


        };

        TTSbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtReceived.setText(data.toString());
                Log.i("TAG", data.toString());
                Log.i("TAG", data.toString().length()+"");
                String toSpeak = data.toString();

                data.delete(0,data.length());
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                ttsObj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

       /* mhandler = new Handler() {
            @Override
            public void close() {

            }

            @Override
            public void flush() {

            }

            @Override
            public void publish(LogRecord logRecord) {

            }
        }*/

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth is not available.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!btAdapter.isEnabled()) {
            Toast.makeText(this,
                    "Please enable your BT and re-run this program.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (D)
            Log.e(TAG, "+++ DONE IN ON CREATE, GOT LOCAL BT ADAPTER +++");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (D) {
            Log.e(TAG, "+ ON RESUME +");
            Log.e(TAG, "+ ABOUT TO ATTEMPT CLIENT CONNECT +");
        }

        // When this returns, it will 'know' about the server,
        // via it's MAC address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "ON RESUME: Socket creation failed.", e);
        }

        btAdapter.cancelDiscovery();

        // Blocking connect, for a simple client nothing else can
        // happen until a successful connection is made, so we
        // don't care if it blocks.
        try {
            btSocket.connect();
            Log.e(TAG, "ON RESUME: BT connection established, data transfer link open.");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.e(TAG,
                        "ON RESUME: Unable to close socket during connection failure", e2);
            }
        }

        if (D)
            Log.e(TAG, "+ ABOUT TO SAY SOMETHING TO SERVER +");

        mThread = new ConnectedThread(btSocket, mhandler);
        mThread.start();
        mThread.write("x");
    }

    @Override
    public void onPause()
    {
        if (ttsObj!= null){
            ttsObj.stop();
            ttsObj.shutdown();
        }
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            Log.e(TAG, "onPause - Connection Paused");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    android.os.Handler mhandler;

    public ConnectedThread(BluetoothSocket socket, android.os.Handler h) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mhandler = h;
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("Bluetooth", "Error in getting I/o Streams");

        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        //Handler mhandler;
        //int handlerState =0;
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                String recvd;
                bytes = mmInStream.read(buffer);
                recvd = Integer.toString(bytes);
                String str="";
                for (int i=0; i<bytes; i++){
                     str += Byte.toString(buffer[i]);
                }
                Log.i("Bluetooth", recvd);
                Log.i("Bluetooth", str);
                // Send the obtained bytes to the UI activity
                mhandler.obtainMessage(0, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e("Bluetooth","IO error in reading" );
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String input) {
        byte[] bytes = input.getBytes();
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e("Bluetooth", "Error in writing bytes ");
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}