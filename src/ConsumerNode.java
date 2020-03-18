import java.io.*;
import java.net.*;

public class ConsumerNode extends NodeImpl implements Consumer  {

    //Client

    //Socket requestSocket = null; ισως μεσα στην run οπως στο εργαστηριο??

    public void run(){
        Socket requestSocket = null;

    }


    @Override
    public void register(Broker broker,ArtistName artist){


    }

    @Override
    public void disconnect(Broker broker,ArtistName artist){




    }

    @Override
    public void playData(ArtistName artist, Value val){

    }



    public static void main(String args[]){

        ConsumerNode n1 = new ConsumerNode();

    }

}
