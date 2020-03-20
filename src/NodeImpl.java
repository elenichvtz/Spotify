import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class NodeImpl implements Node{

    public Socket requestSocket;
    public ServerSocket providerSocket;

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
        try {
            providerSocket = new ServerSocket(4321, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect(){
        try {
            requestSocket.close();
            providerSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateNodes(){

    }
}

