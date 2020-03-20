import java.io.IOException;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//Server
public class BrokerNode extends NodeImpl implements Broker, java.io.Serializable{

    ServerSocket mysocket;
    Socket connection = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

   /* @Override
    public void connect() {
        try {
            mysocket = new ServerSocket(4321, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void calculateKeys(){               //hash key of broker??????
        int hash = ("127.0.0.1" + "/" + "4321").hashCode();
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
        try {
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
        }catch (IOException e){
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

    }
}
