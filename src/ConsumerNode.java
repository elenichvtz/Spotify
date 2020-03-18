import java.io.*;
import java.net.*;

public class ConsumerNode extends NodeImpl implements Consumer  {

    //Client

    public Socket requestSocket = null;

    public void run(){
        //Socket requestSocket = null;

    }

    @Override
    public void connect() {
        try {
            requestSocket = new Socket("127.0.0.1", 4321);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(Broker broker,ArtistName artist) {

    }

    @Override
    public void disconnect(Broker broker,ArtistName artist) {
        try {
            requestSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playData(ArtistName artist, Value val) {

    }

    public static void main(String args[]) {

        ConsumerNode n1 = new ConsumerNode();

    }

}
