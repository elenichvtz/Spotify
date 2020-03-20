import java.io.IOException;
import java.io.*;
import java.net.*;

public class BrokerNode extends NodeImpl implements Broker {



    ServerSocket mysocket;
    Socket connection = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

    @Override
    public void connect() {
            try {
                mysocket = new ServerSocket(4321, 10);

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void calculateKeys(){



    }

    @Override
    public Publisher acceptConnection(Publisher publisher){
        try {

            connection = mysocket.accept();
            System.out.println("Connection accepted");
        }catch (IOException e){
            e.printStackTrace();

        }
        return publisher;
    }


    @Override
    public Consumer acceptConnection(Consumer consumer) {
        try {
            connect();

            connection = mysocket.accept();
            System.out.println("Connection accepted");
        }catch (IOException e){
            e.printStackTrace();

        }

        return consumer;


    }

    @Override
    public void notifyPublisher(String name){
        try {
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void pull(ArtistName artist){




    }

    public static void main(String args[]){


    }

   // NAI  🥺
    //    👉👈

}
