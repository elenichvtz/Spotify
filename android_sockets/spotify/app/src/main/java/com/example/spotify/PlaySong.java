package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.Button;

import android.widget.SeekBar;
import android.widget.TextView;


import java.io.FileDescriptor;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.Serializable;

import android.media.MediaPlayer.OnCompletionListener;
import android.widget.VideoView;

public class PlaySong extends AppCompatActivity implements Serializable {
    private MediaPlayer player;
    private VideoView video;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        MainActivity mainObject = new MainActivity();
        FileOutputStream file = mainObject.file;
        //player = MediaPlayer.create(this, Uri.parse("/storage/emulated/0/Android/data/com.example.spotify/files/".concat(mainObject.name)));
        Button b2 = (Button) findViewById(R.id.button2);
        final TextView t = (TextView) findViewById(R.id.textView1);


        System.out.println("Name of the song: " + mainObject.name);
        /*mp = new MediaPlayer();
        try {
            mp.setDataSource(String.valueOf(file));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mp.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        b2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              // initializeMediaPlayer(mainObject);
                try {
                    player.setDataSource("/storage/emulated/0/Android/data/com.example.spotify/files/".concat(mainObject.name));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.prepareAsync();
                player.start();
            }

        });

       /* b2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopPlaying();
                mp = MediaPlayer.create(PlayaudioActivity.this, R.raw.beet);
                mp.start();
            }
        });
    }

    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }*/
    }
    private void initializeMediaPlayer(MainActivity mainObject) {
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
            player.setDataSource(mainObject.file.getFD());
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



