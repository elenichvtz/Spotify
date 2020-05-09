package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.view.KeyEvent;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import android.view.View.OnKeyListener;
import android.view.View;



import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    Socket requestSocket = null;
    private EditText txt_input;
    private TextView lb1_output;

    private TextWatcher text =null;
    private TextKeyListener textKeyListener = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_input = (EditText) findViewById(R.id.txt_input);
        txt_input.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //...
                    // Perform your action on key press here
                    // ...
                    Intent k = new Intent(MainActivity.this, ListOfSongs.class);
                    startActivity(k);
                    //ListView lv = getListView();
                    //lv.setTextFilterEnabled(true);
                    return true;
                }
                return false;
            }
        });

       // textKeyListener = new TextKeyListener(dhdh, hdhd)
        /*EditText editText = (EditText) findViewById(R.id.txt_input);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    //sendMessage();
                    handled = true;
                }
                return handled;
            }
        });*/
    }
    //artist.setOnEditorActionListener("Type an artist", , KeyEvent.KEYCODE_ENTER);
}


