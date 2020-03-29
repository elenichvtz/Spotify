import java.util.ArrayList;
import java.util.List;
import java.io.*;

public interface Node extends Serializable {

    public final static List<BrokerNode> brokers = new ArrayList<BrokerNode>();

    public abstract void init(); //change

    public abstract List <BrokerNode> getBrokers();

    public abstract void connect();
    public abstract void disconnect();

}
