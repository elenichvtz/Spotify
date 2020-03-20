import java.io.*;
import java.net.*;

//Client
public class ConsumerNode extends NodeImpl implements Consumer, java.io.Serializable  {

    Socket requestSocket = null; //ισως μεσα στην run οπως στο εργαστηριο??
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

    @Override
    public void connect() {
        while(!requestSocket.isConnected()) {
            try {
                requestSocket = new Socket("127.0.0.1", 4321);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void register(Broker broker, ArtistName artist) {
        //TODO: check if already registered
        try {

            //broker.notifyPublisher(artist.getArtistName());   //First notify

            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeUTF(artist.getArtistName());         //then send
            out.flush(); //sent for sure
            //TODO: find the correct broker

            broker.pull(artist);                            // then pull

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        @Override
    public void disconnect(Broker broker, ArtistName artist) {
        super.disconnect();
        //remove from list
    }

    @Override
    public void playData(ArtistName artist, Value val){

    }

    public static void main(String args[]){

        ConsumerNode n1 = new ConsumerNode();
    }
}
