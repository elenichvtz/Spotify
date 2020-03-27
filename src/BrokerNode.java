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
public class BrokerNode implements Broker{

    ServerSocket ConsumerSocket;
    ServerSocket PublisherSocket;
    Socket connectionPublisher;
    Socket connectionConsumer;
    ObjectOutputStream out = null;
    ObjectOutputStream outc = null;
    ObjectInputStream in = null; //in for publisher
    ObjectInputStream inc = null; //in for consumer

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
            this.ConsumerSocket = new ServerSocket(port+1, 10);
            this.PublisherSocket = new ServerSocket(port,10);
            //this.requestSocket = this.providerSocket.accept();
            System.out.println("broker provider socket connect");

        } catch (IOException e) {
            e.printStackTrace();
        }

        connect();

        try {
            this.in = new ObjectInputStream(this.connectionPublisher.getInputStream());
            this.inc = new ObjectInputStream(this.connectionConsumer.getInputStream());

            this.out = new ObjectOutputStream(this.connectionConsumer.getOutputStream());
            this.outc = new ObjectOutputStream(this.connectionConsumer.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //receive map from publisher
        try {
            Object publishermap = this.in.readObject();
            System.out.println(publishermap.toString());

            BigInteger k = calculateKeys();
            out.writeObject(k);
            out.flush();



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.key = calculateKeys();

        System.out.println("init");
    }

    @Override
    public List<Broker> getBrokers() {
        return null;
    }

    @Override
    public BigInteger calculateKeys(){
        String s = this.ip + this.port;

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
            this.connectionConsumer = this.ConsumerSocket.accept();
            this.connectionPublisher = this.PublisherSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect(){
        try {
            this.connectionPublisher.close();
            this.PublisherSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Publisher acceptConnection(Publisher publisher){
        try {
            this.connectionPublisher = this.PublisherSocket.accept();
            System.out.println("Connection accepted");
        }catch (IOException e){
            e.printStackTrace();
        }
        return publisher;
    }

    @Override
    public Consumer acceptConnection(Consumer consumer) {
        try {
            this.connectionConsumer = this.ConsumerSocket.accept();
            System.out.println("Connection accepted");
        }catch (IOException e){
            e.printStackTrace();
        }
        return consumer;
    }

    public void SendPublisher(Publisher publisher){


    }

    @Override
    public void notifyPublisher(String name){
        //Θα ενημερωνει ο broker τον καθε publisher για ποια κλειδια ειναι υπευθυνοι (για ποιο ευρος τιμων)

        try {
            out = new ObjectOutputStream(this.connectionPublisher.getOutputStream());
            out.writeInt(calculateKeys().intValue());
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pull(ArtistName artist){
        try {
            in = new ObjectInputStream(this.connectionConsumer.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        BrokerNode b = new BrokerNode("127.0.0.1",4321);
        b.init();
    }
}
