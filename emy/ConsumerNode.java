import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

//Client
public class ConsumerNode extends Thread implements Consumer,Serializable {

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
            this.requestSocket = new Socket(this.ip, this.port+1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<BrokerNode> getBrokers() {
<<<<<<< HEAD
        return null;
=======
        return brokers;
>>>>>>> fcfeb9fb3a6dba7d65547ffd3b7229bdec553ae2
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
<<<<<<< HEAD

        for (int i =0; i< broker.getPublisherList().size();i++){
            if (broker.getPublisherList().get(i).getStart() == (artist.getArtistName().charAt(0))){
                try {
                    //broker.getPublisherList().get(i).hashTopic(artist); //returns the Broker responsible for that artist
                    if(broker.equals(broker.getPublisherList().get(i).hashTopic(artist))){ //if current broker equals the one returned from hashtopic then
                        broker.pull(artist);
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

            }
        }
        /*broker.getPublisherList();
=======
        //TODO: check if random Broker is correct then pull ,else search for right Broker
>>>>>>> fcfeb9fb3a6dba7d65547ffd3b7229bdec553ae2
        try {
            if(!brokerMap.containsKey(broker)) {
                System.out.println("Broker not found.");
                return;
            }
            else if (!brokerMap.containsValue(artist)){
                System.out.println("Artist not found.");
                return;
            }
            else{
                if(broker.equals(hashTopic(artist))){ //random broker is the correct broker
                    //instead of this if statement maybe we can make another map with artists and their hash values
                    //but i'm leaving this here in case we fix it
                    broker.pull(artist);
                }
                else{
                    for(Broker br : brokerMap.keySet()){ //for every broker in brokerMap
                        if(br.equals(broker)){ //when we find the correct broker
                            broker.pull(artist);
                        }
                    }
                }
            }*/

            //broker.notifyPublisher(artist.getArtistName());   //First notify
            /*
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeUTF(artist.getArtistName());         //then send
            out.flush(); //sent for sure
            //TODO: find the correct broker
            */
        /*
        } catch (IOException e) {
            e.printStackTrace();
        }*/





        //TODO: check if random Broker is correct then pull ,else search for right Broker
        /*try {

            //broker.notifyPublisher(artist.getArtistName());   //First notify

            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeUTF(artist.getArtistName());         //then send
            out.flush(); //sent for sure
            //TODO: find the correct broker


        } catch (IOException e) {
            e.printStackTrace();
        }*/
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

        try {

            Socket broker = cn.getSocket();

            ObjectOutputStream out = new ObjectOutputStream(broker.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(broker.getInputStream());
            ArtistName artistName = new ArtistName("Komiku");

            Object list = in.readObject();
            //send ip and port to broker
            out.writeUTF(cn.getConsumerIP());
            out.writeInt(cn.getConsumerPort());
            out.writeObject(artistName); //successfully sends artistName to BrokerNode
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //ERROR IN PRINTING BROKER LIST!!!

        //List<Broker> k = cn.getBrokers();
        /*System.out.println(brokers.isEmpty());
        for(int i=0;i<cn.getBrokers().size();i++){
            System.out.println("Printing list.. " +cn.getBrokers().get(i));
        }*/

    }
}
