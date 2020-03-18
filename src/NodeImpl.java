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



    }

    public void AddBroker(Broker broker){  //egw tin evala autinane <3
        brokers.add(broker);

    }


    @Override
    public List<Broker> getBrokers() {
        return brokers;
    }


    @Override
    public void connect(){
        try{
            requestSocket = new Socket("127.0.0.1", 4321);
        }catch (IOException e){
            e.printStackTrace();
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

