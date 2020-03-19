
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.util.HashMap;


public class PublisherNode extends NodeImpl implements Publisher{

    List<Broker> publisher_brokers; // den xreiazetai new mallon?
    Address address = new Address();
    private Socket requestSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
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
    public Broker hashTopic(ArtistName artist) throws NoSuchAlgorithmException{
        //Hashes the ArtistName

        MessageDigest sha = MessageDigest.getInstance("SHA-256"); //το περιεχομενο εδω δεν ειμαι σιγουρη πως το δηλωνουμε , αλλου το εχει string αλλου σκετο
        String name = artist.getArtistName();


        byte[] namehash = sha.digest(name.getBytes());
        BigInteger big1 = new BigInteger(1,namehash); // 1 means positive

        //TODO: Finish this


    }


    @Override
    public void connect() {
        try {
            requestSocket = new Socket(address.getIp(), address.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            requestSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void push(ArtistName artist,Value val){

        byte[] song = val.getMusicfile().getMusicFileExtract();
        int chunksize = 512; //512 kb at most
        try {
            out = new ObjectOutputStream(requestSocket.getOutputStream());  //initialize out

           



            for(int i =0; i < song.length; i+= chunksize) { //send chuncks of song
                out.write(song, 0, chunksize+1);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //TODO: Check if correct



        //analoga me to hashing


    }

    @Override
    public void notifyFailure(Broker broker){
        //TODO: write code

    }

}
