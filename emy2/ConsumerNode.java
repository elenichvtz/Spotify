import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

//Client
public class ConsumerNode extends Thread implements Consumer,Serializable {

    Socket requestSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    String ip;
    int port;
    ArrayList<String> listofsongs = new ArrayList<String>();
    List<PublisherNode> Publishers = new ArrayList<PublisherNode>();

    ConsumerNode(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void init() {
        try {
            this.requestSocket = new Socket(this.ip, this.port+1);
            this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
            this.in = new ObjectInputStream(this.requestSocket.getInputStream());
            //this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<BrokerNode> getBrokers() {
        return null;
    }

    @Override
    public void connect() {
        //while(!requestSocket.isConnected()) {
            try {
                requestSocket = new Socket(this.ip, this.port+1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        //}
    }

    public void connect(int port){
        try {
            requestSocket = new Socket(this.ip, port+1);
            this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
            this.in = new ObjectInputStream(this.requestSocket.getInputStream());
            System.out.println("Connected to a new Broker with port "+port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void register(BrokerNode broker, ArtistName artist) {


        try {
            System.out.println("Inside register");
            this.out.writeUTF(this.ip);
            this.out.writeInt(this.port);
            this.out.writeObject(artist); //successfully sends artistName to BrokerNode
            this.out.flush();

            //na pairnei apo ton borker to port pou prepei na syndethei

            int brokerport = this.in.readInt();
            if(brokerport != broker.getBrokerPort()) {

                System.out.println(brokerport + " is the correct broker port.");
                System.out.println("Disconnecting...");
                disconnect(broker, artist);

                //na kanei connect(port apo broker)
                System.out.println("yo2");
                connect(brokerport);

                this.out.writeUTF(this.ip);
                System.out.println("yo");
                this.out.writeInt(this.port);
                System.out.println("yo");
                //out.flush();
                this.out.writeObject(artist); //successfully sends artistName to BrokerNode
                this.out.flush();
                //System.out.println("yo");
                //this.in = new ObjectInputStream(this.requestSocket.getInputStream());

            }

            //η λιστα με τα τραγουδια του artist επιστρεφεται στον consumer
            this.listofsongs = (ArrayList<String>)in.readObject();
            System.out.println("Map received from broker to consumer");
            System.out.println(listofsongs.toString());
            //String song = "River Meditation";
            //String song = "Champ de tournesol";
            String song = "Bleu";
            out.writeUTF(song);

            out.flush();


            //sends song
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void disconnect(BrokerNode broker, ArtistName artist) {
        try {

            this.requestSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playData(ArtistName artist, Value val){
        int chunks = 0;
        System.out.println("Inside play data");
        ArrayList<Value> pieces = new ArrayList<>();


        try {
            System.out.println("yo");
            chunks = in.readInt(); // or do...while()
            FileOutputStream fileOuputStream = new FileOutputStream("/Users/emiliadan/Downloads/distributed_project/songReceived3.mp3");

            for (int i = 1; i <= chunks;i++) {
                System.out.println("yo2");
                Value value = new Value((MusicFile) in.readObject());
                System.out.println("Chunk id is: "+value.getMusicfile().getChunkId());
                fileOuputStream.write(value.getMusicfile().getMusicFileExtract());
                fileOuputStream.flush();

                pieces.add(value); //αποθηκευει τοπικα τα chunks
            }
            System.out.println(pieces.toString());
        } catch (IOException | ClassNotFoundException /*| UnsupportedTagException | InvalidDataException*/ e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getListofSongs() { return this.listofsongs; }

    public void setBrokers(BrokerNode b) { //ισως να μην χρειαστει
        brokers.add(b);
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

        ConsumerNode cn = new ConsumerNode("localhost", 7654);
        cn.init();
        //ArtistName artistName = new ArtistName("Jason Shaw");
        ArtistName artistName = new ArtistName("Komiku");
        BrokerNode b = new BrokerNode("localhost", 7654);

        cn.register(b,artistName); //υποτιθεται οτι η λιστα με τους μπροκερσ πρεπει να ειναι γεματη
        MusicFile ms = new MusicFile(null,null,null,null,null,0,0);
        Value value = new Value(ms);
        cn.playData(artistName,value);


    }
}
