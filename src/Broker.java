import java.util.List;
import java.io.*; 
//server
public interface Broker extends Node {

    public final static List<Consumer> registeredUsers;
    public final static List<Publisher> registeredPublishers;

    public abstract void calculateKeys();
    public abstract Publisher acceptConnection(Publisher publisher);
    public abstract Consumer acceptConnection(Consumer consumer);
    public abstract void notifyPublisher(String name);

    public abstract void pull(ArtistName artist);
}
