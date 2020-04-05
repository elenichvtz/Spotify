import com.mpatric.mp3agic.*;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static java.lang.Math.ceil;

public class PublisherNode implements Publisher, Serializable{

    Socket requestSocket = null;
    ServerSocket providerSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    String path = "C:\\Users\\nikim\\Desktop\\dataset1";
    char start;
    char end;
    String ip;
    int port;
    Object brokerkey;

    Map<String,ArrayList<String>> artistMap = new HashMap<String, ArrayList<String>>();

    PublisherNode(char start,char end, String ip, int port){
        this.start = start;
        this.end = end;
        this.ip = ip;
        this.port = port;
    }

    public Map<String,ArrayList<String>> getArtistMap() {
        return this.artistMap;
    }

    @Override
    public void init(){

        //Create artistMap for this publisher.
        File f = null;
        BufferedReader reader = null;

        Path dirPath = Paths.get(path);
        //System.out.println(dirPath.getFileName());
        try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPath)) {
            for (Path file : dirPaths) { //for every folder in path
                if (Files.isDirectory(file)) {
                    //System.out.println(file.getName(3).toString());
                    Path CurrentFolderContent = Paths.get(path.concat("/").concat(file.getFileName().toString()));
                    System.out.println(CurrentFolderContent.getFileName());

                    try (DirectoryStream<Path> currentsongs = Files.newDirectoryStream(CurrentFolderContent)) {//the songs in the current folder
                        if (!currentsongs.toString().startsWith(".")) {
                            for (Path songs : currentsongs) {
                                String foldercontents = path.concat("/").concat(file.getFileName().toString());

                                if (!songs.getFileName().toString().startsWith(".")) {
                                    try {

                                        Mp3File mp3file = new Mp3File(songs);

                                        if (mp3file.hasId3v2Tag()) {
                                            //System.out.println("YES FOR ID2");
                                            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                                            //System.out.println("Name of artist with tag version 2 is " + id3v2Tag.getArtist());

                                            if (id3v2Tag.getArtist()!=null && !id3v2Tag.getArtist().isBlank()) {
                                                if (id3v2Tag.getArtist().charAt(0) >= this.start && id3v2Tag.getArtist().charAt(0) <= this.end) {

                                                    //System.out.println(id3v2Tag.getArtist());
                                                    //System.out.println(artistMap.get(id3v2Tag.getArtist()));
                                                    if (!this.artistMap.containsKey(id3v2Tag.getArtist())) {

                                                        ArrayList<String> playlist = new ArrayList<String>();
                                                        playlist.add(id3v2Tag.getTitle());
                                                        this.artistMap.put(id3v2Tag.getArtist(), playlist);
                                                    } else {
                                                        ArrayList<String> playlist2 = this.artistMap.get(id3v2Tag.getArtist());
                                                        playlist2.add(id3v2Tag.getTitle());
                                                        this.artistMap.put(id3v2Tag.getArtist(), playlist2);
                                                    }
                                                }
                                            }
                                            else if((id3v2Tag.getArtist()==null || id3v2Tag.getArtist().isBlank()) && ('U'>= this.start && 'U'<=this.end)) {
                                                ArrayList<String> playlist3 = new ArrayList<String>();
                                                playlist3.add(id3v2Tag.getTitle());
                                                id3v2Tag.setArtist("Unknown");
                                                //System.out.println(id3v2Tag.getArtist());
                                                this.artistMap.put(id3v2Tag.getArtist(),playlist3);
                                            }
                                        }

                                        if (mp3file.hasId3v1Tag()) {
                                            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                                            //System.out.println("YES");

                                            if(id3v1Tag.getArtist()!=null && !id3v1Tag.getArtist().isBlank()) {
                                                if ((id3v1Tag.getArtist().charAt(0) >= this.start && id3v1Tag.getArtist().charAt(0) <= this.end)) { //if artist already exists

                                                    if (this.artistMap.containsKey(id3v1Tag.getArtist())) {
                                                        // playlist.add(id3v1Tag.getTitle());
                                                        ArrayList<String> playlist = this.artistMap.get(id3v1Tag.getArtist());
                                                        playlist.add(id3v1Tag.getTitle());
                                                        this.artistMap.put(id3v1Tag.getArtist(), playlist);
                                                    } else {
                                                        ArrayList<String> playlist2 = new ArrayList<String>();
                                                        playlist2.add(id3v1Tag.getTitle());
                                                        this.artistMap.put(id3v1Tag.getArtist(), playlist2);
                                                    }
                                                }
                                            }
                                            else if(id3v1Tag.getArtist()==null && ('U'>= this.start && 'U'<=this.end)) {
                                                ArrayList<String> playlist3 = new ArrayList<String>();
                                                playlist3.add(id3v1Tag.getTitle());
                                                id3v1Tag.setArtist("Unknown");
                                                //System.out.println(id3v1Tag.getArtist());
                                                this.artistMap.put(id3v1Tag.getArtist(),playlist3);
                                            }
                                        }
                                    } catch (InvalidDataException | UnsupportedTagException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //
        //Open up server socket and wait for broker to request a song.
        //then call push (on that broker)

        //find the right broker
        //send request for connection with that broker = open client socket on the same port


        //initialize sockets
        try {
            this.requestSocket = new Socket(this.ip, this.port);
            //this.in = new ObjectInputStream(this.requestSocket.getInputStream());
            //this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("wait");

        try {
            this.providerSocket = new ServerSocket(this.port+2, 10);
            //this.in = new ObjectInputStream(this.requestSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<BrokerNode> getBrokers() {
        return ListOfBrokers;
    }

    @Override
    public Broker hashTopic(ArtistName artist) throws NoSuchAlgorithmException{
        //Hashes the ArtistName

        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        String name = artist.getArtistName();

        byte[] namehash = sha.digest(name.getBytes());
        BigInteger big1 = new BigInteger(1,namehash); // 1 means positive BigInteger for hash(nameartist)

        BigInteger max = getBrokers().get(0).calculateKeys();

        for(int i=1; i<=2; i++){                               //vriskoume to megalutero kleidi ton brokers
            if ( getBrokers().get(i).calculateKeys().compareTo(max) > 1){
                max = getBrokers().get(i).calculateKeys();
            }
        }

        BigInteger hash2 = new BigInteger("max");// ΘΕΛΕΙ ΤΟ ΜΙΝ ΚΛΕΙΔΙ ΤΟΥ BROKER το απαντησε στο eclass

        BigInteger hashNumber = big1.mod(hash2);
        //brokerMap

        if((hashNumber.compareTo(getBrokers().get(0).calculateKeys()) == -1) && (hashNumber.compareTo(getBrokers().get(2).calculateKeys())==1)){
            return getBrokers().get(0);
        }
        else if(hashNumber.compareTo(getBrokers().get(1).calculateKeys()) == -1) {
            return getBrokers().get(1);
        }
        else{
            return getBrokers().get(2);
        }
    }

    public void push(ArtistName artist,Value val) {

        boolean exist = false;
        //getArtistMap();
        for (String name: getArtistMap().keySet()){

            if( name.equals(artist.toString())) {
                exist = true;
            }
        }

        File f = null;
        BufferedReader reader = null;
        int chunk_size = 512 * 1024;
        int counter=1;

        Path dirPath = Paths.get(path);
        try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPath)) { //stores the folders ex. "Comedy"  in the zip
            for (Path file : dirPaths) { //for every folder in path
                Path CurrentFolderContent = Paths.get(path.concat("//").concat(file.getFileName().toString()));
                try (DirectoryStream<Path> currentsongs = Files.newDirectoryStream(CurrentFolderContent)) {//the songs in the current folder
                    for (Path songs : currentsongs) {
                        String foldercontents = path.concat("//").concat(file.getFileName().toString());
                        try {
                            String songname = songs.getFileName().toString(); //return the name of the song in string
                            Mp3File mp3file = null;
                            try {
                                mp3file = new Mp3File(foldercontents.concat("//").concat(songs.getFileName().toString()));
                            } catch (UnsupportedTagException e) {
                                e.printStackTrace();
                            } catch (InvalidDataException e) {
                                e.printStackTrace();
                            }

                            if (mp3file.hasId3v1Tag()) {
                                ID3v1 id3v1Tag = mp3file.getId3v1Tag();

                                if (val.getMusicfile().getArtistName().equals(id3v1Tag.getArtist()) && (val.getMusicfile().getTrackName().equals(id3v1Tag.getTrack()))) {

                                    ByteArrayOutputStream byteout = new ByteArrayOutputStream();

                                    File file2 = new File(foldercontents.concat("//").concat(songs.getFileName().toString()));
                                    FileInputStream fis = new FileInputStream(file2);

                                    byte[] chunk = new byte[chunk_size];
                                    int numberOfChunks = (int)ceil(file2.length()/chunk_size);
                                    try {
                                        for (int readNum; (readNum = fis.read(chunk)) != -1; ) {
                                            byteout.write(chunk, 0, readNum);
                                            MusicFile musicfile = new MusicFile(id3v1Tag.getTitle(), id3v1Tag.getArtist(), id3v1Tag.getAlbum(),
                                                    id3v1Tag.getGenreDescription(),chunk, counter,numberOfChunks);

                                            counter++;
                                            val.setMusicfile(musicfile);

                                            //send chunk through socket
                                            while(true) {
                                                try {
                                                    //
                                                    //this.requestSocket = this.providerSocket.accept();
                                                    //this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());  //initialize out
                                                    this.out.writeObject(val);
                                                    this.out.flush();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break; //so it doesn't need to check next if
                            }

                            if (mp3file.hasId3v2Tag()){
                                ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                                if(val.getMusicfile().getArtistName().equals(id3v2Tag.getArtist()) && (val.getMusicfile().getTrackName().equals(id3v2Tag.getTrack()))) {
                                    ByteArrayOutputStream byteout = new ByteArrayOutputStream();

                                    File file2 = new File(foldercontents.concat("//").concat(songs.getFileName().toString()));
                                    FileInputStream fis = new FileInputStream(file2);

                                    byte[] chunk = new byte[chunk_size];
                                    int numberOfChunks = (int)ceil(file2.length()/chunk_size);
                                    try {
                                        for (int readNum; (readNum = fis.read(chunk)) != -1; ) {
                                            byteout.write(chunk, 0, readNum);
                                            MusicFile musicfile = new MusicFile(id3v2Tag.getTitle(), id3v2Tag.getArtist(), id3v2Tag.getAlbum(),
                                                    id3v2Tag.getGenreDescription(),chunk, counter,numberOfChunks);

                                            counter++;
                                            val.setMusicfile(musicfile);

                                            //send chunk through socket
                                            while(true) {
                                                try {
                                                    //
                                                    //this.requestSocket = this.providerSocket.accept();
                                                    out.writeInt(numberOfChunks); // sends also the number of chunks??? not sure if neeeded
                                                    this.out.writeObject(val);
                                                    this.out.flush();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //public Object returnBrokerKey () { return this.brokerkey; }

    @Override
    public void connect() {
        try {
            this.requestSocket = this.providerSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        //read request
        try {
            out = new ObjectOutputStream(this.requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(this.requestSocket.getInputStream());
            //we get the artist and title from the stream
            ArtistName artist = (ArtistName) in.readObject();
            String title = (String) in.readObject();

            MusicFile mf = new MusicFile(title);
            Value value = new Value(mf);
            this.push(artist, value); //we call push for the song the broker is looking for

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect(){
        try {
            //
            out.close();
            in.close();
            this.requestSocket.close();
            //this.providerSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return this.requestSocket;
    }

    public ServerSocket getServerSocket() {
        return this.providerSocket;
    }

    public String getPublisherIP() {
        return this.ip;
    }

    public int getPublisherPort() {
        return this.port;
    }
    
    public char getStart() {
        return this.start;
    }

    public char getEnd() {
        return this.end;
    }

    //
    public void setPublisherPort(int port) { this.port = port; } //because when we find the right broker, we change the port to make a socket connection

    @Override
    public void notifyFailure(Broker broker){
        //TODO: write code
        //not being the right publisher or not having the song/artist?????
    }

    public static void main(String args[]){

        //
        PublisherNode p1 = new PublisherNode('A', 'M', "localhost", 4856); //first publisher with random port
        PublisherNode p2 = new PublisherNode('N', 'Z', "localhost", 6572); //second publisher with random port

        for(BrokerNode broker : ListOfBrokers){ //we notify all brokers that these publishers exist
            broker.registeredPublishers.add(p1);
            broker.registeredPublishers.add(p2);
        }
        p1.init(); //Create artistMap for this publisher. Open up server socket and wait for broker to request a song.
        p2.init();
        while(true){
            p1.connect(); //this.requestSocket = this.providerSocket.accept();
            p2.connect();

            //create a thread
            p1.run(); //inside run we call push
            p2.run();

            //i think we have to disconnect the socket at this point but i'm not too sure
        }

        //PublisherNode p = new PublisherNode('A', 'M', "localhost", 7654);
        //PublisherNode p2 = new PublisherNode('M','Z',"localhost",7655);
        //p.init();

        /* from emy
        try {
            Socket broker = p.getSocket();

            ObjectOutputStream out = new ObjectOutputStream(broker.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(broker.getInputStream());
            
            //send ip, port, start and end to broker
            out.writeUTF(p.getPublisherIP());
            out.writeInt(p.getPublisherPort());
            out.writeChar(p.getStart());
            out.writeChar(p.getEnd());

            //send map to broker
            out.writeObject(p.getArtistMap());
            out.flush();
            //p.push();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        //p2.init();
        */
    }
}
