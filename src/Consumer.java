
import java.io.*;
//client
public interface Consumer extends Node {

    public abstract void register(Broker broker,ArtistName artist);

    public abstract void disconnect(Broker broker,ArtistName artist);

    public abstract void playData(ArtistName artist, Value val);
}
