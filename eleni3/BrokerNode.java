import java.io.IOException;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//Server
public class BrokerNode extends Thread implements Broker,Serializable, Runnable {

    ServerSocket publisher_providerSocket;
    Socket publisher_requestSocket;
    Socket publisher_requestSocket2;
    ServerSocket consumer_providerSocket;
    Socket consumer_requestSocket;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    ArtistName artistReceived= null;
    Map<String, ArrayList<String>> mapreceived = new HashMap<String, ArrayList<String>>();
    Map<String, ArrayList<String>> mapreceived2 = new HashMap<String, ArrayList<String>>();


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
            //this.in = new ObjectInputStream(this.publisher_requestSocket.getInputStream());
            //this.out = new ObjectOutputStream(this.publisher_requestSocket.getOutputStream());
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
        //this.key = calculateKeys();
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
        //System.out.println("S is..: "+s);
        //String k = ip + "8765";
        //System.out.println("K is..: "+k);
        //String n = ip + "9876";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
           // MessageDigest md5 = MessageDigest.getInstance("MD5");
            //MessageDigest md2 = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(s.getBytes());

            //byte[] messageDigests = md.digest(k.getBytes());
            //byte[] messageDigestss = md.digest(n.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);
            //BigInteger no2 = new BigInteger(1, messageDigests);
            //BigInteger no3 = new BigInteger(1, messageDigestss);

            //System.out.println("No  is :"+no);
            //System.out.println("No2 is :"+no2);
            //System.out.println("No3 is :"+no3);



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
            this.publisher_requestSocket2 = this.publisher_providerSocket.accept();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            this.publisher_requestSocket.close();
            this.publisher_requestSocket2.close();
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
            out = new ObjectOutputStream(this.publisher_requestSocket2.getOutputStream());
            out.writeInt(calculateKeys().intValue());
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pull(ArtistName artist, String song) {

        if(mapreceived.containsKey(artistReceived.getArtistName())){
            System.out.println("it is exist");

        }
        /*if(mapreceived2){
            do for mapreceived from second publisher
        */

        //
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


    public void setOut(ObjectOutputStream out){this.out = out;}

    public void setIn(ObjectInputStream in) {this.in = in;}

    public void setMapReceived(Map map){
        this.mapreceived = map;
    }

    public Map getMapReceived() {
        return this.mapreceived;
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

    public Socket getPublisherSocket2() { return this.publisher_requestSocket2; }

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

        BrokerNode b = new BrokerNode("localhost", 7654);
        BrokerNode b2 = new BrokerNode("localhost", 8765);
        BrokerNode b3 = new BrokerNode("localhost", 9876);
        b.init();
        b2.init();
        b3.init();
        b.setBrokers(b);
        System.out.println("Key is: "+b.calculateKeys());

        System.out.println(brokers.isEmpty());


        b2.setBrokers(b2);
        b3.setBrokers(b3);

        //publisher port is key and map with artists and songs is data
        Map<Integer, Map<String, ArrayList<String>>> art = new HashMap<>();

        System.out.println(b3.getBrokers().size());

        // socket object to receive incoming publisher
        b3.getBrokers().parallelStream().forEach((broker) -> {
            //broker.run();

            //Socket publisher = b.getPublisherServerSocket().accept();


            Thread t1 = new Thread(){
                public void run() {
                    try {
                        Socket publisher = broker.getPublisherServerSocket().accept();

                        System.out.println("A new publisher is connected: " + publisher);

                        //ObjectOutputStream out = new ObjectOutputStream(publisher.getOutputStream());
                        //ObjectInputStream in = new ObjectInputStream(publisher.getInputStream());
                        broker.setOut(new ObjectOutputStream(publisher.getOutputStream()));
                        broker.setIn(new ObjectInputStream(publisher.getInputStream()));

                        //receive map, ip and port from publisher
                        String publisherip = broker.in.readUTF();
                        System.out.println(publisherip);
                        int publisherport = broker.in.readInt();
                        System.out.println(publisherport);
                        char start = broker.in.readChar();
                        char end = broker.in.readChar();
                        System.out.println(start + " & " + end);

                        broker.setMapReceived((Map<String, ArrayList<String>>) broker.in.readObject());

                        art.put(publisherport, broker.getMapReceived());

                        System.out.println(broker.getBrokerPort() + " " + broker.getMapReceived().toString());

                        System.out.println("art size "+art.size());

                        PublisherNode pn = new PublisherNode(start, end, publisherip, publisherport);

                        registeredPublishers.add(pn);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Is map empty? " + (broker.getMapReceived()).isEmpty());
                }};

            t1.start();

                //System.out.println(registeredPublishers.isEmpty());

            //System.out.println("art size1 "+art.size());

                while (true) {

                    /*if (broker.getArtistReceived() != null) {
                        System.out.println(broker.getArtistReceived());
                        //      b.pull(b.getArtistReceived());
                    }*/

                    Thread c = new Thread(){
                        public void run() {

                            try {
                                // socket object to receive incoming consumer requests
                                Socket consumer = broker.getConsumerServerSocket().accept();

                                broker.setOut(new ObjectOutputStream(consumer.getOutputStream()));
                                broker.setIn(new ObjectInputStream(consumer.getInputStream()));

                                //broker.out.writeInt(broker.getBrokerPort());

                                String consumerip = broker.in.readUTF();
                                System.out.println("con " + consumerip);
                                int consumerport = broker.in.readInt();
                                System.out.println(consumerport);

                                ConsumerNode cn = new ConsumerNode(consumerip, consumerport);

                                //registeredUsers.add(cn);
                                System.out.println(registeredUsers.isEmpty());
                                ArtistName artistName = null;
                                try {
                                    artistName = (ArtistName) broker.in.readObject();
                                    System.out.println(artistName.toString() + " received from consumer");
                                    broker.setArtistReceived(artistName);
                                    //artistreceived.setArtistName(artistName.toString());
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }

                                //find the broker who has the artist
                                //return the port to consumer even if its mine

                                System.out.println("yep "+registeredPublishers.get(0).hashTopic(artistName).getBrokerPort());


                                if(registeredPublishers.get(0).hashTopic(artistName).getBrokerPort() == broker.getBrokerPort()) {
                                    System.out.println("Yessss");

                                    broker.out.writeInt(registeredPublishers.get(0).hashTopic(artistName).getBrokerPort());

                                    if (broker.getMapReceived().containsKey(broker.getArtistReceived().getArtistName())) {
                                        System.out.println("it is exist");
                                    }
                                    Map<String, ArrayList<String>> mapreceived = broker.getMapReceived();
                                    for (String name : mapreceived.keySet()) {
                                        //System.out.println("key is:" + name);
                                        if (name.toString().equals(broker.getArtistReceived().getArtistName())) {
                                            System.out.println("Yes it is equal");
                                            broker.out.writeObject(broker.getMapReceived().get(name)); //πρεπει να στελνει μονο το arraylist αν το κλειδι ειναι αυτο που εστειλε ο consumer
                                            broker.out.flush();
                                        }
                                    }
                                }
                                else {

                                    System.out.println("Noooo "+registeredPublishers.get(0).hashTopic(artistName).getBrokerPort());

                                    System.out.println("yo");
                                    int port = registeredPublishers.get(0).hashTopic(artistName).getBrokerPort();
                                    broker.out.writeInt(port);
                                    broker.out.flush();
                                    System.out.println("yooo");
                                }

                            } catch (IOException | NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        }};
                    c.start();
                }
        });

    }
}
