package com.example.aviral.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by aviral on 6/8/15.
 */
public class Home extends Fragment {

      TextView txtView;
      ImageView img;

      @Override
      public View onCreateView ( LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState){
          View rootview = inflater.inflate(R.layout.fragment_home, container, false);
          Bundle args = new Bundle();

          txtView = (TextView) rootview.findViewById(R.id.txtView);
          img = (ImageView) rootview.findViewById(R.id.mimg);

          return rootview;
      }


}
