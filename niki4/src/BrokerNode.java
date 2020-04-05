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
    ArrayList<Publisher> publishers = new ArrayList<>();
    String ip;
    int port;

    ///
    //static ArrayList<BrokerNode> ListOfBrokers = new ArrayList<>();
    Map<BrokerNode, BigInteger> BrokerKeys = new HashMap<>(); //map with brokers and their keys (calculated by calculateKeys())
    //
    //we keep a list with the connected publishers and when we have a song request we check the artist name and go to the right publisher
    ArrayList<PublisherNode> registeredPublishers = new ArrayList<>();
    //ArrayList<ConsumerNode> registeredConsumers = new ArrayList<>(); //idk the use of this yet

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

        /*try {
            this.consumer_requestSocket = this.consumer_providerSocket.accept();
            //this.in = new ObjectInputStream(this.publisher_requestSocket.getInputStream());
            //this.out = new ObjectOutputStream(this.publisher_requestSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //System.out.println(registeredPublishers.isEmpty());

        //send key to publisher
        /*try {
            this.out.writeObject(this.key);
            this.out.flush();
            System.out.println(this.key);
            System.out.println("flush");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.consumer_requestSocket = new Socket(this.ip, this.port+1);
            this.out = new ObjectOutputStream(this.consumer_requestSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //send key to consumer
        try {
            this.out.writeObject(this.key);
            this.out.flush();
            System.out.println(this.key);
            System.out.println("flushed");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    @Override
    public List<BrokerNode> getBrokers() {
        return ListOfBrokers;
    }


    public void setBrokers(BrokerNode b) {
        ListOfBrokers.add(b);
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





        //υποθέτω με την κλήση του action απο το Actionforclients εχει αρχικοποιηθεί ήδη το in και out

        /*try {
            in = new ObjectInputStream(this.publisher_requestSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*for(int i=0; i<registeredPublishers.size();i++) {
            for (String name : registeredPublishers.get(i).getArtistMap().keySet()){

                if(artist.getArtistName().equals())
            }
        }*/

    }

    public void setArtistReceived(ArtistName artistReceived) {
        this.artistReceived = artistReceived;
    }

    public ArtistName getArtistReceived() {
        return artistReceived;
    }

    public List<PublisherNode> getPublisherList() {
        return registeredPublishers;
    }

    public ServerSocket getPublisherServerSocket() {
        return this.publisher_providerSocket;
    }

    public Socket getPublisherSocket() {
        return this.publisher_requestSocket;
    }

    public ServerSocket getConsumerServerSocket() {
        return this.consumer_providerSocket;
    }

    public Socket getConsumerSocket() {
        return this.consumer_requestSocket;
    }

    public String getBrokerIP() {
        return this.ip;
    }

    public int getBrokerPort() {
        return this.port;
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