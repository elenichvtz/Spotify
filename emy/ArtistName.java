import java.util.List;
import java.io.*;

public class ArtistName implements Serializable {
    String artistName;

    ArtistName(String artistName){
        this.artistName = artistName;

    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistName() {
        return this.artistName;
    }

    @Override
    public String toString() { return this.artistName.toString(); }
}
