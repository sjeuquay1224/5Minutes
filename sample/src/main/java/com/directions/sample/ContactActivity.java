package com.directions.sample;

/**
 * Created by RON on 11/5/2015.
 */

import android.os.Bundle;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.directions.sample.model.SoundManager;

public class ContactActivity extends AppCompatActivity {
    Button buttonSend;
    EditText textTo;
    EditText textSubject;
    EditText textMessage;
    Toolbar toolbar;
    private SoundManager mSoundManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);
        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getBaseContext());
        mSoundManager.addSound(1, R.raw.click);

        buttonSend = (Button) findViewById(R.id.buttonSend);
        //textTo = (EditText) findViewById(R.id.editTextTo);
        textTo=new EditText(this);
        textTo.setText("nguyenhai4001@gmail.com");
        textSubject = (EditText) findViewById(R.id.editTextSubject);
        textSubject.setHintTextColor(getResources().getColor(R.color.primary));
        textMessage = (EditText) findViewById(R.id.editTextMessage);
        textMessage.setHintTextColor(getResources().getColor(R.color.primary));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Gửi ý kiến");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                mSoundManager.playSound(1);
            }
        });
        buttonSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mSoundManager.playSound(1);
                String to = textTo.getText().toString();
                String subject = textSubject.getText().toString();
                String message = textMessage.getText().toString();

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                //email.putExtra(Intent.EXTRA_CC, new String[]{ to});
                //email.putExtra(Intent.EXTRA_BCC, new String[]{to});
                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, message);

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Ch?n lo?i mail mu?n g?i :"));

            }
        });
    }
}