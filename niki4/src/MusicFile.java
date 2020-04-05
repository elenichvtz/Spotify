import java.io.Serializable;

public class MusicFile implements Serializable {

    //writeObject
    //readObject
    //arraylist me filename
    //oxi localhost
    //brokers diaforetika port alliws tha evgaze bind error

    String trackName;
    String artistName;
    String albumInfo;
    String genre;
    int totalChunks;
    int chunkId;
    byte[] musicFileExtract;

    MusicFile(String trackName,String artistName,String albumInfo,String genre,byte[] musicFileExtract, int chunkId, int totalChunks){
        this.trackName = trackName;
        this.artistName =  artistName;
        this.albumInfo = albumInfo;
        this.genre = genre;
        this.musicFileExtract = musicFileExtract;
        this.chunkId = chunkId;
        this.totalChunks= totalChunks;
    }

    MusicFile(String trackName){
        this.trackName = trackName;
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

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setMusicFileExtract(byte[] musicFileExtract) {
        this.musicFileExtract = musicFileExtract;
    }

    public void setChunkId(int chunkId) { this.chunkId = chunkId;} //new

    public void setTotalChunks(int totalChunks) { this.totalChunks = totalChunks; } //new

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

    public int getChunkId() { return chunkId; } //new

    public int getTotalChunks() { return totalChunks; } //new

    @Override
    public String toString() {
        return super.toString();
    }
}
