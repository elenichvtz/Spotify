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
import java.util.concurrent.atomic.AtomicReference;

//Client & Server
public class BrokerNode extends Thread implements Broker,Serializable {

    ServerSocket publisher_providerSocket;
    Socket publisher_requestSocket;
    ServerSocket consumer_providerSocket;
    Socket ppconnection;
    Socket consumerconnection;
    static ObjectOutputStream out = null;
    static ObjectInputStream in = null;
    static ObjectOutputStream out4 = null;
    ObjectOutputStream out2 = null;
    ObjectInputStream in2 = null;

    static ObjectOutputStream out3 = null;
    static ObjectInputStream in3 = null;
    ArtistName artistReceived= null;
    Map<String, ArrayList<String>> mapreceived = new HashMap<String, ArrayList<String>>();
    static Map<Integer, Map<String, ArrayList<String>>> artists = new HashMap<>();

    static boolean p = false;
    static boolean exist = false;

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.consumer_providerSocket = new ServerSocket(this.port + 1, 10);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Broker connected.");
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
        return publisher;
    }

    @Override
    public ConsumerNode acceptConnection(ConsumerNode consumer) {
        registeredUsers.add(consumer);
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
    public synchronized void  pull(ArtistName artist, String song ) {
        System.out.println("Inside pull");
        int publisherPort = 0;
        for (Map.Entry<Integer,  Map<String, ArrayList<String>>> entry : artists.entrySet()) {
            Map<String, ArrayList<String>> k = entry.getValue();

            if (k.containsKey(artist.artistName)) {
                publisherPort = entry.getKey();
                break;
            }
        }

        try {
            ppconnection = new Socket("localhost",publisherPort+2);
            out3 = new ObjectOutputStream(ppconnection.getOutputStream());
            in3 = new ObjectInputStream(ppconnection.getInputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        MusicFile f = new MusicFile(song, artist.getArtistName(), null, null, null, 0, 0);

        Value value = new Value(f);

        try {
            this.out3.writeObject(artist);
            this.out3.writeObject(value);
            this.out3.flush();
            int numOfchunks = in3.readInt();
            out.writeInt(numOfchunks);
            out.flush();
            System.out.println(artist);
            System.out.println(song);
            try {
                for (int i = 1; i <= numOfchunks; i++) {

                    MusicFile m = (MusicFile) in3.readObject();
                    out.writeObject(m);
                    out.flush();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Out of pull");
    }

    public void setOut(ObjectOutputStream out){this.out = out;}

    public void setIn(ObjectInputStream in) {this.in = in;}

    public void setOut2(ObjectOutputStream out){this.out2 = out;}

    public void setIn2(ObjectInputStream in) {this.in2 = in;}

    public void setMapReceived(Map<String,ArrayList<String>> map){ this.mapreceived = map; }

    public void setArtists(Map<Integer, Map<String, ArrayList<String>>> artists) { this.artists = artists; }

    public Map getMapReceived() { return mapreceived; }

    public void setArtistReceived(ArtistName artistReceived) { this.artistReceived = artistReceived; }

    public ArtistName getArtistReceived() { return artistReceived; }

    public ServerSocket getPublisherServerSocket() { return this.publisher_providerSocket; }

    public ServerSocket getConsumerServerSocket() { return this.consumer_providerSocket; }

    public int getBrokerPort() { return this.port; }

    public static class MyThread extends Thread {

        Socket s = null;
        Boolean flag = false;
        BrokerNode b ;

        public MyThread(Socket s, BrokerNode b) {

            this.s = s;
            this.b = b;
        }

        public void run(){
            try {

            System.out.println("Before reading consumer");
            String consumerip = b.in.readUTF();
            int consumerport = b.in.readInt();
            System.out.println("After reading consumer");
            ConsumerNode cn = new ConsumerNode(consumerip, consumerport);

            registeredUsers.add(cn);

            ArtistName artistName = null;
            try {
                artistName = (ArtistName) b.in.readObject();
                b.setArtistReceived(artistName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (registeredPublishers.get(0).hashTopic(artistName).getBrokerPort() == b.getBrokerPort()) {

                boolean f = false;

                for (Map.Entry<Integer, Map<String, ArrayList<String>>> entry : artists.entrySet()) {

                    Map<String, ArrayList<String>> k = entry.getValue();

                    for (Map.Entry<String, ArrayList<String>> entry2 : k.entrySet()) {

                        if (entry2.getKey() != null) {

                            if (entry2.getKey().equals(b.getArtistReceived().getArtistName()) && entry2.getKey() != null) {

                                f = true;
                                List<String> songs = entry2.getValue();

                                if (!p) {
                                    out.writeInt(b.port);
                                }

                                p = false;

                                out.writeInt(1);

                                out.writeObject(songs);
                                out.flush();

                                String song = in.readUTF();

                                b.pull(b.getArtistReceived(), song);

                                break;
                            }
                        }
                        if (f) { break; }
                    }
                }
                if (!exist) {
                    out.writeInt(0);
                    out.flush();
                }
            }
            else {
                int port = registeredPublishers.get(0).hashTopic(artistName).getBrokerPort();

                b.out.writeInt(port);
                b.out.flush();
                b.out.close();
                b.in.close();
                b.p = true;
            }
        }
                    catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        }
    }



    public static void main(String args[]) {

        BrokerNode b = new BrokerNode("127.0.0.1", 7654);
        BrokerNode b2 = new BrokerNode("127.0.0.2", 8765);
        BrokerNode b3 = new BrokerNode("127.0.0.3", 9876);
        b.init();
        b2.init();
        b3.init();
        brokers.add(b);
        brokers.add(b2);
        brokers.add(b3);
        ArrayList<MyThread> threads = new ArrayList<>();

        Map<Integer, Map<String, ArrayList<String>>> artists = new HashMap<>();

        brokers.parallelStream().forEach((broker) -> {

            if(broker.getBrokerPort()!=9876) {

                try {

                    Socket publisher = broker.getPublisherServerSocket().accept();
                    broker.setOut2(new ObjectOutputStream(publisher.getOutputStream()));
                    broker.setIn2(new ObjectInputStream(publisher.getInputStream()));

                    //receive map, ip and port from publisher
                    String publisherip = broker.in2.readUTF();
                    int publisherport = broker.in2.readInt();
                    char start = broker.in2.readChar();
                    char end = broker.in2.readChar();

                    broker.setMapReceived((Map<String, ArrayList<String>>) broker.in2.readObject());

                    artists.put(publisherport, broker.getMapReceived()); //add in art the maps received

                    PublisherNode pn = new PublisherNode(start, end, publisherip, publisherport);

                    registeredPublishers.add(pn);

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            broker.setArtists(artists);

            while (true) {

                //socket object to receive incoming consumer requests
                Socket consumer = null;

                try {
                    consumer = broker.getConsumerServerSocket().accept();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Socket finalConsumer = consumer;
                try {
                    b.setOut(new ObjectOutputStream(finalConsumer.getOutputStream()));
                    b.setIn(new ObjectInputStream(finalConsumer.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MyThread t = new MyThread(finalConsumer, broker);
                t.start();

                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


             }

        });
    }
}
