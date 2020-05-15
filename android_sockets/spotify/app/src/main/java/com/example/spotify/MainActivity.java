package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.rtp.AudioStream;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

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
import java.io.File;
import java.io.FileDescriptor;
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
    static FileOutputStream file;

    static ArrayList<Value> pieces1;
    static String name;
    MediaPlayer player;


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

    public class AsyncTaskRunner extends AsyncTask<String,String,String> implements java.io.Serializable {

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
                        lb1_output.setText("");
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



    public class AsyncTaskRunner1 extends AsyncTask<String,String,String> implements java.io.Serializable {

        private String resp;
        ProgressDialog progressDialog;
        //String[] params = new String[2];
        int exist;
        MainActivity k = new MainActivity();

        ArtistName artist = new ArtistName(txt_input.getText().toString());

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


                        str = in.readUTF();
                        System.out.println("Name of the song broker sent me: " + str);
                        chunks = in.readInt();
                        if(str==null) str="songReceived";
                        name = str.concat(".mp3");
                        File fis = new File(getExternalFilesDir(null), name);
                        FileOutputStream file1 ;
                       // MusicFile f = new MusicFile(song, artist.getArtistName(), null, null, null, 0, 0);
                       // DataInputStream in3 = new DataInputStream(k.requestSocket.getInputStream());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                       // byte result[] = new byte[512*1024*chunks];

                        byte buffer[] = new byte[512*1024];


                       //file1 =
                        for (int i = 1; i <= chunks; i++) {
                            fis.createNewFile();
                            MusicFile f = new MusicFile(str, artist.getArtistName(), null, null, null, 0, 0);
                            baos.write(buffer, 0 , in3.read(buffer));
                            byte[] result = baos.toByteArray();
                            f.setMusicFileExtract(result);
                            System.out.println("Path of the song: " +fis.getPath());
                            String res = Arrays.toString(result);
                            Value value =  new Value(f);

                            System.out.println("Value of the song broker sent me: " + value.getMusicfile().getTrackName());
                            file1= new FileOutputStream(fis, true);

                            file1.write(value.getMusicfile().getMusicFileExtract());
                            file1.flush();
                            file = file1;
                            pieces.add(value); //saves chunks locally


                        }
                        //initializeMediaPlayer(file);
                        /*MediaPlayer player = new MediaPlayer();
                        Button b2 = (Button) findViewById(R.id.button2);
                        b2.setOnClickListener(new View.OnClickListener() {

                                                  @Override
                                                  public void onClick(View v) {
                                                      try {
                                                          player.setDataSource("/storage/emulated/0/Android/data/com.example.spotify/files/".concat(name));
                                                      } catch (IOException e) {
                                                          e.printStackTrace();
                                                      }
                                                      player.prepareAsync();
                                                      player.start();
                                                  }
                                              });*/
                        //String bip = "bip.mp3";

                       // MediaStore.Audio.Media hit = new Mediabip);
                       /* MediaPlayer mp = new MediaPlayer();
                       // mp = MediaPlayer.create(getApplicationContext(),
                        mp.setDataSource(fd);
                        mp.start();*/

                        //mediaPlayer1.prepareAsync();

                        /*for(int i=0; i<pieces.size(); i++) {
                            pieces1.add(pieces.get(i));
                        }*/




                    } catch (IOException  e) {
                        e.printStackTrace();
                    }
               // }



            return null;
        }

        protected void onPostExecute(String text) {
            //if(exist==1) {
               txt_input.setText("");
                progressDialog = ProgressDialog.show(MainActivity.this,
                        "Wait a few seconds",
                        "Fetching the song");
                Intent intentplay = new Intent(MainActivity.this, PlaySong.class);
                startActivity(intentplay);
            }

        }





    }







