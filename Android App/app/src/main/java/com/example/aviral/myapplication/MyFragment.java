package com.example.aviral.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by aviral on 6/7/15.
 */
public class MyFragment extends android.support.v4.app.Fragment{

    public static final String ARG_OBJECT = "object";
    TextToSpeech ttobj;
    private EditText write;
    Button myBtn;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
        Bundle args = getArguments();
        int pos = getArguments().getInt(ARG_OBJECT);

        write = (EditText) rootView.findViewById(R.id.editText1);
        ttobj = new TextToSpeech(getActivity().getApplicationContext(),new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status){
                if (status!= TextToSpeech.ERROR){
                    ttobj.setLanguage(Locale.US);
                }
            }
        });
        myBtn = (Button) rootView.findViewById(R.id.button1);

        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakText(view);
            }
        });


        return rootView;

    }

    public void speakText (View view){

        String toSpeak = write.getText().toString();
        Toast.makeText(getActivity().getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
        ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    public void onPause(){

        if (ttobj!= null){
            ttobj.stop();
            ttobj.shutdown();
        }
        super.onPause();
    }




}

