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
    int exist;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(BrokerNode broker, ArtistName artist) {

        try {
            this.out.writeUTF(this.ip);
            this.out.writeInt(this.port);
            this.out.writeObject(artist);
            this.out.flush();

            int brokerport = this.in.readInt();

            if(brokerport != broker.getBrokerPort()) {

                disconnect(broker, artist);

                connect(brokerport);

                this.out.writeUTF(this.ip);
                this.out.writeInt(this.port);

                this.out.writeObject(artist); //successfully sends artistName to BrokerNode
                this.out.flush();
            }

            exist = in.readInt();

            if(exist == 1) {

                //list of songs of the requested artist
                this.listofsongs = (ArrayList<String>) in.readObject();

                System.out.println(this.listofsongs.toString());

                Scanner userInput = new Scanner(System.in);

                boolean songflag = false;

                while(songflag == false){
                    System.out.println("Pick a song: ");
                    String song = userInput.nextLine();
                    for(int i=0; i<listofsongs.size(); i++){
                        if(song.equals(listofsongs.get(i))){
                            songflag =true;
                            this.out.writeUTF(song);
                            this.out.flush();
                            break;
                        }

                    }
                }
            }
            else{
                System.out.println("The artist you searched doesn't exist.");
                System.out.println("Please try again.");
            }

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

        ArrayList<Value> pieces = new ArrayList<>();

        try {
            chunks = in.readInt();
            FileOutputStream fileOuputStream = new FileOutputStream("songReceived.mp3");

            for (int i = 1; i <= chunks;i++) {
                Value value = new Value((MusicFile) in.readObject());
                fileOuputStream.write(value.getMusicfile().getMusicFileExtract());
                fileOuputStream.flush();

                pieces.add(value); //saves chunks locally
            }

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

        while(cn.exist == 0){
            System.out.println("Pick an artist: ");
            userInput = new Scanner(System.in);
            artist = userInput.nextLine();
            artistName = new ArtistName(artist);
            cn.disconnect(b,artistName);
            cn.connect(b.port);
            cn.register(b,artistName);
        }

        MusicFile ms = new MusicFile(null,null,null,null,null,0,0);
        Value value = new Value(ms);

        cn.playData(artistName,value);
    }
}
