package com.example.spotify;
import java.io.Serializable;

public class Value implements Serializable {

    MusicFile musicfile;
    static final long serialVersionUID = 42L;

    Value(MusicFile musicFile){
        this.musicfile = musicFile;
    }

    public void setMusicfile(MusicFile musicfile) {
        this.musicfile = musicfile;
    }

    public MusicFile getMusicfile() {
        return musicfile;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

