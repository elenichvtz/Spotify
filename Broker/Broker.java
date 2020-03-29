package src;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public interface Broker extends Node {

    public final static List<Consumer> registeredUsers = new ArrayList<Consumer>();
    public final static List<PublisherNode> registeredPublishers = new ArrayList<PublisherNode>();

    public abstract BigInteger calculateKeys();
    public abstract Publisher acceptConnection(Publisher publisher);
    public abstract Consumer acceptConnection(Consumer consumer);
    public abstract void notifyPublisher(String name);
    public abstract void pull(ArtistName artist) throws NoSuchAlgorithmException;
}
