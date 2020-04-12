import java.io.IOException;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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
    Map<Integer, Map<String, ArrayList<String>>> artists = new HashMap<>();

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
    public void pull(Socket ppconnection,ArtistName artist, String song) {

            System.out.println("Inside pull..");
            System.out.println("Name of the artist: " + artist.artistName + " and name of the song: " + song);
            int publisherPort = 0;
            for (Map.Entry<Integer, Map<String, ArrayList<String>>> entry : artists.entrySet()) {
                Map<String, ArrayList<String>> k = entry.getValue();

                if (k.containsKey(artist.artistName)) {
                    publisherPort = entry.getKey();
                    break;
                }
            }

            try {

                ppconnection = new Socket("localhost", publisherPort + 2);
                out3 = new ObjectOutputStream(ppconnection.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }


                try{
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


    public static class MyThread implements Runnable {

        // to stop the thread
        private boolean exit;
        private ArtistName artist;
        private String song;
        Thread t;
        protected Socket clientSocket = null;

        MyThread(Socket clientSocket,ArtistName artist,String song)
        {
            this.clientSocket = clientSocket;
            this.artist = artist;
            this.song = song;
            Thread t = new Thread(this, song);
            System.out.println("New thread: " + t);
            exit = false;
            t.start(); // Starting the thread

        }
        public void run( )
        {

            while (!exit) {
            System.out.println("Inside run!");

                brokers.get(0).pull(clientSocket,artist, song);


            }
        };

        // for stopping the thread
        public void stop()
        {
            exit = true;
        }
    }
    Queue<MyThread> threads = new Queue<MyThread>() {
        @Override
        public boolean add(MyThread myThread) {
            return false;
        }

        @Override
        public boolean offer(MyThread myThread) {
            return false;
        }

        @Override
        public MyThread remove() {
            return null;
        }

        @Override
        public MyThread poll() {
            return null;
        }

        @Override
        public MyThread element() {
            return null;
        }

        @Override
        public MyThread peek() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<MyThread> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends MyThread> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    };

    public static void main(String args[]) {

        BrokerNode b = new BrokerNode("localhost", 7654);
        BrokerNode b2 = new BrokerNode("localhost", 8765);
        BrokerNode b3 = new BrokerNode("localhost", 9876);
        b.init();
        b2.init();
        b3.init();
        brokers.add(b);
        brokers.add(b2);
        brokers.add(b3);


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
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                Socket finalConsumer = consumer;

                Thread consumer_thread = new Thread(() -> {

                    try {
                        broker.setOut(new ObjectOutputStream(finalConsumer.getOutputStream()));
                        broker.setIn(new ObjectInputStream(finalConsumer.getInputStream()));

                        String consumerip = broker.in.readUTF();
                        int consumerport = broker.in.readInt();

                        ConsumerNode cn = new ConsumerNode(consumerip, consumerport);

                        registeredUsers.add(cn);

                       // do {
                            ArtistName artistName = null;
                            String song = "";
                            try {
                                artistName = (ArtistName) broker.in.readObject();
                                broker.setArtistReceived(artistName);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                            if (registeredPublishers.get(0).hashTopic(artistName).getBrokerPort() == broker.getBrokerPort()) {

                                boolean f = false;

                                for (Map.Entry<Integer, Map<String, ArrayList<String>>> entry : artists.entrySet()) {

                                    Map<String, ArrayList<String>> k = entry.getValue();

                                    for (Map.Entry<String, ArrayList<String>> entry2 : k.entrySet()) {

                                        if (entry2.getKey() != null) {

                                            if (entry2.getKey().equals(broker.getArtistReceived().getArtistName()) && entry2.getKey() != null) {

                                                f = true;
                                                List<String> songs = entry2.getValue();

                                                if (!broker.p) {
                                                    broker.out.writeInt(broker.port);
                                                }

                                                broker.p = false;

                                                broker.out.writeInt(1);

                                                broker.out.writeObject(songs);
                                                broker.out.flush();
                                                Socket socket =null;
                                                song = broker.in.readUTF();
                                                artistName = broker.getArtistReceived();
                                                MyThread t2 = new MyThread(socket,broker.getArtistReceived(), song);//3000//3000-2500
                                                broker.threads.add(t2);


                                                break;
                                            }
                                        }
                                        if (f) {break; }
                                    }

                                   // t2.run();

                                    //Thread.sleep(500);
                                    //t2.stop();
                                    //Thread.sleep(500);
                                    if (f) {break; }
                                }

                                if (!exist) {
                                    broker.out.writeInt(0);
                                    broker.out.flush();
                                }
                            }
                            else {
                                int port = registeredPublishers.get(0).hashTopic(artistName).getBrokerPort();

                                broker.out.writeInt(port);
                                broker.out.flush();
                                broker.out.close();
                                broker.in.close();
                                broker.p = true;
                            }
                       // }while(!broker.threads.isEmpty());
                    }
                    catch (IOException | NoSuchAlgorithmException  e) {
                        e.printStackTrace();
                    }
                });
                consumer_thread.start();
                try {
                    consumer_thread.join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
