import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ActionsForClients2 extends Thread {

    ObjectInputStream in;
    //ObjectOutputStream out;
    List<Consumer> registeredUsers = new ArrayList<>();
    ConsumerNode con;

    public ActionsForClients2(Socket consumer,List<Consumer> registeredUsers ) {
        try {
            //out = new ObjectOutputStream(consumer.getOutputStream());
            in = new ObjectInputStream(consumer.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Consumer getConsumer(){
        return con;
    }


    public synchronized void run(){



            try {
                // socket object to receive incoming consumer requests
                //Socket consumer = broker.getConsumerServerSocket().accept();

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
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }



                    System.out.println("Assigning new thread for this client");


                } catch (IOException e) {
                    e.printStackTrace();
                }


    }
}
