import java.util.List;
import java.io.*;

public interface Publisher extends Node {

    public abstract void getBrokerList();

    public abstract Broker hashTopic(ArtistName artist);

    public abstract void push(ArtistName artist,Value val);

    public abstract void notifyFailure(Broker broker);
}
