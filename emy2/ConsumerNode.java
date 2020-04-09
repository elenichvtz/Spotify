import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Scanner;

//Client
public class ConsumerNode extends Thread implements Consumer,Serializable {

    Socket requestSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    String ip;
    int port;
    ArrayList<String> listofsongs = new ArrayList<String>();

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
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void register(BrokerNode broker, ArtistName artist) {

        try {
            System.out.println("Inside register");
            this.out.writeUTF(this.ip);
            this.out.writeInt(this.port);
            this.out.writeObject(artist); //successfully sends artistName to BrokerNode
            this.out.flush();

            int brokerport = this.in.readInt();

            if(brokerport != broker.getBrokerPort()) {

                System.out.println(brokerport + " is the correct broker port.");
                System.out.println("Disconnecting...");
                disconnect(broker, artist);

                System.out.println("yo2");
                connect(brokerport);

                this.out.writeUTF(this.ip);
                System.out.println("yo");
                this.out.writeInt(this.port);
                System.out.println("yo");

                this.out.writeObject(artist); //successfully sends artistName to BrokerNode
                this.out.flush();

            }

            //liat of songs of the requested artist
            this.listofsongs = (ArrayList<String>)in.readObject();
            System.out.println("Map received from broker to consumer");
            System.out.println(listofsongs.toString());

            System.out.println("Pick a song: ");

            Scanner userInput = new Scanner(System.in);
            String song = userInput.nextLine();

            out.writeUTF(song);

            out.flush();

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
            chunks = in.readInt();
            for (int i = 1; i <= chunks;i++) {
                System.out.println("yo2");
                Value value = (Value) in.readObject();
                pieces.add(value); //chunks are saved locally
            }
            System.out.println(pieces.toString());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){

        ConsumerNode cn = new ConsumerNode("localhost", 7654);
        cn.init();

        System.out.println("Pick an artist: ");
        Scanner userInput = new Scanner(System.in);
        String artist = userInput.nextLine();

        ArtistName artistName = new ArtistName(artist);
        BrokerNode b = new BrokerNode("localhost", 7654);

        cn.register(b, artistName);

        MusicFile ms = new MusicFile(null,null,null,null,null,0,0);
        Value value = new Value(ms);

        cn.playData(artistName,value);
    }
}
