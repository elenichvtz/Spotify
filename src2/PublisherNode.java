<<<<<<< HEAD
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PublisherNode extends NodeImpl implements Publisher{

    List<Broker> publisher_brokers; // den xreiazetai new mallon?
    //Client

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
    public void push(ArtistName artist,Value val){
        //analoga me to hashing 

=======
public class PublisherNode extends NodeImpl implements Publisher {
    @Override
    public void getBrokerList() {
        
    }

    @Override
    public Broker hashTopic(ArtistName artist) {
        return null;
    }

    @Override
    public void push(ArtistName artist, Value val) {
>>>>>>> 38f1844698dffb311f7865830279b91993cbe333

    }

    @Override
<<<<<<< HEAD
    public void notifyFailure(Broker broker){


    }

=======
    public void notifyFailure(Broker broker) {

    }
>>>>>>> 38f1844698dffb311f7865830279b91993cbe333
}
