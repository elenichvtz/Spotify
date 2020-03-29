import java.util.ArrayList;
import java.util.List;
import java.io.*;

public interface Node extends Serializable{

    public final static List<Broker> brokers = new ArrayList<Broker>();

    public abstract void init(); //change

    public abstract List <Broker> getBrokers();

    public abstract void connect();
    public abstract void disconnect();

}
