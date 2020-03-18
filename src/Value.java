import java.util.List;
import java.io.*;

public class Value {

    MusicFile musicfile;

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
