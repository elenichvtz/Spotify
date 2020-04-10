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

//Client & Server
public class BrokerNode extends Thread implements Broker,Serializable {

    ServerSocket publisher_providerSocket;
    Socket publisher_requestSocket;
    ServerSocket consumer_providerSocket;
    Socket ppconnection;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

    ObjectOutputStream out2 = null;
    ObjectInputStream in2 = null;

    ObjectOutputStream out3 = null;
    ObjectInputStream in3 = null;
    ArtistName artistReceived= null;
    Map<String, ArrayList<String>> mapreceived = new HashMap<String, ArrayList<String>>();
    Map<Integer, Map<String, ArrayList<String>>> art2 = new HashMap<>();

    static boolean p = false;

    String ip;
    int port;

    BrokerNode(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public synchronized void init() {

        try {
            this.publisher_providerSocket = new ServerSocket(this.port, 10);
            System.out.println("broker provider socket connect");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.consumer_providerSocket = new ServerSocket(this.port + 1, 10);
            System.out.println("broker consumer provider socket connect");

        } catch (IOException e) {
            e.printStackTrace();
        }
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

        try {
            out = new ObjectOutputStream(this.publisher_requestSocket.getOutputStream());
            out.writeInt(calculateKeys().intValue());
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pull(ArtistName artist, String song) {

        System.out.println("Inside pull....");
        int publisherPort = 0;
        for (Map.Entry<Integer,  Map<String, ArrayList<String>>> entry : art2.entrySet()) {

            Map<String, ArrayList<String>> k = entry.getValue();
            if (k.containsKey(artist.artistName)) {
                publisherPort = entry.getKey();
                break;
            }
        }
        System.out.println("Correct publisher port is: "+publisherPort);

        try {
            ppconnection = new Socket("localhost",publisherPort+2);
            System.out.println("Connected as client tot Publisher");
            out3 = new ObjectOutputStream(ppconnection.getOutputStream());
            System.out.println("yo");
            in3 = new ObjectInputStream(ppconnection.getInputStream());
            System.out.println("yo");

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("it exists");
        MusicFile f = new MusicFile(song, artist.getArtistName(), null, null, null, 0, 0);
        Value value = new Value(f);
        try {
            this.out3.writeObject(artist);
            this.out3.writeObject(value);
            this.out3.flush();
            int numOfchunks = in3.readInt();
            out.writeInt(numOfchunks);
            out.flush();
            System.out.println("Chunks to receive from publisher are: "+numOfchunks);
            try {
                for (int i = 1; i <= numOfchunks; i++) {
                    MusicFile m = (MusicFile) in3.readObject();

                    System.out.println("Chunk id is: "+m.getChunkId());
                    out.writeObject(m);
                    out.flush();
                    System.out.println("Ola good");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOut(ObjectOutputStream out){this.out = out;}

    public void setIn(ObjectInputStream in) {this.in = in;}

    public void setOut2(ObjectOutputStream out){this.out2 = out;}

    public void setIn2(ObjectInputStream in) {this.in2 = in;}

    public void setMapReceived(Map<String,ArrayList<String>> map){ this.mapreceived = map; }

    public void setArt(Map<Integer, Map<String, ArrayList<String>>> art2) { this.art2 = art2; }

    public Map getMapReceived() { return mapreceived; }

    public void setArtistReceived(ArtistName artistReceived) { this.artistReceived = artistReceived; }

    public ArtistName getArtistReceived() { return artistReceived; }

    public ServerSocket getPublisherServerSocket() { return this.publisher_providerSocket; }

    public ServerSocket getConsumerServerSocket() { return this.consumer_providerSocket; }

    public int getBrokerPort() { return this.port; }

    public static void main(String args[]) {

        BrokerNode b = new BrokerNode("localhost", 7654);
        BrokerNode b2 = new BrokerNode("localhost", 8765);
        BrokerNode b3 = new BrokerNode("localhost", 9876);
        b.init();
        b2.init();
        b3.init();
        brokers.add(b);

        System.out.println(brokers.isEmpty());
        brokers.add(b2);
        brokers.add(b3);

        Map<Integer, Map<String, ArrayList<String>>> art = new HashMap<>();

        brokers.parallelStream().forEach((broker) -> {

            if(broker.getBrokerPort()!=9876) {

                try {

                    Socket publisher = broker.getPublisherServerSocket().accept();
                    broker.setOut2(new ObjectOutputStream(publisher.getOutputStream()));
                    broker.setIn2(new ObjectInputStream(publisher.getInputStream()));

                    //receive map, ip and port from publisher
                    String publisherip = broker.in2.readUTF();
                    System.out.println(publisherip);
                    int publisherport = broker.in2.readInt();
                    System.out.println(publisherport);
                    char start = broker.in2.readChar();
                    char end = broker.in2.readChar();
                    System.out.println(start + " & " + end);
                    System.out.println("Current broker port is: " + broker.getBrokerPort());

                    broker.setMapReceived((Map<String, ArrayList<String>>) broker.in2.readObject());

                    art.put(publisherport, broker.getMapReceived()); //add in art the maps received

                    System.out.println(broker.getMapReceived().toString());

                    PublisherNode pn = new PublisherNode(start, end, publisherip, publisherport);

                    registeredPublishers.add(pn);

                    System.out.println("Updating list with artist done");

                    registeredPublishers.add(pn);

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            broker.setArt(art);

            System.out.println("Is map empty?" + (broker.getMapReceived()).isEmpty());

            while (true) {

                try {
                    // socket object to receive incoming consumer requests
                    Socket consumer = broker.getConsumerServerSocket().accept();

                    broker.setOut(new ObjectOutputStream(consumer.getOutputStream()));
                    broker.setIn(new ObjectInputStream(consumer.getInputStream()));

                    String consumerip = broker.in.readUTF();
                    System.out.println("conumer ip is " + consumerip);
                    int consumerport = broker.in.readInt();
                    System.out.println("consumer port is "+consumerport);

                    ConsumerNode cn = new ConsumerNode(consumerip, consumerport);

                    registeredUsers.add(cn);

                    System.out.println(registeredUsers.isEmpty());

                    ArtistName artistName = null;
                    try {
                        artistName = (ArtistName) broker.in.readObject();
                        System.out.println(artistName.toString() + " received from consumer");
                        broker.setArtistReceived(artistName);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    System.out.println("yep "+registeredPublishers.get(0).hashTopic(artistName).getBrokerPort());

                    if(registeredPublishers.get(0).hashTopic(artistName).getBrokerPort() == broker.getBrokerPort()) {
                        System.out.println("Yessss");

                        if (broker.getMapReceived().containsKey(broker.getArtistReceived().getArtistName())) {
                            System.out.println("it is exist");
                        }

                        boolean f = false;

                        for (Map.Entry<Integer,  Map<String, ArrayList<String>>> entry : art.entrySet()) {

                            Map<String, ArrayList<String>> k = entry.getValue();
                            for(Map.Entry<String, ArrayList<String>> entry2 : k.entrySet()){

                                System.out.println("key is:" + entry2.getKey());
                                System.out.println(broker.getArtistReceived().getArtistName());
                                if (entry2.getKey().equals(broker.getArtistReceived().getArtistName()) && entry2.getKey() != null) {
                                    f = true;
                                    System.out.println("Yes it is equal");
                                    System.out.println(entry2.getValue().toString());
                                    List<String> songs = entry2.getValue();
                                    System.out.println("P is"+broker.p);
                                    if(!broker.p) {
                                        broker.out.writeInt(broker.port);
                                        System.out.println("Executed");
                                    }

                                    broker.p = false;

                                    broker.out.writeObject(songs);
                                    broker.out.flush();

                                    String song = broker.in.readUTF();

                                    System.out.println("Song received : "+song);
                                    System.out.println(art.size());

                                    broker.pull(broker.getArtistReceived(),song);
                                    break;
                                }
                            }
                            if (f){break;}
                        }
                    }
                    else {
                        System.out.println("Noooo "+registeredPublishers.get(0).hashTopic(artistName).getBrokerPort());

                        System.out.println("yo");
                        int port = registeredPublishers.get(0).hashTopic(artistName).getBrokerPort();
                        broker.out.writeInt(port);
                        broker.out.flush();
                        System.out.println("yo");
                        broker.out.close();
                        broker.in.close();
                        broker.p = true;
                    }

                    System.out.println("END");

                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
