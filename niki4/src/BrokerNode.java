import javax.sound.sampled.Port;
import java.io.IOException;
import java.io.*;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrokerNode extends Thread implements Broker,Serializable {

    ServerSocket publisher_providerSocket;
    Socket publisher_requestSocket;
    ServerSocket consumer_providerSocket;
    Socket consumer_requestSocket;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    ArtistName artistReceived= null;

    BigInteger key;
    //ArrayList<PublisherNode> publishers = new ArrayList<>();
    String ip;
    int port;

    ///
    //static ArrayList<BrokerNode> ListOfBrokers = new ArrayList<>();
    Map<BrokerNode, BigInteger> BrokerKeys = new HashMap<>(); //map with brokers and their keys (calculated by calculateKeys())
    //
    //we keep a list with the connected publishers and when we have a song request we check the artist name and go to the right publisher
    ArrayList<PublisherNode> registeredPublishers = new ArrayList<>();
    //ArrayList<ConsumerNode> registeredConsumers = new ArrayList<>(); //idk the use of this yet
    //
    ArrayList<ArtistName> artists = new ArrayList<>();
    Map<Integer, ArrayList<ArtistName>> PortArtist = new HashMap<>(); //map that tells us which artists every port (broker) has

    //marina
    ArrayList<ArtistName> brokerArtist = new ArrayList<>(); //list with artists for this broker???


    BrokerNode(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run() {
    }

    @Override
    public synchronized void init() {

        try {
            this.publisher_providerSocket = new ServerSocket(this.port, 10);
            System.out.println("broker - publisher providerSocket connect");

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.consumer_providerSocket = new ServerSocket(this.port + 100, 10);
            System.out.println("broker - consumer providerSocket connect");

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.key = calculateKeys();
        BrokerKeys.put(this, key);
    }


    @Override
    public List<BrokerNode> getBrokers() {
        return ListOfBrokers;
    }

    public int getBrokerPort() { return this.port; }

    public void setBrokers(BrokerNode b) {
        ListOfBrokers.add(b);
    }

    /*public ArrayList<ArtistName> matchArtists (){
        for every artist
        hashTopic(artist).

        //we call hashtopic and get the port of the broker that is returned
        //we put <that port, artist> in PortArtist
    }*/

    //marina
    @Override
    public Map<Integer, ArrayList<ArtistName>> findBroker(List<BrokerNode> brokers) throws NoSuchAlgorithmException {

        for(Map.Entry<ArtistName,ArrayList<String>> entry1 : registeredPublishers.get(0).getArtistMap().entrySet()) {


            for (Map.Entry<Integer, ArrayList<ArtistName>> entry : PortArtist.entrySet()) {
                if (registeredPublishers.get(0).hashTopic(entry1.getKey()).port == brokers.get(0).port) {
                    brokerArtist.add(entry1.getKey());
                    PortArtist.put(brokers.get(0).port, brokerArtist);
                }
                if (registeredPublishers.get(0).hashTopic(entry1.getKey()).port == brokers.get(1).port) {
                    brokerArtist.add(entry1.getKey());
                    PortArtist.put(brokers.get(1).port, brokerArtist);
                }
                if (registeredPublishers.get(0).hashTopic(entry1.getKey()).port == brokers.get(2).port) {
                    brokerArtist.add(entry1.getKey());
                    PortArtist.put(brokers.get(2).port, brokerArtist);
                }
            }
        }
        return PortArtist;
    }

    //marina
    public Map<Integer, ArrayList<ArtistName>> getBrokerMap() throws NoSuchAlgorithmException {
        PortArtist = findBroker(ListOfBrokers);
        return PortArtist;
    }



    @Override
    public BigInteger calculateKeys() {
        String s = ip + port;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(s.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);

            return no;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void connect() {
        try {
            this.publisher_requestSocket = this.publisher_providerSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            /////////////////
            this.consumer_requestSocket = this.consumer_providerSocket.accept();
            PortArtist.put(this.consumer_providerSocket.getLocalPort(), artists);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            this.publisher_requestSocket.close();
            this.publisher_providerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PublisherNode acceptConnection(PublisherNode publisher) {
        registeredPublishers.add(publisher);
        System.out.println("Connection accepted");
        return publisher;
    }

    @Override
    public ConsumerNode acceptConnection(ConsumerNode consumer) {
        registeredUsers.add(consumer);
        System.out.println("Connection accepted");
        return consumer;
    }

    @Override
    public void notifyPublisher(String name) {
        //Θα ενημερωνει ο broker τον καθε publisher για ποια κλειδια ειναι υπευθυνοι (για ποιο ευρος τιμων)

        //mhpws

        try {
            out = new ObjectOutputStream(this.publisher_requestSocket.getOutputStream());
            out.writeInt(calculateKeys().intValue());
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pull(ArtistName artist, String title) {

        for(PublisherNode publisher : registeredPublishers){
            if(artist.getArtistName().charAt(0) >= publisher.getStart() && artist.getArtistName().charAt(0) <= publisher.getEnd()){ //we find the right publisher

                try {
                    //send request <arist, title> to publisher
                    publisher.out.writeObject(artist);
                    publisher.out.writeObject(title);
                    Value valueSent = (Value) publisher.in.readObject(); //we read the value that was sent in push

                    //TODO: we send the value to consumer
                    //consumer.out.writeObject(valueSent);
                    //the consumer will readObject the value and play it in playData

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) throws IOException {

        //
        BrokerNode b1 = new BrokerNode("localhost", 3456);
        BrokerNode b2 = new BrokerNode("localhost", 7890);
        BrokerNode b3 = new BrokerNode("localhost", 9321);
        ListOfBrokers.add(b1);
        ListOfBrokers.add(b2);
        ListOfBrokers.add(b3);

        for(BrokerNode broker : ListOfBrokers){
            broker.init();
            while (true) {
                broker.connect(); //broker.publisher_requestSocket = broker.publisher_providerSocket.accept();
                //now we have established a connection between broker & publisher
                /*
                The accept method waits until a client starts up and requests a connection on the host and port of this server.
                When a connection is requested and successfully established, the accept method returns a new Socket object
                which is bound to the same local port and has its remote address and remote port set to that of the client.
                The server can communicate with the client over this new Socket and continue to listen
                for client connection requests on the original ServerSocket.
                */

                //create a thread to deal with the client
                //we tell publisher which key range this broker is responsible for
            }
        }

        /* from emy

        BrokerNode b = new BrokerNode("localhost", 7654);
        //BrokerNode b2 = new BrokerNode("localhost", 7655);
        //BrokerNode b3 = new BrokerNode("localhost", 7656);
        b.init();
        //b2.init();
        //b3.init();
        b.setBrokers(b);
        //List<Broker> p = b.getBrokers();
        //p.add(b);
        System.out.println(brokers.isEmpty());
        //brokers.add(b2);
        //brokers.add(b3);
        // socket object to receive incoming publisher
        Socket publisher = b.getPublisherServerSocket().accept();

        synchronized (b) {

            System.out.println("A new publisher is connected: " + publisher);
            //ArtistName k = b.getArtistReceived();
            ActionsForPublishers action = new ActionsForPublishers(publisher, registeredPublishers);
            action.start();
            registeredPublishers.add(action.getPublisher());
            System.out.println(registeredPublishers.isEmpty());

            while (true) {

                if(b.getArtistReceived()!= null){
                    MusicFile f = new MusicFile(b.getArtistReceived().getArtistName(),null,null,null,null,0,0);
                    Value val = new Value(f);
                    for(int i=0;i <b.getPublisherList().size();i++){
                        for(String name : b.getPublisherList().get(i).getArtistMap().keySet()){
                            if (name.equals(b.getArtistReceived())){
                                b.getPublisherList().get(i).push(b.getArtistReceived(),val);
                            }
                        }
                    }
                }


                //running infinite loop for getting client request


                try {
                    // socket object to receive incoming consumer requests
                    Socket consumer = b.getConsumerServerSocket().accept();

                    ActionsForConsumers action2 = new ActionsForConsumers(consumer, registeredUsers, brokers);
                    action2.start();
                    System.out.println("A new consumer is connected: " + consumer);
                    registeredUsers.add(action2.getConsumer());
                    b.setArtistReceived(action2.getArtistreceived());
                    System.out.println("Consumer list is empty?: " + registeredPublishers.isEmpty());


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        */

    }
}
/*
    public static void main() { //
        BrokerNode b2 = new BrokerNode("localhost", 7655);
        b2.init();
        while(true){
            try {
                // socket object to receive incoming publisher
                Socket publisher = b2.getPublisherServerSocket().accept();

                System.out.println("A new publisher is connected: " + publisher);
                ActionsForClients action = new ActionsForClients(publisher, registeredPublishers);
                action.start();
                registeredPublishers.add(action.getPublisher());
                System.out.println(registeredPublishers.isEmpty());


            } catch (IOException ex) {
                ex.printStackTrace();
            }


            //running infinite loop for getting client request


            try {
                // socket object to receive incoming consumer requests
                Socket consumer = b2.getConsumerServerSocket().accept();

                ActionsForClients2 action2 = new ActionsForClients2(consumer,registeredUsers,brokers);
                action2.start();
                System.out.println("A new consumer is connected: " + consumer);
                registeredUsers.add(action2.getConsumer());
                System.out.println("Consumer list is empty?: "+registeredPublishers.isEmpty());



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
*/