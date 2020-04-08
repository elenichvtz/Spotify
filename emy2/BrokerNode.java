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


//Server
public class BrokerNode extends Thread implements Broker,Serializable {

    ServerSocket publisher_providerSocket;
    Socket publisher_requestSocket;
    ServerSocket consumer_providerSocket;
    Socket consumer_requestSocket;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    ArtistName artistReceived= null;
    Map<String, ArrayList<String>> mapreceived = new HashMap<String, ArrayList<String>>();
    Map<String, ArrayList<String>> mapreceived2 = new HashMap<String, ArrayList<String>>();
    ArrayList<ArtistName>  myartists = new ArrayList<>();
    Map<Integer, ArrayList<ArtistName>> emy = new HashMap<>();

    BigInteger key;
    ArrayList<Publisher> publishers = new ArrayList<>();

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
            byte[] messageDigest = md.digest(s.getBytes());


            BigInteger no = new BigInteger(1, messageDigest);

            return no;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*public Map<Integer, ArrayList<ArtistName>> updateList(List<BrokerNode> broker,PublisherNode p,List<Map<String,ArrayList<String>>> tom ) throws NoSuchAlgorithmException {
        Map<Integer, ArrayList<ArtistName>> map = new HashMap<>();
        //for(int i =0; i < tom.size();i++){
            for(String name: mapreceived.keySet()) {
                if (!name.isBlank() && !name.isEmpty() && !name.equals("null")){
                    ArtistName art = new ArtistName(name);
                    System.out.println("Inside updateList " + name);
                    if (p.hashTopic(art).port == broker.get(0).port) {
                        if (!map.containsKey(broker.get(0).port)) {
                            System.out.println("Adding to broker with port: " + broker.get(0).port + " " + 0);
                            ArrayList<ArtistName> m = new ArrayList<>();
                            m.add(art);
                            map.put(broker.get(0).port, m);
                        } else {
                            ArrayList<ArtistName> t1 = map.get(broker.get(0).port);
                            t1.add(art);
                            System.out.println("Adding to broker with port: " + broker.get(0).port + " " + 0);
                            map.put(broker.get(0).port, t1);
                        }
                    } else if (p.hashTopic(art).port == broker.get(1).port) {
                        if (!map.containsKey(broker.get(1).port)) {
                            System.out.println("Adding to broker with port: " + broker.get(1).port + " " + 1);
                            ArrayList<ArtistName> m2 = new ArrayList<>();
                            m2.add(art);
                            map.put(broker.get(1).port, m2);
                        } else {
                            ArrayList<ArtistName> t2 = map.get(broker.get(1).port);
                            t2.add(art);
                            System.out.println("Adding to broker with port: " + broker.get(1).port + " " + 1);
                            map.put(broker.get(1).port, t2);

                        }
                    } else if (p.hashTopic(art).port == broker.get(2).port) {
                        if (!map.containsKey(broker.get(2).port)) {
                            System.out.println("Adding to broker with port: " + broker.get(2).port + " " + 2);
                            ArrayList<ArtistName> m3 = new ArrayList<>();
                            m3.add(art);
                            map.put(broker.get(2).port, m3);
                        } else {
                            ArrayList<ArtistName> t3 = map.get(broker.get(2).port);
                            t3.add(art);
                            System.out.println("Adding to broker with port: " + broker.get(2).port);
                            map.put(broker.get(2).port, t3);

                        }
                    }
                }

            }
        //}
        return map;
    }*/

    /*public Map<Integer,ArrayList<ArtistName>> updateList(PublisherNode p, Map<Integer,ArrayList<ArtistName>> map){
        for(String name: mapreceived.keySet()){  //πρεπει να το κανω και για το δευτερο μαπ
            ArtistName art = new ArtistName(name);
            try {
                if(p.hashTopic(art).getBrokerPort() == this.getBrokerPort()){
                    ArrayList<ArtistName> m = new ArrayList<>();
                    m.add(art);
                    map.put(this.port,m);
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return map;
    }*/


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
    public void pull(ArtistName artist, Value value, ConsumerNode con, PublisherNode pub) {


        try {
            out.writeUTF(con.getConsumerIP());
            out.writeInt(con.getConsumerPort());
            out.writeObject(value); //successfully sends artistName to BrokerNode
            out.flush();
            in = new ObjectInputStream(publisher_requestSocket.getInputStream());
            MusicFile ch = (MusicFile) in.readObject();

            ArrayList<MusicFile> song = new ArrayList<>(); //lista pou tha exei ola ta chunks tou tragoudiou
            if (in.equals("0")) { // song doesn't exist
                out.writeUTF(con.getConsumerIP());
                out.writeInt(con.getConsumerPort());
                out.writeObject("Choose another song"); //successfully sends artistName to BrokerNode
                out.flush();
            } else {   // song  exists
                song.add(ch);
                Value val = new Value(ch);//
                do { // mexri na parei ol ta chunks tou kommatiou
                    //traba next chunk
                    in = new ObjectInputStream(publisher_requestSocket.getInputStream());
                    ch = (MusicFile) in.readObject();
                    val.setMusicfile(ch);
                    out.writeUTF(con.getConsumerIP());
                    out.writeInt(con.getConsumerPort());
                    out.writeObject(ch); //successfully sends artistName to BrokerNode
                    out.flush();
                    song.add(ch);
                } while (ch.getChunkId() < ch.getTotalChunks());
                /*out.writeUTF(con.getConsumerIP());
                out.writeInt(con.getConsumerPort());
                out.writeObject(song); //successfully sends artistName to BrokerNode
                out.flush();*/

            }


            //sends song
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /*@Override η δικια μου pull (Emy's pull)
    public void pull(ArtistName artist, String song) {

        if(mapreceived.containsKey(artistReceived.getArtistName())){
            System.out.println("it exists");
            MusicFile f = new MusicFile(song,artist.getArtistName(),null,null,null,0,0);
            Value value = new Value(f);
            try {
                this.out.writeObject(artist);
                this.out.writeObject(value);

                int numOfchunks = in.readInt();
                try {
                    for (int i = 1; i <= numOfchunks;i++) {
                        Value chunk = new Value((MusicFile) in.readObject());
                        out.writeObject(chunk);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } // να ελεγχει και το δευτερο μαπ απο τον δευτερο publisher

    }*/

    public Map<Integer, ArrayList<ArtistName>> getEmy() {
        return emy;
    }

    public void setEmy(Map<Integer, ArrayList<ArtistName>> emy) {
        this.emy = emy;
    }

    public void setOut(ObjectOutputStream out){this.out = out;}

    public void setIn(ObjectInputStream in) {this.in = in;}

    public void setMapReceived(Map<String,ArrayList<String>> map){
        this.mapreceived = map;
    }

    public Map getMapReceived() {
        return mapreceived;
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

        BrokerNode b = new BrokerNode("localhost", 7654);
        BrokerNode b2 = new BrokerNode("localhost", 8765);
        BrokerNode b3 = new BrokerNode("localhost", 9876);
        b.init();
        b2.init();
        b3.init();
        brokers.add(b);
        //System.out.println("Key is: "+b.calculateKeys());

        System.out.println(brokers.isEmpty());
        brokers.add(b2);
        brokers.add(b3);
        Map<Integer, Map<String, ArrayList<String>>> art = new HashMap<>();

        brokers.parallelStream().forEach((broker) -> {
                try {
                    Socket publisher = broker.getPublisherServerSocket().accept();
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
                    System.out.println("Current broker port is: "+broker.getBrokerPort());

                    broker.setMapReceived((Map<String, ArrayList<String>>) broker.in.readObject());
                    art.put(publisherport, broker.getMapReceived()); //add in art the maps received
                    System.out.println(broker.getMapReceived().toString());

                    //tom.add(broker.getMapReceived());

                    PublisherNode pn = new PublisherNode(start, end, publisherip, publisherport);
                    registeredPublishers.add(pn);


                    System.out.println("Updating list with artist done");
                    //System.out.println(emy.toString());


                    registeredPublishers.add(pn);


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("Is map empty?" + (broker.getMapReceived()).isEmpty());


            /*try {
                broker.setEmy(broker.updateList(brokers,registeredPublishers.get(0),tom));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }*/



                while (true) {



                    try {
                        // socket object to receive incoming consumer requests
                        Socket consumer = broker.getConsumerServerSocket().accept();

                        broker.setOut(new ObjectOutputStream(consumer.getOutputStream()));
                        broker.setIn(new ObjectInputStream(consumer.getInputStream()));

                        //broker.out.writeInt(broker.getBrokerPort());

                        String consumerip = broker.in.readUTF();
                        System.out.println("conumer ip is " + consumerip);
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
                            boolean f = false;
                            for (Map.Entry<Integer,  Map<String, ArrayList<String>>> entry : art.entrySet()) {

                                //entry.getValue();
                                Map<String, ArrayList<String>> k = entry.getValue();
                                for(Map.Entry<String, ArrayList<String>> entry2 : k.entrySet()){

                                    //System.out.println("key is:" + name);
                                    System.out.println(broker.getArtistReceived().getArtistName());
                                    if (entry2.getKey().equals(broker.getArtistReceived().getArtistName()) && entry2.getKey() != null) {
                                        f = true;
                                        System.out.println("Yes it is equal");
                                        System.out.println(entry2.getValue().toString());
                                        ArrayList<String> songs = entry2.getValue();
                                        broker.out.writeObject(songs); //πρεπει να στελνει μονο το arraylist αν το κλειδι ειναι αυτο που εστειλε ο consumer
                                        broker.out.flush();
                                        break;
                                    }
                                }
                                if (f){break;}

                            }
                        }
                        else {

                            System.out.println("Noooo"+registeredPublishers.get(0).hashTopic(artistName).getBrokerPort());

                            System.out.println("yo");
                            int port = registeredPublishers.get(0).hashTopic(artistName).getBrokerPort();
                            broker.out.writeInt(port);
                            broker.out.flush();
                            System.out.println("yo");
                            broker.out.close();
                            broker.in.close();


                        }
                        System.out.println("END");

                    } catch (IOException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    /*try {
                        // socket object to receive incoming consumer requests
                        Socket consumer = broker.getConsumerServerSocket().accept();

                        broker.setOut(new ObjectOutputStream(consumer.getOutputStream()));
                        broker.setIn(new ObjectInputStream(consumer.getInputStream()));


                        //broker.out.writeObject(emy);
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


                        System.out.println("Assigning new thread for this client");


                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            //}
        });

    }
}
