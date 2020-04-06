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
                requestSocket = new Socket(this.ip, this.port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        //}
    }

    public void connect(int port){
        try {
            requestSocket = new Socket(this.ip, port+1);
            System.out.println("Connected to a new Broker with port"+port);
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
                    PublisherNode p = new PublisherNode('A', 'M', "localhost", 9876);
                    //broker.getPublisherList().get(0).hashTopic(artist); //returns the Broker responsible for that artist
                    System.out.println("IS EMPTY?"+broker.getPublisherList().isEmpty());
                    System.out.println(broker.equals(p.hashTopic(artist)));

                    if(broker.getBrokerPort() ==((p.hashTopic(artist)).getBrokerPort())){ //if current broker equals the one returned from hashtopic then
                        Socket brker = getSocket();
                        //ObjectOutputStream out = null;

                        try {
                            out = new ObjectOutputStream(brker.getOutputStream());
                            in = new ObjectInputStream(brker.getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //Object list = in.readObject();
                        //send ip and port to broker
                        try {
                            System.out.println("Inside register");
                            out.writeUTF(getConsumerIP());
                            out.writeInt(getConsumerPort());
                            out.writeObject(artist); //successfully sends artistName to BrokerNode
                            out.flush();

                            ArrayList<String> m = (ArrayList<String>)in.readObject();
                            System.out.println("Map received from broker to consumer");
                            System.out.println(m.toString());

                            //sends song
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //η λιστα με τα τραγουδια του artist επιστρεφεται στον consumer
                        this.listofsongs = (ArrayList<String>) in.readObject();

                        //broker.pull(artist);
                    }else {


                        int newport = p.hashTopic(artist).getBrokerPort();
                        System.out.println("Port for right broker is: "+p.hashTopic(artist).getBrokerPort());
                        System.out.println("Disconnecting...");
                        disconnect(broker,artist);
                        connect(newport);

                        out.writeUTF(getConsumerIP());
                        out.writeInt(getConsumerPort());
                        out.writeObject(artist); //successfully sends artistName to BrokerNode
                        out.flush();

                        ArrayList<String> m = (ArrayList<String>)in.readObject();
                        System.out.println("Map received from broker to consumer");
                        System.out.println(m.toString());

                        //disconnect();....
                    }
                        //disconnect from random broker
                    //get port from correct broker
                    //connect with the correct broker

                    //else an den einai sostos o broker

                } catch (NoSuchAlgorithmException | ClassNotFoundException | IOException e) {
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
        ArtistName artistName = new ArtistName("Komiku");
        BrokerNode b = new BrokerNode("localhost", 7654);

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
