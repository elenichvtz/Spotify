package com.example.spotify;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
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


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.lang.*;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements Serializable {
    private Button button;
    private EditText txt_input;
    private TextView lb1_output;
    private Spinner dropdown;
   // static ObjectOutputStream out = null;
   // static ObjectInputStream in = null;
    //static Socket requestSocket = null;
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
    static DataInputStream in3 = null;
    static DataOutputStream out3 = null;
     FileOutputStream file;

    static ArrayList<Value> pieces1;
    static String name;
    MediaPlayer player;
    static final long serialVersionUID = 42L;
    File fis;
    static File fis2;
    FileOutputStream file1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_input = (EditText) findViewById(R.id.txt_input);
        lb1_output = (TextView) findViewById(R.id.textView);
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
                AsyncTaskRunner1 run1 = new AsyncTaskRunner1();
                run1.execute();

            }
            if (resultCode == ListOfSongs.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


    public void onStart() {
        super.onStart();
        System.out.println("FLAG : " + flag_artist);
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

    }
    private void initializeMediaPlayer(FileOutputStream file) {
        player = new MediaPlayer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            player.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build());
        } else {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        try {
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            player.setDataSource(file.getFD());
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class AsyncTaskRunner extends AsyncTask<String,String,String> implements Serializable {
        static final long serialVersionUID = 42L;
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
                requestSocket = new Socket("192.168.1.3", 7655);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());
                in3 = new DataInputStream(requestSocket.getInputStream());
                out3 = new DataOutputStream(requestSocket.getOutputStream());
                portOfBroker = 7655;
                out.writeUTF("192.168.1.3");
                out.writeInt(7655);
                out.writeUTF(artist.getArtistName());
                out.flush();
                int brokerport = in.readInt();
                System.out.println("..... doInBackground");
                if (brokerport != 7655) {
                    portOfBroker = brokerport;
                    requestSocket.close();
                    Socket request = new Socket("192.168.1.3", brokerport+1);
                    out = new ObjectOutputStream(request.getOutputStream());
                    in = new ObjectInputStream(request.getInputStream());
                    in3 = new DataInputStream(request.getInputStream());
                    out3 = new DataOutputStream(request.getOutputStream());
                    out.writeUTF("192.168.1.3");
                    out.writeInt(brokerport);
                    out.writeUTF(artist.getArtistName()); //successfully sends artistName to BrokerNode
                    out.flush();
                    System.out.println("Connect with other broker");
                }

                exist = in.readInt();
                if(exist!=1){
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
                        //lb1_output.setVisibility(View.GONE);
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

                    System.out.println("BEFORE FLAG SONG is: " + flag_song);
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
       // @Override


    }



    public class AsyncTaskRunner1 extends AsyncTask<String,String,String> implements Serializable {
        static final long serialVersionUID = 42L;
        private String resp;
        ProgressDialog progressDialog;
        //String[] params = new String[2];
        int exist;
        MainActivity k = new MainActivity();

        ArtistName artist = new ArtistName(txt_input.getText().toString());

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @SuppressLint("WrongThread")
        protected String doInBackground(String... params) {
            System.out.println("artist:" + artist.getArtistName());


            publishProgress("Sleeping...");


                //requestSocket = new Socket("192.168.1.3", 7655);
               // out = new ObjectOutputStream(requestSocket.getOutputStream());
               // in = new ObjectInputStream(requestSocket.getInputStream());
                //if(flag_song==true){
                    try {
                        System.out.println("Port of broker: " + portOfBroker);

                        requestSocket = k.requestSocket;
                        out = k.out;
                        in3 = k.in3;

                        System.out.println("Socket output: " + out==null);
                        out.writeUTF(message);
                        out.flush();

                        int chunks = 0;
                        String str ;
                        //System.out.println("Song's name: " +str);
                        ArrayList<Value> pieces = new ArrayList<>();
                        MediaPlayer player = new MediaPlayer();
                        try {
                            str = in.readUTF();
                            chunks = in.readInt();
                            if(str==null) str="songReceived";
                            File fis = new File(getExternalFilesDir(null) , str.concat(".mp3"));
                            FileOutputStream fileOutputStream = null;
                            fis.createNewFile();
                            fileOutputStream = new FileOutputStream(fis, true);
                            fileOutputStream.write(str.concat(".mp3").getBytes());
                            fileOutputStream.flush();


                            for (int i = 1; i <= chunks; i++) {

                                Value value = new Value((MusicFile) in.readObject());
                                System.out.println("Song broker send me:  " + value.getMusicfile().getTrackName());

                                fileOutputStream.write(value.getMusicfile().getMusicFileExtract());
                                fileOutputStream.flush();
                                pieces.add(value); //saves chunks locally
                            }
                            //fileOutputStream.close();
                            fileOutputStream.close();
                            player.setDataSource(fis.getPath());

                            player.prepare();

                            player.start();
                           // player.reset();

                            /*Button button = (Button)findViewById(R.id.button2);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println("Play song!!!!");
                                    //player.seekTo(1000);

                                    player.start();
                                }
                            });*/


                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }








                    } catch (IOException  e) {
                        e.printStackTrace();
                    }



            return null;
        }

       /* protected void onPostExecute(String text) {
            //if(exist==1) {
               txt_input.setText("");
                progressDialog = ProgressDialog.show(MainActivity.this,
                        "Wait a few seconds",
                        "Fetching the song");
                Intent intentplay = new Intent(MainActivity.this, PlaySong.class);
                startActivity(intentplay);
            }*/

        }





    }







