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

    ServerSocket providerSocket;
    Socket requestSocket = null;
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

        try {
            this.providerSocket = new ServerSocket(port, 10);
            //this.requestSocket = this.providerSocket.accept();
            System.out.println("broker provider socket connect");

        } catch (IOException e) {
            e.printStackTrace();
        }

        connect();

        try {
            this.in = new ObjectInputStream(this.requestSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //receive map from publisher
        try {
            Object publishermap = this.in.readObject();
            System.out.println(publishermap.toString());
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect(){
        try {
            this.requestSocket.close();
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
            this.requestSocket = this.providerSocket.accept();
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
            in = new ObjectInputStream(this.requestSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        BrokerNode b = new BrokerNode("127.0.0.1",4321);
        b.init();
    }
}
