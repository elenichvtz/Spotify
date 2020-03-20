import java.io.*;
import java.net.Socket;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.util.HashMap;


public class PublisherNode extends NodeImpl implements Publisher{

    private Socket requestSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    //Client

    @Override
    public void getBrokerList(){
        super.getBrokers();

    }

    @Override
    public Broker hashTopic(ArtistName artist) throws NoSuchAlgorithmException{
        //Hashes the ArtistName

        MessageDigest sha = MessageDigest.getInstance("SHA-256"); //το περιεχομενο εδω δεν ειμαι σιγουρη πως το δηλωνουμε , αλλου το εχει string αλλου σκετο
        String name = artist.getArtistName();


        byte[] namehash = sha.digest(name.getBytes());
        BigInteger big1 = new BigInteger(1,namehash); // 1 means positive

        BigInteger hash2 = new BigInteger("3");

        BigInteger hashNumber = big1.mod(hash2);

        if(hashNumber.intValue() == 0){

            return getBrokers().get(0);
        }
        else if(hashNumber.intValue() == 1){
            return getBrokers().get(1);
        }
        else{
            return getBrokers().get(2);
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
        int chunksize = 512*1024; //512 kb or KB at most
        try {
            out = new ObjectOutputStream(requestSocket.getOutputStream());  //initialize out

            for(int i =0; i < song.length; i+= chunksize) { //send chunks of song
                out.write(song, 0, chunksize+1);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //TODO: Check if correct


    }

    @Override
    public void notifyFailure(Broker broker){
        //TODO: write code

    }

}
