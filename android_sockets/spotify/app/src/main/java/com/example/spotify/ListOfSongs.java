package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListOfSongs extends AppCompatActivity {
    ListView songlist;

   /* public void selectSong(){

            //list of songs of the requested artist
            this.listofsongs = (ArrayList<String>) in.readObject();

            System.out.println(this.listofsongs.toString());



            boolean songflag = false;

            while (songflag == false) {
                System.out.println("Pick a song: ");
                String song = userInput.nextLine();
                for (int i = 0; i < listofsongs.size(); i++) {
                    if (song.equals(listofsongs.get(i))) {
                        songflag = true;
                        this.out.writeUTF(song);
                        this.out.flush();
                        break;
                    }

                }
            }
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_songs);

        songlist = (ListView) findViewById(R.id.listview);
        ArrayList<String> songs = new ArrayList<>();

        songs.add("Celebration");
        songs.add("Bleu");
        songs.add("La Citadelle");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,songs);

        songlist.setAdapter(arrayAdapter);

        songlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               Intent i = new Intent(ListOfSongs.this, PlaySong.class);
               startActivity(i);
            }
        });


    }
}
