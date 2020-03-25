import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class NodeImpl implements Node{

    public Socket requestSocket;
    public ServerSocket providerSocket;

   @Override
    public void init(int x){


    }

    @Override
    public List<Broker> getBrokers() {
        return brokers;
    }

    @Override
    public void connect(){

    }

    @Override
    public void disconnect(){

    }

    @Override
    public void updateNodes(){

    }
}

