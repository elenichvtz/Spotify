package com.example.spotify;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.Button;

import android.widget.EditText;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import android.media.MediaPlayer.OnCompletionListener;
import android.widget.VideoView;

public class PlaySong extends AppCompatActivity implements Serializable {
    private VideoView video;
    private MediaController ctlr;
    ObjectInputStream in;
    ObjectOutputStream out;
    File fis;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFormat(PixelFormat.TRANSLUCENT);

        setContentView(R.layout.activity_play_song);
        // video=(VideoView)findViewById(R.id.videoView);

    }
    public void onStart() {
        super.onStart();
        AsyncTaskRunner1 runner = new AsyncTaskRunner1();
        runner.execute();

    }

    public class AsyncTaskRunner1 extends AsyncTask<String,String,String> implements Serializable {
        static final long serialVersionUID = -373782829391231342L;
        private String resp;
        ProgressDialog progressDialog;
        //String[] params = new String[2];
        int exist;
        MainActivity k = new MainActivity();

        //ArtistName artist = new ArtistName(txt_input.getText().toString());

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @SuppressLint("WrongThread")
        protected String doInBackground(String... params) {



            publishProgress("Sleeping...");


            //requestSocket = new Socket("192.168.1.3", 7655);
            // out = new ObjectOutputStream(requestSocket.getOutputStream());
            // in = new ObjectInputStream(requestSocket.getInputStream());
            //if(flag_song==true){
            try {


                //requestSocket = k.requestSocket;
                out = k.out;
                in = k.in;


                System.out.println("Socket output: " + out==null);
                out.writeUTF(k.message);
                out.flush();
                System.out.println("Message in asynctask is:  " + k.message);

                int chunks = 0;
                String str ;
                //System.out.println("Song's name: " +str);
                ArrayList<Value> pieces = new ArrayList<>();
                boolean on_off = k.choice;
                if(on_off==false) {
                    try {
                        str = in.readUTF();
                        chunks = in.readInt();
                        System.out.println("number of chunks:   " + chunks);
                        if (str == null) str = "songReceived";
                        fis = new File(getFilesDir() + "/" + str.concat(".mp3"));
                        FileOutputStream fileOutputStream = null;
                        //fis.createNewFile();
                        fileOutputStream = new FileOutputStream(fis);
                        //fileOutputStream.write(str.concat(".mp3").getBytes());
                        //fileOutputStream.flush();


                        for (int i = 1; i <= chunks; i++) {

                            Value value = new Value((MusicFile) in.readObject());
                            System.err.println(i);
                            System.out.println("Song broker send me:  " + value.getMusicfile().getTrackName());

                            fileOutputStream.write(value.getMusicfile().getMusicFileExtract());
                            fileOutputStream.flush();

                            pieces.add(value); //saves chunks locally
                            out.writeUTF("ok");
                            out.flush();

                        }
                        fileOutputStream.close();
                        MediaPlayer player = new MediaPlayer();

                        try {
                            System.out.println("fis length: " + fis.length());
                            player.setDataSource(fis.getPath());
                            player.prepare();
                            player.start();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //fis2 = fis;
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        str = in.readUTF();
                        chunks = in.readInt();
                        System.out.println("number of chunks:   " + chunks);
                        if (str == null) str = "songReceived";
                        fis = new File(getFilesDir() + "/" + str.concat(".mp3"));
                        FileOutputStream fileOutputStream = null;
                        //fis.createNewFile();
                        fileOutputStream = new FileOutputStream(fis);
                        //fileOutputStream.write(str.concat(".mp3").getBytes());
                        //fileOutputStream.flush();


                        for (int i = 1; i <= chunks; i++) {

                            Value value = new Value((MusicFile) in.readObject());
                            System.err.println(i);
                            System.out.println("Song broker send me:  " + value.getMusicfile().getTrackName());

                            fileOutputStream.write(value.getMusicfile().getMusicFileExtract());
                            fileOutputStream.flush();

                            pieces.add(value); //saves chunks locally
                            out.writeUTF("ok");
                            out.flush();

                        }
                        fileOutputStream.close();
                        MediaPlayer player = new MediaPlayer();

                        try {
                            System.out.println("fis length: " + fis.length());
                            player.setDataSource(fis.getPath());
                            player.prepare();
                            player.start();
                            fis.delete();
                            System.out.println("fis size: " + fis.length());


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //fis2 = fis;
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
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





