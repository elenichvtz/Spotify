import java.util.List;
<<<<<<< HEAD
import java.io.*; 

public class Value {

	public MusicFile musicFile;

}
=======
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
>>>>>>> 615de5ecf998569f132e565b2d8626a71b3373a4
