package com.directions.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.directions.sample.R;
import com.directions.sample.model.SoundManager;

/**
 * Created by RON on 10/15/2015.
 */
public class AboutFragment extends Fragment {
    private SoundManager mSoundManager;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.about, container, false);
        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getActivity().getBaseContext());
        mSoundManager.addSound(1, R.raw.click);
        Button lienhe=(Button)layout.findViewById(R.id.btn_contact);
        lienhe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoundManager.playSound(1);
                Intent i=new Intent("com.directions.sample.CONTACTACTIVITY");
                startActivity(i);
            }
        });
        return layout;
    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
