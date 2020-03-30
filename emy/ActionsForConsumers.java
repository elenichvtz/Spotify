import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ActionsForConsumers extends Thread {

    ObjectInputStream in;
    ObjectOutputStream out;
    List<ConsumerNode> registeredUsers = new ArrayList<>();
    List<BrokerNode> brokers = new ArrayList<>();
    ConsumerNode con;
    ArtistName artistreceived;

    public ActionsForConsumers(Socket consumer,List<ConsumerNode> registeredUsers,List<BrokerNode> brokers) {
        try {
            out = new ObjectOutputStream(consumer.getOutputStream());
            in = new ObjectInputStream(consumer.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ConsumerNode getConsumer(){
        return con;
    }

    public ArtistName getArtistreceived(){ return artistreceived;}



    public synchronized void run(){



            try {
                // socket object to receive incoming consumer requests
                //Socket consumer = broker.getConsumerServerSocket().accept();

                //out.writeObject(brokers); // revert back if necessary
                    //receive ip and port from consumer
                    String consumerip = in.readUTF();
                    System.out.println("con " + consumerip);
                    int consumerport = in.readInt();
                    System.out.println(consumerport);

                    ConsumerNode cn = new ConsumerNode(consumerip, consumerport);

                    //registeredUsers.add(cn);
                    System.out.println(registeredUsers.isEmpty());
                ArtistName artistName = null;
                try {
                    artistName = (ArtistName) in.readObject();
                    System.out.println(artistName.toString()+" received from consumer");
                    artistreceived = artistName; //
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }



                    System.out.println("Assigning new thread for this client");


                } catch (IOException e) {
                    e.printStackTrace();
                }


    }
}
