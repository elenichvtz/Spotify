import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.List;

//Client
public class ConsumerNode implements Consumer {

    Socket requestSocket = null; //ισως μεσα στην run οπως στο εργαστηριο??

    ObjectOutputStream out = null;
    ObjectInputStream in = null;

    String ip;
    int port;
    String artist;
    String song;

    ConsumerNode(String ip, int port,String artist,String song){
        this.ip = ip;
        this.port = port;
        this.artist = artist;
        this.song = song;
    }

    @Override
    public void init() {
        System.out.println("Initializing Consumer...");
        try {
            this.requestSocket = new Socket(this.ip, this.port+2);
            this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.in = new ObjectInputStream(this.requestSocket.getInputStream());
            //this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //receive map from broker
        try {
            Object themap = this.in.readObject();

            System.out.println(themap);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Broker> getBrokers() {
        return null;
    }

    @Override
    public void connect() {
        /*while(!requestSocket.isConnected()) {
            try {
                requestSocket = new Socket(this.ip, this.port+1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void disconnect() {
        try {
            this.requestSocket.close();

        }catch (IOException e){
            e.printStackTrace();
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
        try {
            int chunks = in.readInt();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //method run
    /*public void run(){
        connect();
        //register();
    }*/

    public static void main(String args[]){

        ConsumerNode n1 = new ConsumerNode("120.0.0.1",4321, "Alexander Nakarada","Be Chillin");
        n1.init();
    }
}
