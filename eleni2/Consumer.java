import java.io.Serializable;

public interface Consumer extends Node, Serializable {

    public abstract void register(Broker broker,ArtistName artist);

    public abstract void disconnect(Broker broker,ArtistName artist);

    public abstract void playData(ArtistName artist, Value val);
}
