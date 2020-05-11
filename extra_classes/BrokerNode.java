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
           // this.consumer_providerSocket = new ServerSocket(this.port + 1, 10);
            this.consumer_providerSocket = new ServerSocket(this.port+1 , 10);

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

        int publisherPort = 0;
        for (Map.Entry<Integer,  Map<String, ArrayList<String>>> entry : artists.entrySet()) {
            Map<String, ArrayList<String>> k = entry.getValue();

            if (k.containsKey(artist.artistName)) {
                publisherPort = entry.getKey();
                break;
            }
        }

        try {
            ppconnection = new Socket("192.168.1.3",publisherPort+2);
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
            String songName = in3.readUTF();
            int numOfchunks = in3.readInt();
            out.writeUTF(songName);
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
        int exit = 0;

        public MyThread(Socket s, BrokerNode b) {

            this.s = s;
            this.b = b;
        }
        public void stopThread() {
            this.exit = 1;
        }

        public void run() {

            //while (exit == 0) {
                try {


                    String consumerip = b.in.readUTF();
                    int consumerport = b.in.readInt();
                    System.out.println("Consumer connected");
                    //ConsumerNode cn = new ConsumerNode(consumerip, consumerport);

                    //registeredUsers.add(cn);

                    ArtistName artistName = null;
                    try {
                       String artist = b.in.readUTF();
                       artistName = new ArtistName(artist);
                       System.out.println("artist: " + artistName.getArtistName());
                       //artistName = (ArtistName) b.in.readObject();
                        b.setArtistReceived(artistName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("111111111: " + artistName.getArtistName());
                    if (registeredPublishers.get(0).hashTopic(artistName).getBrokerPort() == b.getBrokerPort()) {
                        System.out.println("00000000: " + artistName.getArtistName());
                        boolean f = false;

                        for (Map.Entry<Integer, Map<String, ArrayList<String>>> entry : artists.entrySet()) {

                            Map<String, ArrayList<String>> k = entry.getValue();

                            for (Map.Entry<String, ArrayList<String>> entry2 : k.entrySet()) {

                                if (entry2.getKey() != null) {

                                    if (entry2.getKey().equals(b.getArtistReceived().getArtistName()) && entry2.getKey() != null) {

                                        f = true;
                                        List<String> songs = entry2.getValue();

                                        if (!p) {
                                            System.out.println("2222222: " + artistName.getArtistName());
                                            out.writeInt(b.port);

                                           // out.writeUTF(b.ip);
                                        }

                                        p = false;
                                        System.out.println("333333333: " + artistName.getArtistName());
                                        out.writeInt(1);
                                        System.out.println("4444444: " + artistName.getArtistName());
                                        out.writeObject(songs);
                                        out.flush();

                                        String song = in.readUTF();

                                        b.pull(b.getArtistReceived(), song);
                                        System.out.println("Goodbye");
                                       // stopThread();

                                        break;
                                    }
                                }
                                if (f) {
                                    break;
                                }
                            }
                        }
                        if (!exist) {
                            out.writeInt(0);
                            out.flush();
                        }
                    } else {
                        int port = registeredPublishers.get(0).hashTopic(artistName).getBrokerPort();

                        b.out.writeInt(port);
                        b.out.flush();
                        b.out.close();
                        b.in.close();
                        b.p = true;
                    }
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

            //}
        }
    }



    public static void main(String args[]) {

        BrokerNode b = new BrokerNode("192.168.1.3", 7654);
        BrokerNode b2 = new BrokerNode("192.168.1.4", 8765);
        BrokerNode b3 = new BrokerNode("192.168.1.5", 9876);
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
                System.out.println("New customer!");
                MyThread t = new MyThread(finalConsumer, broker);
                threads.add(t);

                for (int i = 0; i < threads.size(); i++) {
                    threads.get(i).start();
                    if (!threads.get(i).isAlive()) {

                        try {
                            threads.get(i).join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        threads.remove(i);
                    }
                    else {

                        try {
                            threads.get(i).join(5);
                            threads.remove(i);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                }
            }

        });
    }
}
