import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ConsumerNode extends Thread implements Consumer,Serializable {

    Socket requestSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    String ip;
    int port;
    //ArrayList<String> listofsongs = new ArrayList<String>(); //no need for this

    ConsumerNode(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void init() {
        try {
            //niki
            //this.requestSocket = new Socket(this.ip, this.port+1); //edw den kserw giati eixame +1 opote gi auto to allaksa
            //an einai lathos peite mou
            this.requestSocket = new Socket(this.ip, this.port);
            this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
            out.flush();
            this.in = new ObjectInputStream(this.requestSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<BrokerNode> getBrokers() {
        return ListOfBrokers;
    }

    @Override
    public void connect() {
        //niki
        //eilikrina mou fainetai pleon peritth auth h methodos
        //giati to socket to ftiaxnei h init
        //kai sthn register sundeomaste sto swsto broker
        //opote edw den kserw ti na balw. mporei kai na mh thelei kati!
        //upenthumizw to skeleton einai ligo moufa
        /*
        while(!requestSocket.isConnected()) {
            try {
                //connect to a broker??
                //requestSocket = new Socket(this.ip, this.port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void disconnect() {
        try {
            out.close();
            in.close();
            this.requestSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(BrokerNode broker, ArtistName artist) {

        //niki
        //we can access the PortArtist map through this random broker
        //Map<Integer, ArrayList<ArtistName>> PortArtist
        //we run through this map
        //when we find the broker that is responsible for the artist we want, we connect to its socket
        //meaning: we change consumers port to that broker's port
        //this.setConsumerPort(brokerPort);

        for(Integer port : broker.PortArtist.keySet()){ //for every port in the PortArtist map
            for(ArtistName art : broker.PortArtist.get(port)){ //for every artist that this specific port serves
                if(art.equals(artist)) {
                    this.setConsumerPort(port);
                    break;
                }
            }
        }


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

        //from emy
        /*
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

        //niki
        try {
            //we read from the broker's inputstream bc that's where the chunk will be written
            //(pull method is the one that writes the chunk (= the Value object) on the inputstream!)
            this.in = new ObjectInputStream(this.requestSocket.getInputStream());
            // read the list of messages from the socket
            Value valueReceived = (Value) this.in.readObject();
            //we get the number of awaited chunks
            int chunks = valueReceived.getMusicfile().getTotalChunks();
            //we create a Value array that has size = totalChunks
            ArrayList<Value> pieces = new ArrayList<>(chunks);
            //we put our current chunk in array[chunkId]
            pieces.add(valueReceived.getMusicfile().getChunkId(), valueReceived);

            //we need a while loop so that we can keep reading the rest of the chunks
            //but i'm not sure how to do it

            //maybe (and i mean MAYBE) we turn this array into mp3 or something playable
            //we play it

        } catch (Exception e) {
            e.printStackTrace();
        }


        //from emy
        /*
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
        */
    }

    //public ArrayList<String> getListofSongs() { return this.listofsongs; }

    //niki
    public void setConsumerPort(int port) { this.port = port; } //because when we find the right broker, we change the port to make a socket connection

    public static void main(String args[]){

        //niki
        ConsumerNode cn = new ConsumerNode("localhost", 5791);
        cn.init();

        Scanner userInput = new Scanner(System.in);
        System.out.println("Artist name: ");
        String artist = userInput.nextLine(); //reads first user input which is the artist name
        ArtistName Artist = new ArtistName(artist);

        Random r = new Random();
        BrokerNode randomBroker = cn.getBrokers().get(r.nextInt(ListOfBrokers.size()));
        cn.register(randomBroker, Artist);

        System.out.println("Song title: ");
        String title = userInput.nextLine(); //reads second user input which is the song title

        MusicFile musicFile = new MusicFile(title);
        Value v = new Value(musicFile);

        //we send artist + title through OutputStream so that broker can read them in pull(?) and fetch the song
        //skefthka mhpws ayto to ekane h connect alla h connect den pairnei orismata kai ara pws tha kserei ti na grapsei sto OutputStream
        try{
            cn.out.writeObject(Artist);
            cn.out.flush();
            cn.out.writeObject(v);
            cn.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


        cn.playData(Artist, v); //because playData takes an ArtistName and a Value as parameters

        //we're missing a check somewhere to make sure the artist and the song exist in the files..
        //ALLA oi bohthoi eipan oti den tha psaksoun gia tragoudi pou den uparxei
        //so maybe we can sweep this under the rug

        //from emy
        /*
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
