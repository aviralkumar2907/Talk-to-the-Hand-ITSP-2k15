package com.example.aviral.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.UUID;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by aviral on 6/8/15.
 */
public class BluetoothTTS extends Fragment implements SpellCheckerSession.SpellCheckerSessionListener{


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
        TextView txtReceived, txtraw;
        Button TTSbtn, StartBtn, startGes;
        private StringBuilder data = new StringBuilder();
        private StringBuilder dataDTW = new StringBuilder(62);
        private String data1 = new String("");
        TextToSpeech ttsObj;
        private SpellCheckerSession mScs;
        String withSpaces, newone= "";
        DTW[] template =new DTW[]  {new DTW(316/4, 286/4, 289/4), new DTW(316/4, 284/4, 290/4), new DTW (316/4, 284/4, 290/4), new DTW(315/4, 283/4, 289/4),
                        new DTW(314/4,284/4, 288/4), new DTW(302/4, 289/4, 295/4), new DTW(297/4, 292/4, 298/4), new DTW(295/4, 289/4, 298/4), new DTW(292/4, 289/4, 300/4),
                        new DTW(307/4, 306/4, 317/4), new DTW(359/4, 363/4, 257/4), new DTW(358/4, 362/4, 238/4), new DTW(337/4, 379/4, 290/4), new DTW(331/4, 378/4, 281/4),
                        new DTW(332/4, 387/4, 284/4), new DTW(332/4, 387/4, 284/4), new DTW(333/4, 382/4, 282/4), new DTW(331/4, 382/4, 284/4), new DTW(332/4,382/4, 284/4),
                        new DTW(331/4, 382/4, 284/4)};

        DTW mean = new DTW(310/4, 290/4, 287/4);
        String[] corresponding = {"z", "j"};
    int flag=0;

        @Override
        public void onGetSuggestions(final SuggestionsInfo[] arg0) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arg0.length; ++i) {
                // Returned suggestions are contained in SuggestionsInfo
                final int len = arg0[i].getSuggestionsCount();



                for (int j = 0; j < len; ++j) {
                    sb.append(arg0[i].getSuggestionAt(j));
                }

            }

            final Runnable setupView = new Runnable() {

                @Override
                public void run() {
                    newone = sb.toString();
                    Log.i("TAxdgfd", newone);
                    txtReceived.setText(newone);
                    String toSpeak = newone;
                    Toast.makeText(getActivity().getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                    ttsObj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }

            };
            setupView.run();

        }

        public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public View onCreateView ( LayoutInflater inflater,
                                      ViewGroup container, Bundle savedInstanceState){

            View rootview = inflater.inflate(R.layout.activity_bluetooth_tts, container, false);
            Bundle args = new Bundle();

            txtReceived = (TextView) rootview.findViewById(R.id.mText);
            TTSbtn = (Button)rootview.findViewById(R.id.btn);
            //StartBtn = (Button) rootview.findViewById(R.id.start);
           // startGes = (Button) rootview.findViewById(R.id.start2);
            txtraw = (TextView) rootview.findViewById((R.id.txt2));
            ttsObj = new TextToSpeech(getActivity().getApplicationContext(),new TextToSpeech.OnInitListener(){

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
                           Log.i("Handler", "Got here");

                        String read = new String(buff, 0, i);
                        if (read.length() >=1 && read.charAt(0) == ' ' || flag==1) {

                            Log.i("TAG", "Hi dude");
                             if(read.charAt(read.length()-1)== '&') {
                                 dataDTW.append(read);
                                 Log.i("RAG", dataDTW.toString());
                                 flag = 0;
                                 DTW[] temp = findDTWfromData(dataDTW.toString());
                                 DTW[] accelerometer = finalDTWarray(temp);
                                 DTW[] ftemplate = finalDTWarray(template);
                                 Log.i("TAG", "hey");
                                 //for (i = 0; i < template.length; i++) {
                                 { Log.i("TAG", "Entered the loop");
                                     DTWMatrix Matrix = new DTWMatrix(accelerometer, ftemplate);   //template z and j separately
                                     Log.i("TAG", "not doing thee DTWMatrix");
                                     Matrix.fillDTWMatrix();
                                     Log.i("TAG", "DTW Matrix filled ");
                                     if (Matrix.findAndCheckMinPath())
                                         data.append("j");
                                     else data.append("i");
                                     dataDTW.delete(0,dataDTW.length());

                                 }
                             }
                            else if(read.length() >=1 && read.charAt(0) == ' '){
                                dataDTW.append(read);
                                Log.i("FIrst", "first case");
                                flag=1;
                            }
                            else if(read.charAt(read.length()-1)!='&') {
                                dataDTW.append(read);
                                Log.i("Second","Second case");
                                flag=1;
                            }



                            else {}
                        }


                        else {
                            Log.i("TAG", read.length() + "");
                            data.append(read);
                            data1 = read;
                        }

                        int eol = data.indexOf(" ");
                        txtraw.setText(data.toString());

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
                    withSpaces = data.toString();
                    mScs.getSuggestions(new TextInfo(withSpaces), 1);


                    //Log.i("TAG", newone);
                    //Log.i("TAG", data.toString().length()+"");


                    data.delete(0,data.length());
                    data = new StringBuilder("");

                }
            });

            /*StartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mThread.write("x");
                }
            });*/

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
                Toast.makeText(getActivity(), "BLUETOOTH NOT FOUND", Toast.LENGTH_SHORT);
                getActivity().finish();
                return null;
            }

            if (!btAdapter.isEnabled()) {
                Toast.makeText(getActivity(),
                        "Please enable your BT and re-run this program.",
                        LENGTH_LONG).show();
                getActivity().finish();
                return null;
            }

            if (D)
                Log.e(TAG, "+++ DONE IN ON CREATE, GOT LOCAL BT ADAPTER +++");
                return rootview;
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
            final TextServicesManager tsm = (TextServicesManager) getActivity().getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
            mScs = tsm.newSpellCheckerSession(null, null, this, true);
        }

        @Override
        public void onPause()
        {
            if (ttsObj!= null){
                ttsObj.stop();
                ttsObj.shutdown();
            }
            super.onPause();
            if (mScs != null) {
                mScs.close();
            }
            try
            {
                //Don't leave Bluetooth sockets open when leaving activity
                btSocket.close();
            } catch (IOException e2) {
                Log.e(TAG, "onPause - Connection Paused");
            }

        }


        public DTW[] findDTWfromData(String input){
            DTW[] temp = new DTW[input.length()/3];
            int flag=0;
            for(int i=1; i<input.length()-1 ; ){
                DTW a;
                if(input.charAt(i)==' ') {i++; flag= 1; continue;}

                Log.i("Value of charAt", Integer.toString((int)input.charAt(i)));
                a = new DTW((int)input.charAt (i)>0 ? (int)input.charAt(i): 255+ (int)input.charAt(i), (int)input.charAt (i+1)>0 ? (int)input.charAt(i+1): 255+ (int)input.charAt(i+1), (int)input.charAt (i+2)>0 ? (int)input.charAt(i+2): 255+ (int)input.charAt(i+2));
                Log.i("Tag-a", Integer.toString(a.acc[0]));
                Log.i("Tag-a", Integer.toString(a.acc[1]));
                Log.i("Tag-a", Integer.toString(a.acc[2]));
                temp[i/3] = a;
                i+=3;
            }
            Log.i("Temp-findDTWfromData", Integer.toString(temp.length));
            return temp;


        }


        public DTW[] finalDTWarray(DTW[] a){
            //DTW[] temp = new DTW[]();
            int startpos=0, endpos=a.length-1;
            for(int i=0; i<a.length; i++){
                if(a[i].acc[0] - mean.acc[0] > 6 || a[i].acc[1] - mean.acc[1] > 6 || a[i].acc[2] - mean.acc[2] > 6){
                    startpos = i;
                    break;
                }
            }
            Log.i("finalDTWArray", Integer.toString(startpos));
            for(int i= a.length-1; i>=0; i--){
                if(a[i].acc[0] - mean.acc[0] > 6 || a[i].acc[1] - mean.acc[1] > 6 || a[i].acc[2] - mean.acc[2] > 6){
                    endpos = i;
                    break;
                }
            }
            DTW[] temp = new DTW[endpos-startpos+1];
            System.arraycopy(a, startpos, temp, 0, endpos-startpos+1);
            return temp;

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
                    mhandler.obtainMessage(0, buffer).sendToTarget();
                    buffer = new byte[1024];
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


