package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.widget.Toast;


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
    static ObjectOutputStream out = null;
    static ObjectInputStream in = null;
    static Socket requestSocket = null;
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
    ListOfSongs l = new ListOfSongs();
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_input = (EditText) findViewById(R.id.txt_input);
        //lb1_output = (TextView)findViewById(R.id.textView);




    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == ListOfSongs.RESULT_OK){
                 songSelected=data.getStringExtra("result");
                 System.out.println("Name of the song: "+ songSelected);

                 lb1_output.setText(songSelected);
                 Log.e("Result",songSelected);
                 Toast.makeText(this, "Result: " + songSelected, Toast.LENGTH_LONG).show();
                 flag_song = true;

            }
            if (resultCode == ListOfSongs.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

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


                   /* String message = "" + secondIntent.getStringExtra("COURSE_SELECTED");
                    System.out.println("Selected song is jhbhjabjaa:      " + songSelected  );

                    TextView myText = (TextView) findViewById(R.id.textView);

                    myText.setText(songSelected);
                    System.out.println("FLAG : " + flag_artist);*/



                    return true;
                }
                return false;
            }
        });

    }

    public class AsyncTaskRunner extends AsyncTask<String,String,String> {

        private String resp;
        ProgressDialog progressDialog;
        String[] params = new String[2];
        int exist;

        ArtistName artist = new ArtistName(txt_input.getText().toString());

        @SuppressLint("WrongThread")
        protected String doInBackground(String... params) {
            System.out.println("artist:" + artist.getArtistName());


            publishProgress("Sleeping...");
            try {
                Socket requestSocket = null;
                ObjectOutputStream out = null;
                ObjectInputStream in = null;

                System.out.println("Inside doInBackground");
                requestSocket = new Socket("192.168.1.3", 7655);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());
                //Thread.sleep(10);

                portOfBroker = 7655;
                out.writeUTF("192.168.1.3");
                out.writeInt(7655);
                out.writeUTF(artist.getArtistName());
                out.flush();

                int brokerport = in.readInt();
                // String ip = in.readUTF();
                System.out.println("Port of broker: " + brokerport);
                System.out.println("..... doInBackground");
                if (brokerport != 7655) {
                    portOfBroker = brokerport;
                    requestSocket.close();

                    Socket request = new Socket("192.168.1.3", brokerport+1);

                    out = new ObjectOutputStream(request.getOutputStream());

                    in = new ObjectInputStream(request.getInputStream());

                    out.writeUTF("192.168.1.3");

                    out.writeInt(brokerport);
                    out.writeUTF(artist.getArtistName()); //successfully sends artistName to BrokerNode

                    out.flush();
                    System.out.println("Connect with other broker");
                }

                exist = in.readInt();
                if (exist == 1) {
                    try {
                        System.out.println("size: "+ 111111111);
                        int size = in.readInt();
                        System.out.println("size: "+ size);
                        for(int i=0; i<size;i++) {
                            listofsongs.add(in.readUTF());
                            System.out.println(listofsongs.get(i));

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent secondIntent= new Intent(MainActivity.this, ListOfSongs.class);
                    startActivity(secondIntent);


                    /*if(flag_song==true){
                        System.out.println("Send song");
                        out.writeUTF(l.itemValue);
                    }*/




               /* String result = intent.getStringExtra("OK");
                if(result.equals("OK")){
                    String songName = l.itemValue;
                    out.writeUTF(songName);
                }*/
                    //out.writeUTF(songSelected);



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
   /* protected void onPostExecute(ArrayAdapter adapter) {
        // execution of result of Long time consuming operation
        progressDialog.dismiss();
        // dropdown = listofsongs;
        dropdown.setVisibility(Spinner.VISIBLE);
        dropdown.setAdapter(adapter);
    }*/

    /*@Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(AsyncTaskRunner.this,
                "Searching for songs of the artist",
                "Still searching...");
    }*/

       /* @Override
        protected void onProgressUpdate(String... text) {
            finalResult.setText(text[0]);
        }*/



    }



}



