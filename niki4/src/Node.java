import java.util.ArrayList;
import java.util.List;
import java.io.*;

public interface Node extends Serializable {

    //public final static List<BrokerNode> brokers = new ArrayList<BrokerNode>();
    static ArrayList<BrokerNode> ListOfBrokers = new ArrayList<>(); //we will send this list to all the publishers & the consumer

    public abstract void init();

    public abstract List <BrokerNode> getBrokers();

    public abstract void connect();
    public abstract void disconnect();

}
