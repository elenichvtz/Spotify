import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

//Client
public class PublisherNode extends NodeImpl implements Publisher{

    List<Broker> publisher_brokers; // den xreiazetai new mallon?
    private Socket requestSocket = null;

    @Override
    public void getBrokerList(){
        /*Kalei tin method getBrokers() tis NodeImpl kai tin ekxwrei stin lista me tous brokers
        tou publisher

        -> mipws xreiazetai telika to new gia na exoume diaforetika antikeimena?
        giati twra exoume mia anafora publisher_brokers stin lista brokers tis Node

         */

        publisher_brokers = getBrokers();
    }

    @Override
    public Broker hashTopic(ArtistName artist){
        String name = artist.getArtistName();
        //tha kanei to hashing
        try {
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(name.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashed = no.toString(16);
            while (hashed.length() < 32) {
                hashed = "0" + hashed;
            }
            return null; //gia thn wra

            //prepei na epistrefei ton katallhlo Broker
            //hash me methodo kuklou
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void connect() {
        try {
            requestSocket = new Socket("127.0.0.1", 4321);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(Broker broker,ArtistName artist) {
        try {
            requestSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void push(ArtistName artist,Value val){
        //analoga me to hashing 

    }

    @Override
    public void notifyFailure(Broker broker){

    }

    public static void main(String args[]){

    }

}
