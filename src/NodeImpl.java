import javax.imageio.IIOException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.net.*;

public class NodeImpl implements Node {

    @Override
    public void init(int x) {
        System.out.println("Initializing..." + x);
    }

    public void AddBroker(Broker broker) {  //egw tin evala autinane <3
        brokers.add(broker);
    }

    @Override
    public List<Broker> getBrokers() {
        return brokers;
    }

    @Override
    public void connect() { }

    @Override
    public void disconnect() { }

    @Override
    public void updateNodes() {

    }
}

