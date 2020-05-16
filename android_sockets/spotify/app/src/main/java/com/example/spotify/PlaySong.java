package com.example.spotify;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PixelFormat;
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

import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

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

        File clip = new File(m.fis2.getPath());

        if (clip.exists()) {
           // video=(VideoView)findViewById(R.id.videoView);
            System.out.println("Path of fis2:     " + m.fis2.getAbsolutePath());
            MediaPlayer player = new MediaPlayer();
            player.setAudioAttributes( new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());

            try {
                player.setDataSource(m.file1.getFD());
                player.prepareAsync();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*ctlr=new MediaController(PlaySong.this);
            ctlr.setMediaPlayer(video);
            video.setMediaController(ctlr);
            video.requestFocus();
            video.start();*/
        }
    }

}





