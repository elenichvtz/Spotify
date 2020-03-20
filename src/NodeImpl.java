import javax.imageio.IIOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.net.*;

public class NodeImpl implements Node{

    public Socket requestSocket;
    public ServerSocket providerSocket;
    //public Socket connection = null;


    @Override
    public void init(int x){
        System.out.println("Initializing..."+x);
        for(int i = 0; i < x; ++i){
            BrokerNode b = new BrokerNode();
            brokers.add(b);
        }

    }


    @Override
    public List<Broker> getBrokers() {
        return brokers;
    }


    @Override
    public void connect(){
        while(!requestSocket.isConnected()) {
            try {
                requestSocket = new Socket("127.0.0.1", 4321);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void disconnect(){
        try {
            requestSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void updateNodes(){




    }
}

