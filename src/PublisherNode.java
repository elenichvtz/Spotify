import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        return null; //null για την ωρα
        //tha kanei to hashing

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
