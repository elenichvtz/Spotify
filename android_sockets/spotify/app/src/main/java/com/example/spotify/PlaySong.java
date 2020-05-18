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
    MainActivity m = new MainActivity();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFormat(PixelFormat.TRANSLUCENT);

        setContentView(R.layout.activity_play_song);



           // video=(VideoView)findViewById(R.id.videoView);

            MediaPlayer player = new MediaPlayer();

            try {
                System.out.println("fis length: " + m.fis2.length());
                player.setDataSource(m.fis2.getPath());
                player.prepare();
                player.start();

            } catch (IOException e) {
                e.printStackTrace();
            }


    }



}





