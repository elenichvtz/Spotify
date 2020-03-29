import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

//Client
public class ConsumerNode extends Thread implements Consumer, Serializable {

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
            this.requestSocket = new Socket(this.ip, this.port+3);

            this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());

            //send ip and port to broker
            this.out.writeUTF(getConsumerIP());
            this.out.writeInt(getConsumerPort());
            this.out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(brokers);
    }

    @Override
    public List<BrokerNode> getBrokers() {
        return brokers;
    }

    @Override
    public void connect() {
        while(!requestSocket.isConnected()) {
            try {
                requestSocket = new Socket(this.ip, this.port);
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
                this.requestSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void playData(ArtistName artist, Value val){
        //receive val and play the extract
    }

    public Socket getSocket() {
        return this.requestSocket;
    }

    public String getConsumerIP() {
        return this.ip;
    }

    public int getConsumerPort() {
        return this.port;
    }

    public static void main(String args[]){

        ConsumerNode cn = new ConsumerNode("127.0.0.3", 4321);
        cn.init();

        Socket broker = cn.getSocket();

        try {
            ObjectInputStream in = new ObjectInputStream(broker.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(broker.getOutputStream());

            //receive broker's ip and port
            String brokerip = in.readUTF();
            int brokerport = in.readInt();

            BrokerNode bn = new BrokerNode(brokerip, brokerport);

            ArtistName artistName = new ArtistName("Komiku");

            cn.register(bn, artistName);

            out.writeObject(artistName);

            System.out.println("brokers size "+cn.getBrokers().size());

            //receive song
            //while loop to read all song
            /*while(true) {
                Value val = (Value) in.readObject();

                cn.playData(artistName, val);

                if(val.getMusicfile().getChunkId()==val.getMusicfile().getTotalChunks()) {break;}
            }

            cn.disconnect(bn, artistName);*/

        } catch (IOException e) {
            e.printStackTrace();
        }
        //ObjectOutputStream out = new ObjectOutputStream(broker.getOutputStream());

        //isos na perimenei gia apantisi gia na  kleisei

    }
}
