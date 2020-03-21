import java.io.*;
import java.net.*;

//Client
public class ConsumerNode extends NodeImpl implements Consumer {

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
        //TODO: check if random Broker is correct then pull ,else search for right Broker
        try {

            //broker.notifyPublisher(artist.getArtistName());   //First notify

            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeUTF(artist.getArtistName());         //then send
            out.flush(); //sent for sure
            //TODO: find the correct broker


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect(Broker broker, ArtistName artist) {
            try {
                requestSocket.close();
                //broker.disconnect(); //???
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void playData(ArtistName artist, Value val){
        //just play the chunks from stream!
    }

    //method run

    public static void main(String args[]){

        ConsumerNode n1 = new ConsumerNode();
    }
}
