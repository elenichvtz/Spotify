import java.io.*;
import java.net.*;
import java.util.List;

//Client
public class ConsumerNode implements Consumer {

    Socket requestSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    String ip;
    int port;

    ConsumerNode(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void init() {
        try {
            this.requestSocket = new Socket(this.ip, this.port+2);
            this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //send ip and port to broker
        try {
            this.out.writeUTF(this.ip);
            this.out.writeInt(this.port);
            this.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //receive broker's key
        try {
            this.in = new ObjectInputStream(this.requestSocket.getInputStream());
            //this.out = new ObjectOutputStream(this.publisher_requestSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int key = this.in.readInt();
            System.out.println(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        ConsumerNode cn = new ConsumerNode("127.0.0.3", 4321);
        cn.init();
    }
}
