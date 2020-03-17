<<<<<<< HEAD
import java.util.List;
import java.io.*; 

public interface Node {
	
    public final static List<Broker> brokers;
=======
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public interface Node {

    public final static List<Broker> brokers = new ArrayList<Broker>();
>>>>>>> 615de5ecf998569f132e565b2d8626a71b3373a4

    public abstract void init(int x);

    public abstract List <Broker> getBrokers();

    public abstract void connect();
    public abstract void disconnect();
    public abstract void updateNodes();

}
