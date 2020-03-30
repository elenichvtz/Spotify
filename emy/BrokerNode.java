import java.io.IOException;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Server
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
            //this.publisher_providerSocket.setReuseAddress(true);
            //this.publisher_requestSocket = this.publisher_providerSocket.accept();
            System.out.println("broker provider socket connect");

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            this.consumer_providerSocket = new ServerSocket(this.port + 1, 10);
            //this.publisher_requestSocket = this.publisher_providerSocket.accept();
            System.out.println("broker consumer provider socket connect");

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*try {
            this.consumer_requestSocket = this.consumer_providerSocket.accept();
            //this.in = new ObjectInputStream(this.publisher_requestSocket.getInputStream());
            //this.out = new ObjectOutputStream(this.publisher_requestSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //System.out.println(registeredPublishers.isEmpty());

        this.key = calculateKeys();

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
        return brokers;
    }


    public void setBrokers(BrokerNode b) {
        brokers.add(b);
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
    public void pull(ArtistName artist) {

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

    public static void main(String args[]) {

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
        synchronized (b) {
            while (true) {
                try {
                    // socket object to receive incoming publisher
                    Socket publisher = b.getPublisherServerSocket().accept();

                    System.out.println("A new publisher is connected: " + publisher);
                    //ArtistName k = b.getArtistReceived();
                    ActionsForPublishers action = new ActionsForPublishers(publisher, registeredPublishers);
                    action.start();
                    registeredPublishers.add(action.getPublisher());
                    System.out.println(registeredPublishers.isEmpty());
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


                } catch (IOException ex) {
                    ex.printStackTrace();
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