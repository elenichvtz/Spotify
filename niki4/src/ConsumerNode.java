import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
    public void register(BrokerNode broker, ArtistName artist) {
        /*Socket brker = getSocket();
        //ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(brker.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Object list = in.readObject();
        //send ip and port to broker
        try {
            out.writeUTF(getConsumerIP());
            out.writeInt(getConsumerPort());
            out.writeObject(artist); //successfully sends artistName to BrokerNode
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        for (int i =0; i< broker.getPublisherList().size();i++){
            if (broker.getPublisherList().get(i).getStart() >= (artist.getArtistName().charAt(0)) && broker.getPublisherList().get(i).getEnd() <= (artist.getArtistName().charAt(0))){
                try {
                    //broker.getPublisherList().get(i).hashTopic(artist); //returns the Broker responsible for that artist
                    if(broker.equals(broker.getPublisherList().get(i).hashTopic(artist))){ //if current broker equals the one returned from hashtopic then
                        Socket brker = getSocket();
                        //ObjectOutputStream out = null;

                        try {
                            out = new ObjectOutputStream(brker.getOutputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //Object list = in.readObject();
                        //send ip and port to broker
                        try {
                            out.writeUTF(getConsumerIP());
                            out.writeInt(getConsumerPort());
                            out.writeObject(artist); //successfully sends artistName to BrokerNode
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //lista tragoudion apo broker
                        this.listofsongs = (ArrayList<String>) in.readObject();

                        //broker.pull(artist);
                    }

                    //else an den einai sostos o broker

                } catch (NoSuchAlgorithmException | ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }

            }
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
        ListOfBrokers.add(b);
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

    //
    public void setConsumerPort(int port) { this.port = port; } //because when we find the right broker, we change the port to make a socket connection

    public static void main(String args[]){

        ConsumerNode cn = new ConsumerNode("localhost", 7654);
        cn.init();
        ArtistName artistName = new ArtistName("Komiku");
        cn.register(ListOfBrokers.get(0),artistName); //υποτιθεται οτι η λιστα με τους μπροκερσ πρεπει να ειναι γεματη

        //o broker tou epistrefei ti lista an uparxei o artist
        //an o artist den uparxei termatizei

        //vlepei ti lista me ta tragoudia tou kallitexni pou dialekse
        for(int i=0; i<cn.getListofSongs().size(); i++) {
            System.out.println(i +". " + cn.getListofSongs().get(i));
        }


        //o consumer epilegei ena tragoudi kai to stelnei ston broker
        try {
            cn.out.writeUTF("Bleu"); //iparxei ston Komiku to elegksa
        } catch (IOException e) {
            e.printStackTrace();
        }

        //lamvanei apo ton broker to value
        //kalei tin playData kai to apothikeuei




        //try {

            //Socket broker = cn.getSocket();

            //ObjectOutputStream out = new ObjectOutputStream(broker.getOutputStream());
            //ObjectInputStream in = new ObjectInputStream(broker.getInputStream());
            //ArtistName artistName = new ArtistName("Komiku");

            /*Object list = in.readObject();
            //send ip and port to broker
            out.writeUTF(cn.getConsumerIP());
            out.writeInt(cn.getConsumerPort());
            out.writeObject(artistName); //successfully sends artistName to BrokerNode
            out.flush();*/

        //} catch (IOException e) {
           // e.printStackTrace();
        //} catch (ClassNotFoundException e) {
            //e.printStackTrace();
        //}

        //ERROR IN PRINTING BROKER LIST!!!

        //List<Broker> k = cn.getBrokers();
        /*System.out.println(brokers.isEmpty());
        for(int i=0;i<cn.getBrokers().size();i++){
            System.out.println("Printing list.. " +cn.getBrokers().get(i));
        }*/

    }
}
