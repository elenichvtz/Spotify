import java.io.IOException;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

//Server
public class BrokerNode extends NodeImpl implements Broker{

    ServerSocket mysocket;
    Socket connection = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

    /*int number;

    InetAddress ip;
    int port;

    BrokerNode(InetAddress ip, int port){
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void init(int x){

        BrokerNode b = new BrokerNode(ip,port+x);

    }*/

    @Override
    public int calculateKeys(){
           return  ("127.0.0.1" + "/" + 4321).hashCode();
    }

    @Override
    public Publisher acceptConnection(Publisher publisher){
        try {
            connection = mysocket.accept();
            System.out.println("Connection accepted");
        }catch (IOException e){
            e.printStackTrace();
        }
        return publisher;
    }

    @Override
    public Consumer acceptConnection(Consumer consumer) {
        try {
            connect();
            connection = mysocket.accept();
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
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeInt(calculateKeys());
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pull(ArtistName artist){
        try {
            in = new ObjectInputStream(requestSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){



        Thread t1 = new Thread();

    }
}
