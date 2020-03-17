import java.util.List;
<<<<<<< HEAD
import java.io.*; 
//client
public interface Publisher extends Node {
	
=======
import java.io.*;

public interface Publisher extends Node {

>>>>>>> 615de5ecf998569f132e565b2d8626a71b3373a4
    public abstract void getBrokerList();

    public abstract Broker hashTopic(ArtistName artist);

    public abstract void push(ArtistName artist,Value val);

    public abstract void notifyFailure(Broker broker);
}
