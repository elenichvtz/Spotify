package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import android.media.MediaPlayer;

import android.os.AsyncTask;

import android.os.Bundle;

import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.KeyEvent;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View.OnKeyListener;
import android.view.View;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.lang.*;


public class MainActivity extends AppCompatActivity implements Serializable {
    private Button button;
    private EditText txt_input;
    private TextView lb1_output;

    private TextWatcher text =null;
    private TextKeyListener textKeyListener = null;
    static int portOfBroker;
    private String ip;
    private int port;
    int exist;

    static boolean flag_artist = false;
    static ArrayList<String> listofsongs = new ArrayList<String>();
    static String songSelected;
    static  boolean flag_song = false;
    Intent intent;
    public static final int LAUNCH_ACTIVITY = 1;
    static String message;
    static Socket requestSocket = null;
    static ObjectOutputStream out = null;
    static ObjectInputStream in = null;
    static ArrayList<Value> pieces1;
    static String name;
    MediaPlayer player;
    static final long serialVersionUID = -373782829391231342L;
    File fis;
    Switch simpleSwitch;
    static boolean choice;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_input = (EditText) findViewById(R.id.txt_input);
        lb1_output = (TextView) findViewById(R.id.textView1);
        simpleSwitch = (Switch) findViewById(R.id.simpleSwitch);// initiate Switch
        button = (Button)findViewById(R.id.button4);
        simpleSwitch.setTextSize(25);
        //button = (Button) findViewById(R.id.button4);
        System.out.println("Fifth");

    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == LAUNCH_ACTIVITY) {
            if(resultCode == ListOfSongs.RESULT_OK){

                System.out.println("Fourth");
                message = "" + intent.getStringExtra(ListOfSongs.EXTRA_MESSAGE);
                System.out.println("Song'name that has been selected is "+ message);
                System.out.println("sdfkshfuwefuh    " + flag_song);
                Intent intent2 = new Intent(MainActivity.this, PlaySong.class);
                startActivity(intent2);
                //AsyncTaskRunner1 run1 = new AsyncTaskRunner1();
               // run1.execute();

            }
            if (resultCode == ListOfSongs.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


    public void onStart() {
        super.onStart();
        System.out.println("FLAG : " + flag_artist);

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    choice = true;
                    System.out.println("Online");
                } else {
                    choice = false;
                    System.out.println("Offline");
                }
            }
        });
       // ArtistName artist = new ArtistName(txt_input.toString());
        txt_input.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //...
                    // Perform your action on key press here
                    // ...
                    txt_input = (EditText) findViewById(R.id.txt_input);
                    System.out.println("Third");

                    AsyncTaskRunner runner = new AsyncTaskRunner();
                    runner.execute();

                    return true;
                }
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, Downloads.class);
                startActivity(intent2);
            }
        });

    }


    public class AsyncTaskRunner extends AsyncTask<String,String,String> implements Serializable {
        static final long serialVersionUID = -373782829391231342L;
        private String resp;
        ProgressDialog progressDialog;
        int exist;
        ArtistName artist = new ArtistName(txt_input.getText().toString());
        @SuppressLint("WrongThread")
        protected String doInBackground(String... params) {
            System.out.println("artist:" + artist.getArtistName());
            publishProgress("Sleeping...");
            try {
                System.out.println("Second");
                System.out.println("Inside doInBackground");
                requestSocket = new Socket("192.168.1.15", 7655);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                portOfBroker = 7655;
                out.writeUTF("192.168.1.15");
                out.writeInt(7655);
                out.writeUTF(artist.getArtistName());
                out.flush();
                int brokerport = in.readInt();
                System.out.println("..... doInBackground");
                System.out.println("Brokerport: " + brokerport);
                if (brokerport != 7655) {
                    portOfBroker = brokerport;
                    requestSocket.close();
                    Socket request = new Socket("192.168.1.15", brokerport+1);
                    out = new ObjectOutputStream(request.getOutputStream());
                    in = new ObjectInputStream(request.getInputStream());

                    out.writeUTF("192.168.1.15");
                    out.writeInt(brokerport);
                    out.writeUTF(artist.getArtistName()); //successfully sends artistName to BrokerNode
                    out.flush();
                    System.out.println("Connect with other broker");
                }
                int port = in.readInt();
                exist = in.readInt();
                System.out.println("exist: " + exist);
                if(exist!=1){
                    lb1_output.setVisibility(View.VISIBLE);
                    lb1_output.setText("Please insert another artist");
                    artist.setArtistName(txt_input.toString());
                    txt_input.setOnKeyListener(new OnKeyListener() {
                        public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                            //If the keyevent is a key-down event on the "enter" button
                            if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                //...
                                // Perform your action on key press here
                                // ...
                                if(txt_input.getText().toString().trim().equalsIgnoreCase("")){
                                    txt_input.setError("Type an artist");
                                }
                                txt_input = (EditText) findViewById(R.id.txt_input);
                                System.out.println("Third");
                                AsyncTaskRunner runner = new AsyncTaskRunner();
                                runner.execute();
                                return true;
                            }
                            return false;
                        }
                    });
                }

                if (exist == 1) {
                    try {
                        //lb1_output.d
                        lb1_output.setText("");

                        int size = in.readInt();
                        System.out.println("size: "+ size);
                        for(int i=0; i<size;i++) {
                            listofsongs.add(in.readUTF());
                            System.out.println(listofsongs.get(i));

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Intent intent= new Intent(MainActivity.this, ListOfSongs.class);
                    startActivityForResult(intent, LAUNCH_ACTIVITY);

                } else {
                    System.out.println("The artist you searched doesn't exist.");
                    System.out.println("Please try again.");
                }

            }
            catch (IOException e){
                Log.e("MessageSender", "" + e);

            }
            return null;
        }


    }



    }







