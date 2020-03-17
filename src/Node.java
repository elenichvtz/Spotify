import java.util.List;
import java.io.*; 

public interface Node {
	
    public final static List<Broker> brokers;

    public abstract void init(int x);

    public abstract List <Broker> getBrokers();

    public abstract void connect();
    public abstract void disconnect();
    public abstract void updateNodes();

}
