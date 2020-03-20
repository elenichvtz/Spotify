import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//Client
public class PublisherNode extends NodeImpl implements Publisher, java.io.Serializable {

    private Socket requestSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

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
        else if(hashNumber.intValue() == 1) {
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
        int chunk_size = 512*1024; //512 KB at most
        try {
            out = new ObjectOutputStream(requestSocket.getOutputStream());  //initialize out

            for(int i =0; i < song.length; i+= chunk_size) { //send chunks of song
                out.write(song, 0, chunk_size+1);
                out.flush();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //TODO: Check if correct
    }

    @Override
    public void notifyFailure(Broker broker){
        //TODO: write code
        //not being the right publisher or not having the song/artist?????
    }

    public static void main(String args[]){

    }
}
