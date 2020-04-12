import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public interface Publisher extends Node {

    public abstract Broker hashTopic(ArtistName artist) throws NoSuchAlgorithmException;

    public abstract void push(Socket socket, ArtistName artist, Value val);
}
