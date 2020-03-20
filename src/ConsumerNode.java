import java.io.*;
import java.net.*;

public class ConsumerNode extends NodeImpl implements Consumer  {

    //Client

    Socket requestSocket = null; //ισως μεσα στην run οπως στο εργαστηριο??
    ObjectOutputStream out = null;
    ObjectInputStream in = null;






    @Override
    public void register(Broker broker,ArtistName artist) {

        try {

            //broker.notifyPublisher(artist.getArtistName());   //First notify

            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeUTF(artist.getArtistName());         //then send
            out.flush(); //send for sure
            //TODO: find the correct broker

            broker.pull(artist);                                            // then pull





        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        @Override
    public void disconnect(Broker broker,ArtistName artist) {
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
