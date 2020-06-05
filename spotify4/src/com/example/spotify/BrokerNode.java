package com.example.spotify;

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
    static final long serialVersionUID = -373782829391231342L;
    ServerSocket publisher_providerSocket;
    Socket publisher_requestSocket;
    ServerSocket consumer_providerSocket;
    Socket ppconnection;
    Socket consumerconnection;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

    ObjectOutputStream out2 = null;
    ObjectInputStream in2 = null;

    ObjectOutputStream out3 = null;
    ObjectInputStream in3 = null;
    ArtistName artistReceived= null;
    Map<String, ArrayList<String>> mapreceived = new HashMap<String, ArrayList<String>>();
    Map<PublisherNode, Map<String, ArrayList<String>>> artists = new HashMap<>();
    ArrayList<MyThread> threads = new ArrayList<>();
    boolean p = false;
    boolean exist = false;

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

    }

    @Override
    public synchronized void  pull(ArtistName artist, String song ) {
        PublisherNode pn = null;
        for (Map.Entry<PublisherNode,  Map<String, ArrayList<String>>> entry : artists.entrySet()) {
            Map<String, ArrayList<String>> k = entry.getValue();

            if (k.containsKey(artist.artistName)) {
                pn = entry.getKey();
                break;
            }
        }

        try {
            ppconnection = new Socket(pn.getPublisherIP(),pn.getPublisherPort()+2);
            out3 = new ObjectOutputStream(ppconnection.getOutputStream());
            in3 = new ObjectInputStream(ppconnection.getInputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        MusicFile f = new MusicFile(song, artist.getArtistName(), null, null, null, 0, 0);

        Value value = new Value(f);

        try {

            out3.writeObject(artist);
            out3.writeObject(value);
            out3.flush();

            String songName = in3.readUTF();

            int numOfchunks = in3.readInt();
            out.writeUTF(songName);
            out.writeInt(numOfchunks);
            System.err.println("Number of chunks: " + numOfchunks);
            out.flush();

            try {
                for (int i = 1; i <= numOfchunks; i++) {

                    MusicFile m = (MusicFile) in3.readObject();
                    System.err.println("Sending song: " + m.getTrackName());

                    out.writeObject((MusicFile)m);

                    out.flush();
                    String ok = in.readUTF();

                    

                }
                out3.close();
                in3.close();
                ppconnection.close();


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

    public void setArtists(Map<PublisherNode, Map<String, ArrayList<String>>> artists) {


        for (Map.Entry<PublisherNode, Map<String, ArrayList<String>>> entry : artists.entrySet()) {
            this.artists.put(entry.getKey(), entry.getValue());
        }

    }

    public Map getMapReceived() { return mapreceived; }

    public void setArtistReceived(ArtistName artistReceived) { this.artistReceived = artistReceived; }

    public ArtistName getArtistReceived() { return artistReceived; }

    public ServerSocket getPublisherServerSocket() { return this.publisher_providerSocket; }

    public ServerSocket getConsumerServerSocket() { return this.consumer_providerSocket; }

    public int getBrokerPort() { return this.port; }

    public String getBrokerIp(){
        return this.ip;
    }

    public  class MyThread extends Thread implements Serializable{

        Socket s = null;
        Boolean flag = false;
        BrokerNode b ;
        int exit = 0;
        PublisherNode pub;
        static final long serialVersionUID =-373782829391231342L;
        public MyThread(Socket s, BrokerNode b, PublisherNode pub ) {

            this.s = s;
            this.b = b;
            this.pub = pub;
        }
        public void stopThread() {
            this.exit = 1;
        }

        public void run() {

            try {
                try {
                    setOut(new ObjectOutputStream(s.getOutputStream()));
                    setIn(new ObjectInputStream(s.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String consumerip = in.readUTF();
                int consumerport = in.readInt();
                System.out.println("Consumer connected");
                System.out.println("consumer ip: " + consumerip + " consumerport: " + consumerport);


                ArtistName artistName = null;
                try {
                    String artist = in.readUTF();
                    artistName = new ArtistName(artist);

                    setArtistReceived(artistName);
                    System.out.println("Ip and port of broker: " + b.ip + " " + b.port);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (pub.hashTopic(artistName).getBrokerPort() == getBrokerPort()) {

                    boolean f = false;

                    for (Map.Entry<PublisherNode, Map<String, ArrayList<String>>> entry : artists.entrySet()) {

                        Map<String, ArrayList<String>> k = entry.getValue();

                        for (Map.Entry<String, ArrayList<String>> entry2 : k.entrySet()) {

                            if (entry2.getKey() != null) {

                                if (entry2.getKey().equals(getArtistReceived().getArtistName()) && entry2.getKey() != null) {


                                    f = true;
                                    List<String> songs = entry2.getValue();

                                    if (!p) {

                                        out.writeInt(port);

                                        // out.writeUTF(b.ip);
                                    }

                                    p = false;

                                    out.writeInt(1);

                                    out.writeInt(songs.size());

                                    out.flush();
                                    for(int j=0; j<songs.size();j++) {

                                        out.writeUTF(songs.get(j));
                                        out.flush();
                                    }

                                    String song = in.readUTF();
                                    System.out.println("song selected: " + song);
                                    pull(getArtistReceived(), song);
                                    System.out.println("Goodbye");


                                    break;
                                }
                            }
                            if (f) {
                                break;
                            }
                        }
                    }
                    if (!exist ) {
                        out.writeInt(port);
                        out.writeInt(0);
                        out.flush();


                    }
                } else {
                    System.out.println("Changing Broker");
                    int port = registeredPublishers.get(0).hashTopic(artistName).getBrokerPort();

                    out.writeInt(port);
                    out.flush();
                    out.close();
                    in.close();
                    p = true;
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        }
    }

    public  class MyThread2 extends Thread implements Serializable {


        BrokerNode b;
        ArrayList<MyThread3> threads = new ArrayList<>();

        static final long serialVersionUID = -3752672829391231342L;

        public MyThread2(BrokerNode b) {
            this.b = b;

        }

        public void run() {
            while(true){
                try {
                    Socket publisher = b.getPublisherServerSocket().accept();
                    MyThread3 thread = new MyThread3(b, publisher);
                    thread.start();
                    threads.add(thread);
                    for (int k = threads.size()-1; k >-1; k--) {
                        //  threads.get(k).start();
                        if (!threads.get(k).isAlive()) {

                            try {
                                threads.get(k).join(1000);

                                threads.remove(k);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                catch(IOException e){
                    e.printStackTrace();
                }

            }
        }
    }

    public  class MyThread3 extends Thread implements Serializable {


        BrokerNode b;
        Socket publisher;


        static final long serialVersionUID = -3752672829391231342L;

        public MyThread3(BrokerNode b, Socket publisher) {
            this.b = b;
            this.publisher = publisher;

        }

        public void run() {
            b.pubRequest(b, publisher);
        }
    }

    public void pubRequest(BrokerNode broker, Socket publisher){

            try {

                broker.setOut2(new ObjectOutputStream(publisher.getOutputStream()));
                broker.setIn2(new ObjectInputStream(publisher.getInputStream()));

                //receive map, ip and port from publisher
                String publisherip = broker.in2.readUTF();
                int publisherport = broker.in2.readInt();
                char start = broker.in2.readChar();
                char end =broker.in2.readChar();

                broker.setMapReceived((Map<String, ArrayList<String>>)broker.in2.readObject());
                PublisherNode pn = new PublisherNode(start, end, publisherip, publisherport);

                artists.put(pn, broker.getMapReceived()); //add in art the maps received
                registeredPublishers.add(pn);

                System.out.println("registered publishers :  " + pn.getPublisherIP() + " " + pn.getPublisherPort());

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }


        broker.setArtists(artists);
    }

    public void conRequest( ){
        while(true) {
            //socket object to receive incoming consumer requests
            Socket consumer = null;

            try {
                consumer = getConsumerServerSocket().accept();

            } catch (IOException e) {
                e.printStackTrace();
            }

            Socket finalConsumer = consumer;

            System.out.println("New customer!");
            MyThread t = new MyThread(finalConsumer, this, registeredPublishers.get(0));
            t.start();
            threads.add(t);

            for (int k = threads.size()-1; k >-1; k--) {
              //  threads.get(k).start();
                if (!threads.get(k).isAlive()) {

                    try {
                        threads.get(k).join(1000);

                        threads.remove(k);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }

        }
    }

    public void startPubThread(){
        MyThread2 thread = new MyThread2(this);
        thread.start();
    }



    public static void main(String args[]) {

      BrokerNode b = new BrokerNode("192.168.1.15", 7654);
      //BrokerNode b = new BrokerNode("192.168.1.15", 8765);
       //BrokerNode b = new BrokerNode("192.168.1.15", 9876);
        b.init();

        Map<Integer, Map<String, ArrayList<String>>> artists = new HashMap<>();
        b.startPubThread();
        b.conRequest();




    }
}
