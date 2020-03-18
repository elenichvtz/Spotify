<<<<<<< HEAD
=======

>>>>>>> 38f1844698dffb311f7865830279b91993cbe333
import java.util.ArrayList;
import java.util.List;
import java.io.*;
//server
public interface Broker extends Node {

    public final static List<Consumer> registeredUsers = new ArrayList<Consumer>();
    public final static List<Publisher> registeredPublishers = new ArrayList<Publisher>();

    public abstract void calculateKeys();
    public abstract Publisher acceptConnection(Publisher publisher);
    public abstract Consumer acceptConnection(Consumer consumer);
    public abstract void notifyPublisher(String name);
<<<<<<< HEAD
=======


>>>>>>> 38f1844698dffb311f7865830279b91993cbe333
    public abstract void pull(ArtistName artist);
}
