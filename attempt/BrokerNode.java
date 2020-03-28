import java.io.IOException;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Server and Client
public class BrokerNode implements Broker{

    ServerSocket providerSocket;
    Socket requestSocket = null;
    ServerSocket providerSocket2;
    //Socket requestSocket2 = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

    ObjectOutputStream outc = null;
    ObjectInputStream inc = null;

    Object publishermap;

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

        try {
            this.providerSocket = new ServerSocket(this.port, 10);
            System.out.println("broker provider socket connect");

        } catch (IOException e) {
            e.printStackTrace();
        }

        connect();

        try {
            this.in = new ObjectInputStream(this.requestSocket.getInputStream());

            //this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //receive map, ip and port from publisher
        try {
            String publisherip = this.in.readUTF();
            System.out.println(publisherip);
            int publisherport = this.in.readInt();
            System.out.println(publisherport);
            char start = this.in.readChar();
            char end = this.in.readChar();
            System.out.println(start +" & "+ end);

            PublisherNode pn = new PublisherNode(start, end, publisherip, publisherport);
            registeredPublishers.add(pn);
            System.out.println(registeredPublishers.isEmpty());

            //acceptConnection(pn);


            publishermap = this.in.readObject(); //receives map from publisher
            System.out.println(publishermap.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //System.out.println(registeredPublishers.isEmpty());

        this.key = calculateKeys();

        try {
            this.requestSocket = new Socket(this.ip, this.port+1);
            this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //send key to publisher
        try {
            this.out.writeObject(this.key);
            this.out.flush();
            System.out.println(this.key);
            System.out.println("flush");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //disconnect();

        System.out.println("Connected to consumer before... ");

        //FOR CONSUMER NOW --------------------------------------------------------------

        try {
            System.out.println("yo");

            this.providerSocket2 = new ServerSocket(this.port+2, 10);
            System.out.println("yo");
            this.requestSocket = providerSocket2.accept(); //εδω εχει προβλημα... κολλαει
            System.out.println("yo");
        } catch (IOException e) {
            e.printStackTrace();
        }



        System.out.println("Connected to consumer after ");


        try {
            this.outc = new ObjectOutputStream(this.requestSocket.getOutputStream());
            this.outc.writeObject(publishermap); //send map to consumer
            this.outc.flush();

            System.out.println("flush for consumer");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public List<Broker> getBrokers() {
        return null;
    }

    @Override
    public BigInteger calculateKeys(){
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
    public void connect(){
        try {
            this.requestSocket = this.providerSocket.accept();
            //this.requestSocket2 = this.providerSocket2.accept(); //SAME THING WITH ACCEPT CONNECTION FOR CONSUMER
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public void disconnect(){
        try {
            //this.requestSocket.close();
            this.providerSocket.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Publisher acceptConnection(Publisher publisher){
        try {
            this.requestSocket = this.providerSocket.accept();
            System.out.println("Connection accepted");
        }catch (IOException e){
            e.printStackTrace();
        }
        return publisher;
    }

    @Override
    public Consumer acceptConnection(Consumer consumer) {
        try {
            this.requestSocket = this.providerSocket2.accept();
            System.out.println("Connection accepted");
        }catch (IOException e){
            e.printStackTrace();
        }
        return consumer;
    }

    @Override
    public void notifyPublisher(String name){
        //Θα ενημερωνει ο broker τον καθε publisher για ποια κλειδια ειναι υπευθυνοι (για ποιο ευρος τιμων)

        try {
            out = new ObjectOutputStream(this.requestSocket.getOutputStream());
            out.writeInt(calculateKeys().intValue());
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pull(ArtistName artist){
        try {
            in = new ObjectInputStream(this.requestSocket.getInputStream()); //θα στειλει στον consumer
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        BrokerNode b = new BrokerNode("127.0.0.1",4321); //TODO: FIX THIS FOR MY PC
        b.init();
    }
}
