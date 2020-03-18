import java.util.ArrayList;
import java.util.List;
import java.io.*;

public interface Broker extends Node {

    public final static List<Consumer> registeredUsers = new ArrayList<Consumer>();
    public final static List<Publisher> registeredPublishers = new ArrayList<Publisher>();

    public abstract void calculateKeys();
    public abstract Publisher acceptConnection(Publisher publisher);
    public abstract Consumer acceptConnection(Consumer consumer);
    public abstract void notifyPublisher(String name);
    public abstract void pull(ArtistName artist);
}
