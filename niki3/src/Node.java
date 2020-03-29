import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.*;
import java.util.Map;

public interface Node {

    //public final static List<Broker> brokers = new ArrayList<Broker>();
    Map<Broker, ArtistName> brokerMap = new HashMap<Broker, ArtistName>(); //map that associates a broker with an artist name
    //we fill this in hashTopic and use it in register

    public abstract void init(); //change

    public abstract List <Broker> getBrokers();

    public abstract void connect();
    public abstract void disconnect();

}
