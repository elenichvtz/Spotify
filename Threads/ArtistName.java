import java.io.Serializable;

public class ArtistName implements Serializable{
    String artistName;

    ArtistName(String artistName){
        this.artistName = artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistName() {
        return artistName;
    }

    @Override
    public String toString() {
        return artistName.toString();
    }
}
