import java.io.*;
import java.net.*;
import javax.sound.sampled.*;


public class ConsumerNode extends NodeImpl implements Consumer  {

    //Client
    String serverName = "127.0.0.1";
    public static int port = 4321;
    Socket requestSocket = null;

    public void run(){

        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            //create socket and connect to server
            System.out.println("Connecting to server " + serverName + ":" + port);
            requestSocket = new Socket(serverName, port);

            outStream = requestSocket.getOutputStream();

            //get the server stream
            inStream = requestSocket.getInputStream();


        } catch (UnknownHostException unknownHost) {
        System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                inStream.close();
                outStream.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void connect() {

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
