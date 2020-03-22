import com.mpatric.mp3agic.Mp3File;

public class MusicFile {

    String trackName;
    String artistName;
    String albumInfo;
    String genre;
    byte[] musicFileExtract;

    MusicFile(String trackName,String artistName,String albumInfo,String genre,byte[] musicFileExtract){
        this.trackName = trackName;
        this.artistName =  artistName;
        this.albumInfo = albumInfo;
        this.genre = genre;
        this.musicFileExtract = musicFileExtract;
    }

    public void setTrackName(String trackName){
        this.trackName = trackName;
    }

    public void setAlbumInfo(String albumInfo) {
        this.albumInfo = albumInfo;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setGenre(int genre) {
        this.genre = genre;
    }

    public void setMusicFileExtract(Mp3File musicFileExtract) {
        this.musicFileExtract = musicFileExtract;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumInfo() {
        return albumInfo;
    }

    public String getGenre() {
        return genre;
    }

    public byte[] getMusicFileExtract() {
        return musicFileExtract;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
