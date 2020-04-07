import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

//Client
public class ConsumerNode extends Thread implements Consumer,Serializable {

    transient Socket requestSocket = null;
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
                this.requestSocket = new Socket(this.ip, this.port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        //}
    }

    public void connect(int port){
        try {
            this.requestSocket = new Socket(this.ip, port+1);
            System.out.println("Connected to a new Broker with port "+port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            this.requestSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
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

            //na pairnei apo ton borker to port pou prepei na syndethei

            int brokerport = this.in.readInt();

            System.out.println(brokerport + " is the correct broker port.");
            System.out.println("Disconnecting...");
            disconnect(broker,artist);

            //na kanei connect(port apo broker)
            System.out.println("yo2");
            connect(brokerport);                      //problem

            this.out.writeUTF(this.ip);
            this.out.writeInt(this.port);
            this.out.writeObject(artist); //successfully sends artistName to BrokerNode EDO VGAZEI ERROR TORA
            this.out.flush();

            ArrayList<String> m = (ArrayList<String>) this.in.readObject();

            System.out.println("Map received from broker to consumer");

            System.out.println(m.toString());

            //η λιστα με τα τραγουδια του artist επιστρεφεται στον consumer
            this.listofsongs = (ArrayList<String>) in.readObject();

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
        ArrayList<Value> pieces = new ArrayList<>();
        try {
            chunks = in.readInt(); // or do...while()
            for (int i = 1; i <= chunks;i++) {
                Value value = new Value((MusicFile) in.readObject());
                pieces.add(value); //αποθηκευει τοπικα τα chunks
            }
        } catch (IOException | ClassNotFoundException e) {
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

        BrokerNode b = new BrokerNode("localhost", cn.getConsumerPort());


        ArtistName artistName = new ArtistName("Komiku");

        cn.register(b,artistName); //υποτιθεται οτι η λιστα με τους μπροκερσ πρεπει να ειναι γεματη

        //o broker tou epistrefei ti lista an uparxei o artist
        //an o artist den uparxei termatizei

        //vlepei ti lista me ta tragoudia tou kallitexni pou dialekse
        //for(int i=0; i<cn.getListofSongs().size(); i++) {
           // System.out.println(i +". " + cn.getListofSongs().get(i));
       // }


        //o consumer epilegei ena tragoudi kai to stelnei ston broker
        /*try {
            cn.out.writeUTF("Bleu"); //iparxei ston Komiku to elegksa
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //lamvanei apo ton broker to value
        //kalei tin playData kai to apothikeuei


/*
        Socket broker = cn.getSocket();
        try {

            //Socket broker = cn.getSocket();

            //ΑΥΤΟ ΘΑ ΤΟ ΞΑΝΑ ΒΑΛΩ ΜΕΣΑ ΣΤΗΝ REGISTER ΕΚΕΙ ΠΡΕΠΕΙ ΝΑ ΓΙΝΕΤΑΙ...

            ObjectOutputStream out = new ObjectOutputStream(broker.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(broker.getInputStream());
            //ArtistName artistName = new ArtistName("Komiku");

            //Object list = in.readObject();
            //send ip and port to broker
            out.writeUTF(cn.getConsumerIP());
            out.writeInt(cn.getConsumerPort());
            out.writeObject(artistName); //successfully sends artistName to BrokerNode
            out.flush();

            //ArrayList<BrokerNode> l = (ArrayList<BrokerNode>)in.readObject();
            System.out.println("Map received from broker to consumer");
            ArrayList<String> m = (ArrayList<String>)in.readObject();
            System.out.println("Map received from broker to consumer");
            System.out.println(m.toString());

            System.out.println("Attempting to send publisher list to consumer...");

            //BrokerNode v = (BrokerNode)in.readObject();

            //System.out.println(v.toString());
            //System.out.println("Success");
            out.close();


        } catch (IOException e) {
         e.printStackTrace();
        } catch (ClassNotFoundException e) {
        e.printStackTrace();
        }

*/
    }
}
