package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;



public class ListOfSongs extends AppCompatActivity implements Serializable {
    ListView songlist;
    TextView txt;
    static String itemValue;
    //Button button;
    Intent intent;
    public static final String EXTRA_MESSAGE = "com.example.spotify.MESSAGE";
    public static final int RESULT_OK = 1;
    public static  int RESULT_CANCELLED = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_songs);
        intent = new Intent(ListOfSongs.this, MainActivity.class);

        /*button =  findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListOfSongs.this, PlaySong.class);
                startActivity(intent);
            }
        });*/


    }

    public void onStart() {
        super.onStart();
        MainActivity c = new MainActivity();
        ArrayList<String> songs = new ArrayList<>();
        songlist = (ListView) findViewById(R.id.listview);


        for (int i = 0; i < c.listofsongs.size(); i++) {
            songs.add(c.listofsongs.get(i));
            System.out.println("Songs: " + c.listofsongs.get(i));
        }
        if (c.listofsongs == null) System.out.println("NULL");

        ArrayAdapter arrayAdapter = new ArrayAdapter(ListOfSongs.this, android.R.layout.simple_list_item_1, songs);

        songlist.setAdapter(arrayAdapter);
        songlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               // AsyncTaskRunner1 runner = new AsyncTaskRunner1();
                itemValue = (String) songlist.getItemAtPosition(position);
              //  Log.e("Send song with name: ", itemValue);
              //  runner.execute();
                //Intent intent1 = new Intent();
                intent.putExtra(EXTRA_MESSAGE, itemValue);

                setResult(ListOfSongs.RESULT_OK, intent);
                //MainActivity m = new MainActivity();
                //m.flag_song = true;
                finish();
                //startActivity(intent1);
            }

        });
    }

}
