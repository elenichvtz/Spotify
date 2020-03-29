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
import java.io.Serializable;

//Server
public class BrokerNode extends Thread implements Broker, Serializable {

    ServerSocket publisher_providerSocket;
    Socket publisher_requestSocket;
    ServerSocket consumer_providerSocket;
    Socket consumer_requestSocket;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

    BigInteger key;
    ArrayList<Publisher> publishers = new ArrayList<>();
    String ip;
    int port;

    BrokerNode(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void init() {

        brokers.add(new BrokerNode(this.ip, this.port));

        try {
            this.publisher_providerSocket = new ServerSocket(this.port, 10);
            System.out.println("broker provider socket connect");

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.consumer_providerSocket = new ServerSocket(this.port + 3, 10);
            System.out.println("broker consumer provider socket connect");

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.key = calculateKeys();

    }


    @Override
    public List<BrokerNode> getBrokers() {
        return brokers;
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
    public Publisher acceptConnection(Publisher publisher) {
        registeredPublishers.add(publisher);
        System.out.println("Connection accepted");
        return publisher;
    }

    @Override
    public Consumer acceptConnection(Consumer consumer) {
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

    public BigInteger getKey() { return this.key;}

    public static void main(String args[]) {

        BrokerNode b = new BrokerNode("127.0.0.1", 4321);
        b.init();

        System.out.println(brokers);

        //publisher 1
       // Thread t1 = new Thread() {
         //   public void run() {
                try {
                    // socket object to receive incoming publisher
                    Socket publisher = b.getPublisherServerSocket().accept();

                    System.out.println("A new publisher is connected: " + publisher);

                    try {
                        ObjectInputStream in = new ObjectInputStream(publisher.getInputStream());

                        //receive map, ip and port from publisher
                        String publisherip = in.readUTF();
                        System.out.println(publisherip);
                        int publisherport = in.readInt();
                        System.out.println(publisherport);
                        char start = in.readChar();
                        char end = in.readChar();
                        System.out.println(start + " & " + end);

                        Object publishermap = in.readObject();
                        maps.add((HashMap) publishermap);
                        System.out.println(maps);

                        PublisherNode pn = new PublisherNode(start, end, publisherip, publisherport);

                        b.acceptConnection(pn);
                        System.out.println(registeredPublishers.size());

                        ObjectOutputStream out = new ObjectOutputStream(publisher.getOutputStream());

                        out.writeObject(b.getKey());
                        out.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        //    }
       // };

        //publisher 2
        /*Thread t2 = new Thread() {

            public void run() {
                try {
                    // socket object to receive incoming publisher
                    Socket publisher = b.getPublisherServerSocket().accept();

                    System.out.println("A new publisher is connected: " + publisher);

                    try {
                        ObjectInputStream in = new ObjectInputStream(publisher.getInputStream());

                        //receive map, ip and port from publisher
                        String publisherip = in.readUTF();
                        System.out.println(publisherip);
                        int publisherport = in.readInt();
                        System.out.println(publisherport);
                        char start = in.readChar();
                        char end = in.readChar();
                        System.out.println(start + " & " + end);

                        Object publishermap = in.readObject();
                        maps.add((HashMap) publishermap);
                        System.out.println(maps);

                        PublisherNode pn = new PublisherNode(start, end, publisherip, publisherport);

                        b.acceptConnection(pn);
                        System.out.println(registeredPublishers.size());

                        ObjectOutputStream out = new ObjectOutputStream(publisher.getOutputStream());

                        out.writeObject(b.getKey());
                        out.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                } catch(IOException ex) {
                    ex.printStackTrace();
                }
            }
        };

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        //running infinite loop for getting client request
        while (true) {

            try {
                // socket object to receive incoming consumer requests
                Socket consumer = b.getConsumerServerSocket().accept();

                System.out.println("A new consumer is connected: " + consumer);

                try {
                    ObjectInputStream cin = new ObjectInputStream(consumer.getInputStream());
                    ObjectOutputStream cout = new ObjectOutputStream(consumer.getOutputStream());

                    //receive ip and port from consumer
                    String consumerip = cin.readUTF();
                    System.out.println("con " + consumerip);
                    int consumerport = cin.readInt();
                    System.out.println(consumerport);

                    ConsumerNode cn = new ConsumerNode(consumerip, consumerport);

                    registeredUsers.add(cn);
                    System.out.println(registeredUsers.isEmpty());

                    cout.writeUTF(b.getBrokerIP());
                    cout.writeInt(b.getBrokerPort());
                    cout.flush();

                    System.out.println("Assigning new thread for this client");

                    //receive artist's name from consumer
                    Object artistName = (ArtistName) cin.readObject();
                    System.out.println(artistName.toString()+" received from consumer");

                    ObjectOutputStream out = new ObjectOutputStream(b.getPublisherSocket().getOutputStream());

                    //send artistName to publisher
                    out.writeObject(artistName);

                    //create a new thread object
                    /*Thread consumerthread = new Thread() {
                        public void run() {
                            System.out.println("consumer thread not done yet");

                            //pairnei to artistname kai vlepei an to eksipiretei

                            //an oxi, vriskei poios einai o katallilos broker(lista brokers (?)) kai stelnei s auton to artistname kai ta stoixeia tou consumer

                            //an nai, zitaei apo ton sosto publisher(apo to maps) ta tragoudia tou artist

                            //an den uparxei o artist, gurizei ston comsumer minima oti den uparxei

                        }
                    };

                    consumerthread.start();*/

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
