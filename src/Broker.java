<<<<<<< HEAD
import java.util.List;
import java.io.*; 
//server
public interface Broker extends Node {

    public final static List<Consumer> registeredUsers;
    public final static List<Publisher> registeredPublishers;
=======
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public interface Broker extends Node {

    public final static List<Consumer> registeredUsers = new ArrayList<Consumer>();
    public final static List<Publisher> registeredPublishers = new ArrayList<Publisher>();
>>>>>>> 615de5ecf998569f132e565b2d8626a71b3373a4

    public abstract void calculateKeys();
    public abstract Publisher acceptConnection(Publisher publisher);
    public abstract Consumer acceptConnection(Consumer consumer);
    public abstract void notifyPublisher(String name);
<<<<<<< HEAD

=======
>>>>>>> 615de5ecf998569f132e565b2d8626a71b3373a4
    public abstract void pull(ArtistName artist);
}
