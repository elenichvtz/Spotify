package src;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public interface Node extends Serializable {

    public final static List<BrokerNode> brokers = new ArrayList<BrokerNode>();

     //change

    public abstract List <BrokerNode> getBrokers();




}
