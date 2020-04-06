import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Broker extends Node {

    public final static List<ConsumerNode> registeredUsers = new ArrayList<ConsumerNode>();
    public final static List<PublisherNode> registeredPublishers = new ArrayList<PublisherNode>();

    public Map<Integer, ArrayList<ArtistName>> findBroker(List<BrokerNode> broker) throws NoSuchAlgorithmException;
    public abstract BigInteger calculateKeys();
    public abstract PublisherNode acceptConnection(PublisherNode publisher);
    public abstract ConsumerNode acceptConnection(ConsumerNode consumer);
    public abstract void notifyPublisher(String name);
    public abstract void pull(ArtistName artist, String title);

}
