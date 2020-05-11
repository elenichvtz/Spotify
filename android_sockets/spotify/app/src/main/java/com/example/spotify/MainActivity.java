package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.KeyEvent;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import android.view.View.OnKeyListener;
import android.view.View;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private EditText txt_input;
    private TextView lb1_output;
    Socket requestSocket = null;
    private TextWatcher text =null;
    private TextKeyListener textKeyListener = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    private String ip;
    private int port;
    int exist;
    static boolean flag_artist = false;
    ArrayList<String> listofsongs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_input = (EditText) findViewById(R.id.txt_input);
       //this.ip = "127.0.0.1";
        //this.port = 7654;
        //flag_artist = true;
        // init();




    }

    public void onStart() {
        super.onStart();


        System.out.println("FLAG : " + flag_artist);

        ArtistName artist = new ArtistName(txt_input.toString());



        txt_input.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //...
                    // Perform your action on key press here
                    // ...
                   // ArtistName artistName = new ArtistName(txt_input.toString());

                    AsyncTaskRunner runner = new AsyncTaskRunner(requestSocket,out, in, artist,"127.0.0.4", 7654, 7654 );

                    runner.execute();
                    System.out.println("FLAG : " + flag_artist);
                    if(flag_artist==true){
                        Spinner dropdown = findViewById(R.id.spinner1);
                        //create a list of items for the spinner.
                        try {
                            listofsongs = (ArrayList<String>) in.readObject();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                        //There are multiple variations of this, but this is the basic variant.
                        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, listofsongs);
                        //set the spinners adapter to the previously created one.
                        dropdown.setAdapter(adapter);
                        //list of songs of the requested artist


                        System.out.println(listofsongs.toString());
                        dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                Intent i = new Intent(MainActivity.this, PlaySong.class);
                                startActivity(i);
                            }
                        });

                    }
                    else{
                        System.out.println("Pick again");
                    }


                    return true;
                }
                return false;
            }
        });


    }






    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {
        private Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        ArtistName artist;
        int broker;
        String ip;
        int port ;

        AsyncTaskRunner(Socket socket,ObjectOutputStream out, ObjectInputStream in, ArtistName artist, String ip, int port ,int broker ){
            this.requestSocket = socket;
            this.out = out;
            this.in = in;
            this.artist = artist;
            this.ip = ip;
            this.port = port;
            this.broker = broker;
        }


        protected Void doInBackground(Void...voids) {
            try {
                System.out.println("Inside doInBackground");
                this.requestSocket = new Socket(this.ip, this.port + 1);
                System.out.println("..... doInBackground");
                this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
                this.in = new ObjectInputStream(this.requestSocket.getInputStream());
                this.out.writeUTF(ip);
                this.out.writeInt(port);
                this.out.writeObject(artist);
                this.out.flush();



                int brokerport = this.in.readInt();

                if (brokerport != broker) {

                    requestSocket.close();

                    this.requestSocket = new Socket(ip, this.port + 1);

                    this.out.writeUTF(ip);
                    this.out.writeInt(port);

                    this.out.writeObject(artist); //successfully sends artistName to BrokerNode
                    this.out.flush();
                }

                exist = in.readInt();
                if (exist == 1) {
                    flag_artist = true;


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


