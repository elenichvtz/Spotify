package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
    private Spinner dropdown;
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

        dropdown = findViewById(R.id.spinner1);
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
                    txt_input = (EditText) findViewById(R.id.txt_input);
                    System.out.println("11111111111  "+ " " + txt_input.getText().toString());
                    AsyncTaskRunner runner = new AsyncTaskRunner();
                    runner.execute();
                    System.out.println("FLAG : " + flag_artist);
                    if(flag_artist==true){

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






    private class AsyncTaskRunner extends AsyncTask<String,String,String> {

        private String resp;
        ProgressDialog progressDialog;
        //System.out.println("artist:");
        ArtistName artist = new ArtistName(txt_input.getText().toString());
        String[] params = new String[2];


        protected String doInBackground(String... params) {
            System.out.println("artist:" + artist.getArtistName());


            publishProgress("Sleeping...");
            try {
              // int time = Integer.parseInt(params[0])*1000;
                //Thread.sleep(time);

                Socket requestSocket = null;
                ObjectOutputStream out = null;
                ObjectInputStream in = null;

                System.out.println("Inside doInBackground");
                requestSocket = new Socket("192.168.1.3", 7655);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());
                //Thread.sleep(10);


                out.writeUTF("192.168.1.3");
                out.writeInt(7655);
                out.writeUTF(artist.getArtistName());
                out.flush();


                int brokerport = in.readInt();
               // String ip = in.readUTF();
                System.out.println("Port of broker: " + brokerport);
                System.out.println("..... doInBackground");
                if (brokerport != 7655) {

                    requestSocket.close();

                    requestSocket = new Socket("192.168.1.3", brokerport);

                    out.writeUTF("192.168.1.3");
                    out.writeInt(brokerport);

                    out.writeUTF(artist.getArtistName()); //successfully sends artistName to BrokerNode
                    out.flush();
                    System.out.println("Connect with other broker");
                }

                exist = in.readInt();
                if (exist == 1) {
                    try {
                        listofsongs = (ArrayList<String>) in.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
      //  @Override
        protected void onPostExecute(ArrayAdapter adapter) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
           // dropdown = listofsongs;
            dropdown.setVisibility(Spinner.VISIBLE);
            dropdown.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Searching for artist",
                    "Still searching...");
        }

       /* @Override
        protected void onProgressUpdate(String... text) {
            finalResult.setText(text[0]);
        }*/



    }

}


