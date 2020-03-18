<<<<<<< HEAD
=======

>>>>>>> 38f1844698dffb311f7865830279b91993cbe333
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public interface Node {

    public final static List<Broker> brokers = new ArrayList<Broker>();

    public abstract void init(int x);

    public abstract List <Broker> getBrokers();

    public abstract void connect();
    public abstract void disconnect();
    public abstract void updateNodes();

}