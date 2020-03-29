import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Broker extends Node, Serializable {

    public final static List<Consumer> registeredUsers = new ArrayList<Consumer>();
    public final static List<Publisher> registeredPublishers = new ArrayList<Publisher>();
    //publishers' maps
    ArrayList<HashMap> maps = new ArrayList<>();

    public abstract BigInteger calculateKeys();
    public abstract Publisher acceptConnection(Publisher publisher);
    public abstract Consumer acceptConnection(Consumer consumer);
    public abstract void notifyPublisher(String name);
    public abstract void pull(ArtistName artist);
}
