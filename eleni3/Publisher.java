import java.util.List;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public interface Publisher extends Node {

    public abstract Broker hashTopic(ArtistName artist) throws NoSuchAlgorithmException;

    public abstract void push(ArtistName artist,Value val);

    public abstract void notifyFailure(BrokerNode broker);
}
