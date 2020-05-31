package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Downloads extends AppCompatActivity {
    ListView downloadedSongs;
    TextView noSongs;
    PlaySong pl = new PlaySong();
    MediaPlayer player;
    SeekBar seekBar;
    boolean wasPlaying = false;
    FloatingActionButton fab;
    Button back;
    Button again;
    File fis;
    static int num_clicks =0;
    static int stop_pos = 0;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);
        downloadedSongs = (ListView) findViewById(R.id.listview);
        back = (Button) findViewById(R.id.button2);
        fab = (FloatingActionButton) findViewById(R.id.button);
        again = (Button) findViewById(R.id.button3);
        noSongs = (TextView) findViewById(R.id.textView);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        fab.setVisibility(View.GONE);
        again.setVisibility(View.GONE);
        noSongs.setVisibility(View.GONE);
        seekBar.setVisibility(View.GONE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player!=null){
                    player.stop();
                    player.reset();
                    player.release();
                    player = null;
                }
                Intent intent = new Intent(Downloads.this, MainActivity.class);
                startActivity(intent);

            }
        });
        final TextView seekBarHint = findViewById(R.id.textView1);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                seekBarHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE);
                int x = (int) Math.ceil(progress / 1000f);

                if (x ==0 && player != null && !player.isPlaying()) {
                    clearMediaPlayer();
                    fab.setImageDrawable(ContextCompat.getDrawable(Downloads.this, android.R.drawable.ic_media_play));
                    Downloads.this.seekBar.setProgress(0);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


                if (player != null && player.isPlaying()) {
                    player.seekTo(seekBar.getProgress());
                }
            }
        });

        if (pl.downloads.size() > 0) {
            System.out.println("I have songs");

            downloadedSongs.setVisibility(View.VISIBLE);
            ArrayAdapter arrayAdapter = new ArrayAdapter(Downloads.this, android.R.layout.simple_list_item_1, pl.downloads);
            downloadedSongs.setAdapter(arrayAdapter);
            downloadedSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemValue = (String) downloadedSongs.getItemAtPosition(position);
                    downloadedSongs.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                    //again.setVisibility(View.VISIBLE);

                    seekBar.setVisibility(View.VISIBLE);
                    player = new MediaPlayer();
                    player.reset();
                    try {
                        for (int i = 0; i < pl.downloadedSongs.size(); i++) {
                            if (itemValue.concat(".mp3").equals(pl.downloadedSongs.get(i).getName())) {


                                player.setDataSource(pl.downloadedSongs.get(i).getPath());
                            }
                        }
                        player.prepare();
                        player.setVolume(0.5f, 0.5f);
                        player.setLooping(false);


                        player.start();
                        seekBar.setMax(player.getDuration());

                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                System.err.println("Releasing the player");

                                mp.reset();


                                again.setVisibility(View.VISIBLE);
                                again.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try {
                                            for (int i = 0; i < pl.downloadedSongs.size(); i++) {
                                                if (itemValue.concat(".mp3").equals(pl.downloadedSongs.get(i).getName())) {


                                                    player.setDataSource(pl.downloadedSongs.get(i).getPath());
                                                }
                                            }
                                            player.prepare();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        player.setVolume(0.5f, 0.5f);
                                        player.setLooping(false);


                                        player.start();
                                        seekBar.setMax(player.getDuration());
                                    }
                                });

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fab.setImageDrawable(ContextCompat.getDrawable(Downloads.this, android.R.drawable.ic_media_pause));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            num_clicks++;

                            stop_pos = player.getCurrentPosition();
                            player.pause();
                            fab.setImageDrawable(ContextCompat.getDrawable(Downloads.this, android.R.drawable.ic_media_play));
                            if(num_clicks%2!=0){
                                player.seekTo(stop_pos);
                                player.start();
                                fab.setImageDrawable(ContextCompat.getDrawable(Downloads.this, android.R.drawable.ic_media_pause));
                            }

                        }
                    });
                }
            });
        }
        else{
            noSongs.setVisibility(View.VISIBLE);
            noSongs.setText("You haven't downloaded any songs");
            //Intent intent = new Intent(Downloads.this, MainActivity.class);
          //  startActivity(intent);
        }
    }
    protected void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
    }

    private void clearMediaPlayer() {
        System.out.println("Inside Playsong/clearMediaPlayer");
        player.stop();
        player.reset();
        player = null;
    }
}
