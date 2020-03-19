import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import java.io.IOException;
import java.io.*;
import java.net.*;

public class BrokerNode extends NodeImpl implements Broker {

    public ServerSocket providerSocket;
    Socket connection = null;

    @Override
    public void calculateKeys() {

    }

    @Override
    public void connect() {
        try {
            providerSocket = new ServerSocket(4321, 10);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            providerSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Publisher acceptConnection(Publisher publisher) {
        try {

            connection = providerSocket.accept();
            System.out.println("Connection accepted");
        }catch (IOException e){
            e.printStackTrace();

        }
        return publisher;
    }

    @Override
    public Consumer acceptConnection(Consumer consumer) {
        try {

            connection = providerSocket.accept();
            System.out.println("Connection accepted");
        }catch (IOException e){
            e.printStackTrace();

        }

        return consumer;
    }

    @Override
    public void notifyPublisher(String name) {

    }

    @Override
    public void pull(ArtistName artist) {

    }

    public static void main(String args[]) {

    }

}
