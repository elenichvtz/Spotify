import java.util.List;
<<<<<<< HEAD
import java.io.*; 
//client
public interface Consumer extends Node {
    
	public abstract void register(Broker broker,ArtistName artist);
=======
import java.io.*;

public interface Consumer extends Node {

    public abstract void register(Broker broker,ArtistName artist);
>>>>>>> 615de5ecf998569f132e565b2d8626a71b3373a4

    public abstract void disconnect(Broker broker,ArtistName artist);

    public abstract void playData(ArtistName artist, Value val);
}
