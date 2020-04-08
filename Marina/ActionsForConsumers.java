package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionsForConsumers extends Thread {

    ObjectInputStream in;
    ObjectOutputStream out;
    List<ConsumerNode> registeredUsers = new ArrayList<>();
    
    ConsumerNode con;
    ArtistName artistreceived = null;
    Map<String,ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

    public ActionsForConsumers(Socket consumer,List<ConsumerNode> registeredUsers,Map<String,ArrayList<String>> map) {
        try {
            out = new ObjectOutputStream(consumer.getOutputStream());
            in = new ObjectInputStream(consumer.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.map = map;

    }
    public ConsumerNode getConsumer(){
        return con;
    }

    public ArtistName getArtistreceived(){ return artistreceived;}

    public void setArtistreceived(ArtistName artistreceived) {
        this.artistreceived = artistreceived;
    }

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
                artistreceived = artistName;
                //artistreceived.setArtistName(artistName.toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if(map.containsKey(artistreceived.getArtistName())){
                System.out.println("it is exist");
            }
            for (String name: map.keySet()){
                System.out.println("key is:"+name);
                if (name.toString().equals(artistreceived.getArtistName())){
                    System.out.println("Yes it is equal");
                    out.writeObject(map.get(name)); //πρεπει να στελνει μονο το arraylist αν το κλειδι ειναι αυτο που εστειλε ο consumer
                    out.flush();
                }



            }




            System.out.println("Assigning new thread for this client");


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
