import java.io.*;
import java.net.*;
import java.util.List;

//Client
public class ConsumerNode implements Consumer {

    Socket requestSocket = null; //ισως μεσα στην run οπως στο εργαστηριο??
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

    @Override
    public void init() {

    }

    @Override
    public List<Broker> getBrokers() {
        return null;
    }

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
    public void disconnect() {

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
    public void run(){
        connect();
        //register();
    }

    public static void main(String args[]){

        ConsumerNode n1 = new ConsumerNode();
    }
}
