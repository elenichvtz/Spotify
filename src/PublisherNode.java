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
import java.util.*;


import static java.lang.Math.ceil;

//Client & Server
public class PublisherNode implements Publisher,Serializable{

    Socket requestSocket = null;
    ServerSocket providerSocket = null;

    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    ObjectOutputStream out2 = null;
    ObjectInputStream in2 = null;

    String path = "C:\\Users\\eleni\\Downloads\\DS\\dataset1";

    char start;
    char end;
    String ip;
    int port;

    int BrokerPort1 = 7654;
    int BrokerPort2 = 8765;
    int BrokerPort3 = 9876;

    Map<String,ArrayList<String>> artistMap = new HashMap<String, ArrayList<String>>();
    ArrayList<BrokerNode> brokerKeys = new ArrayList<>();

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
    public synchronized void init(){

        Path dirPath = Paths.get(path);
        try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPath)) {
            for (Path file : dirPaths) { //for every folder in path
                if (Files.isDirectory(file)) {
                    Path CurrentFolderContent = Paths.get(path.concat("/").concat(file.getFileName().toString()));
                    System.out.println(CurrentFolderContent.getFileName());

                    try (DirectoryStream<Path> currentsongs = Files.newDirectoryStream(CurrentFolderContent)) {//the songs in the current folder
                        if (!currentsongs.toString().startsWith(".")) {
                            for (Path songs : currentsongs) {
                                System.out.println("Song is: "+songs.getFileName());

                                if (!songs.getFileName().toString().startsWith(".")) {
                                    try {

                                        Mp3File mp3file = new Mp3File(songs);
                                        ID3v2 id3v2Tag;
                                        if (mp3file.hasId3v2Tag()) {
                                            System.out.println("Has id3v2");

                                            id3v2Tag = mp3file.getId3v2Tag();
                                            System.out.println("Is artist not null?: "+(id3v2Tag.getArtist()!=null));
                                            if (id3v2Tag.getArtist()!=null && !id3v2Tag.getArtist().isBlank() && id3v2Tag.getTitle()!=null && !id3v2Tag.getTitle().isBlank()) {
                                                System.out.println("Inside if");
                                                if (id3v2Tag.getArtist().charAt(0) >= this.start && id3v2Tag.getArtist().charAt(0) <= this.end) {

                                                    if (!this.artistMap.containsKey(id3v2Tag.getArtist())) {
                                                        ArrayList<String> playlist = new ArrayList<>();
                                                        if(!playlist.contains(id3v2Tag.getTitle())) { playlist.add(id3v2Tag.getTitle()); }
                                                        this.artistMap.put(id3v2Tag.getArtist(), playlist);
                                                    } else {
                                                        ArrayList<String> playlist2 = this.artistMap.get(id3v2Tag.getArtist());
                                                        playlist2.add(id3v2Tag.getTitle());
                                                        this.artistMap.put(id3v2Tag.getArtist(), playlist2);
                                                    }
                                                }
                                            }
                                            else if((id3v2Tag.getArtist()==null || id3v2Tag.getArtist().isBlank()) && id3v2Tag.getTitle()!=null && !id3v2Tag.getTitle().isBlank() && ('U'>= this.start && 'U'<=this.end)) {
                                                System.out.println("Inside else");
                                                System.out.println("Setted artist to: "+id3v2Tag.getAlbumArtist());
                                                id3v2Tag.setAlbumArtist("Unknown");
                                                if(!this.artistMap.containsKey("Unknown")) {
                                                    ArrayList<String> playlist3 = new ArrayList<String>();
                                                    playlist3.add(id3v2Tag.getTitle());
                                                    this.artistMap.put("Unknown",playlist3);
                                                    System.out.println(this.artistMap.toString());
                                                    System.out.println("Inside second if");
                                                }
                                                else {
                                                    ArrayList<String> temp = this.artistMap.get("Unknown");
                                                    temp.add(id3v2Tag.getTitle());
                                                    this.artistMap.put("Unknown",temp);
                                                    System.out.println("Inside second else");
                                                }
                                            }
                                        }
                                        else{
                                            id3v2Tag = new ID3v24Tag();
                                            mp3file.setId3v2Tag(id3v2Tag);

                                        }

                                        if (mp3file.hasId3v1Tag()) {
                                            System.out.println("Has id3v1");
                                            ID3v1 id3v1Tag = mp3file.getId3v1Tag();

                                            if (id3v1Tag.getArtist() != null && !id3v1Tag.getArtist().isBlank() && id3v1Tag.getTitle() != null && !id3v1Tag.getTitle().isBlank()) {
                                                if ((id3v1Tag.getArtist().charAt(0) >= this.start && id3v1Tag.getArtist().charAt(0) <= this.end)) { //if artist already exists

                                                    if (this.artistMap.containsKey(id3v1Tag.getArtist())) {
                                                        ArrayList<String> playlist = this.artistMap.get(id3v1Tag.getArtist());
                                                        if(!playlist.contains(id3v1Tag.getTitle())) { playlist.add(id3v1Tag.getTitle()); }
                                                        this.artistMap.put(id3v1Tag.getArtist(), playlist);
                                                    } else {
                                                        ArrayList<String> playlist2 = new ArrayList<String>();
                                                        playlist2.add(id3v1Tag.getTitle());
                                                        this.artistMap.put(id3v1Tag.getArtist(), playlist2);
                                                    }
                                                }
                                            } else if ((id3v1Tag.getArtist() == null || id3v1Tag.getArtist().isBlank()) && id3v1Tag.getTitle() != null && !id3v1Tag.getTitle().isBlank() && ('U' >= this.start && 'U' <= this.end)) {

                                                id3v1Tag.setArtist("Unknown");
                                                if (!this.artistMap.containsKey("Unknown")) {
                                                    ArrayList<String> playlist3 = new ArrayList<String>();
                                                    playlist3.add(id3v1Tag.getTitle());
                                                    this.artistMap.put(id3v1Tag.getArtist(), playlist3);
                                                } else {
                                                    ArrayList<String> temp = this.artistMap.get("Unknown");
                                                    temp.add(id3v1Tag.getTitle());
                                                    this.artistMap.put("Unknown", temp);
                                                }
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

        //initialize sockets

        try {
            this.requestSocket = new Socket(this.ip, this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("wait");

        try {
            this.providerSocket = new ServerSocket(this.port+2, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void updateList(){
        BrokerNode b = new BrokerNode("localhost",BrokerPort1);
        brokerKeys.add(b);
        BrokerNode b2 = new BrokerNode("localhost",BrokerPort2);
        brokerKeys.add(b2);
        BrokerNode b3 = new BrokerNode("localhost",BrokerPort3);
        brokerKeys.add(b3);
    }

    @Override
    public BrokerNode hashTopic(ArtistName artist) throws NoSuchAlgorithmException{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        String name = artist.getArtistName();

        byte[] namehash = sha.digest(name.getBytes());
        BigInteger big1 = new BigInteger(1,namehash); // 1 means positive BigInteger for hash(nameartist)

        BigInteger max = new BigInteger("-1");

        if(brokerKeys.size() == 0){
            updateList();
        }


        for (int i=0; i< brokerKeys.size(); i++){
            if (brokerKeys.get(i).calculateKeys().compareTo(max) == 1){
                max = brokerKeys.get(i).calculateKeys();

            }
        }

        ArrayList<BigInteger> keys = new ArrayList<>();
        for (int i =0;i<brokerKeys.size();i++){
            keys.add(brokerKeys.get(i).calculateKeys());

        }

        Collections.sort(keys);
        ArrayList<BrokerNode> brokerNodes = new ArrayList<>();

        for(int i =0 ;i<keys.size();i++){
            for(int j =0;j<brokerKeys.size();j++){
                if((keys.get(i)).compareTo(brokerKeys.get(j).calculateKeys()) == 0){
                    brokerNodes.add(brokerKeys.get(j));
                }
            }
        }

        BigInteger hashNumber = big1.mod(max);

        if((hashNumber.compareTo(keys.get(0)) == 1) && (hashNumber.compareTo(keys.get(1)) == -1)){
            return brokerNodes.get(1);
        }
        if((hashNumber.compareTo(keys.get(1)) == 1)&& (hashNumber.compareTo(keys.get(2)) == -1)){
            return brokerNodes.get(2);
        }
        return brokerNodes.get(0);
    }


    public void push(ArtistName artist,Value val) {

            int chunk_size = 512 * 1024;
            int counter = 1;

            Path dirPath = Paths.get(path);
            try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPath)) { //stores the folders ex. "Comedy"
                boolean found2 = false;
                for (Path file : dirPaths) { //for every folder in path
                    if (Files.isDirectory(file)) {
                        Path CurrentFolderContent = Paths.get(path.concat("/").concat(file.getFileName().toString()));
                        System.out.println("Push "+CurrentFolderContent.getFileName());
                        try (DirectoryStream<Path> currentsongs = Files.newDirectoryStream(CurrentFolderContent)) { //the songs in the current folder
                            if (!currentsongs.toString().startsWith(".")) {
                                boolean found = false;
                                for (Path songs : currentsongs) {
                                    System.out.println(songs.getFileName());
                                    if (!songs.getFileName().toString().startsWith(".")) {
                                        String foldercontents = path.concat("/").concat(file.getFileName().toString());

                                        try {
                                            Mp3File mp3file = null;
                                            try {
                                                mp3file = new Mp3File(foldercontents.concat("//").concat(songs.getFileName().toString()));
                                                System.out.println("Inside push...");
                                            } catch (UnsupportedTagException e) {
                                                e.printStackTrace();
                                            } catch (InvalidDataException e) {
                                                e.printStackTrace();
                                            }

                                            if (mp3file.hasId3v1Tag()) {
                                                System.out.println("Id3v1");
                                                ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                                                if (val.getMusicfile().getArtistName().equals(id3v1Tag.getArtist()) && (val.getMusicfile().getTrackName().equals(id3v1Tag.getTitle()))) {
                                                    System.out.println("Found the song2");
                                                    found = true;
                                                    ByteArrayOutputStream byteout = new ByteArrayOutputStream();

                                                    File file2 = new File(foldercontents.concat("//").concat(songs.getFileName().toString()));
                                                    FileInputStream fis = new FileInputStream(file2);
                                                    System.out.println("yo");
                                                    byte[] chunk = new byte[chunk_size];
                                                    int numberOfChunks = (int) ceil((float)file2.length() / chunk_size);
                                                    System.out.println("file length is "+file2.length()+" file/chunk is : "+(file2.length() / chunk_size));
                                                    this.out2.writeInt(numberOfChunks);
                                                    System.out.println("yo");
                                                    try {

                                                        for (int readNum; (readNum = fis.read(chunk)) != -1; ) {
                                                            byteout.write(chunk, 0, readNum);

                                                            MusicFile musicfile = new MusicFile(id3v1Tag.getTitle(), id3v1Tag.getArtist(), id3v1Tag.getAlbum(),
                                                                    id3v1Tag.getGenreDescription(), chunk, counter, numberOfChunks);

                                                            counter++;
                                                            chunk = new byte[chunk_size];
                                                            System.out.println("yo");
                                                            val.setMusicfile(musicfile);

                                                            //send chunk through socket
                                                            System.out.println("yo");
                                                            try {
                                                                this.out2.writeObject(val);
                                                                this.out2.flush();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                            if (mp3file.hasId3v2Tag()) {
                                                ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                                                System.out.println("Id3v2");

                                                if ((val.getMusicfile().getArtistName().equals(id3v2Tag.getArtist()) && (val.getMusicfile().getTrackName().equals(id3v2Tag.getTitle()))) || ((id3v2Tag.getArtist() == null) && (val.getMusicfile().getTrackName().equals(id3v2Tag.getTitle())))) {
                                                    System.out.println("Found the song");
                                                    found = true;

                                                    ByteArrayOutputStream byteout = new ByteArrayOutputStream();
                                                    System.out.println("yo");
                                                    File file2 = new File(foldercontents.concat("//").concat(songs.getFileName().toString()));
                                                    FileInputStream fis = new FileInputStream(file2);
                                                    System.out.println("yo");
                                                    byte[] chunk = new byte[chunk_size];
                                                    int numberOfChunks = (int) ceil((float)file2.length() / chunk_size);
                                                    System.out.println("file length is "+file2.length()+" file/chunk is : "+((float)file2.length() / chunk_size));
                                                    out2.writeInt(numberOfChunks);
                                                    out2.flush();
                                                    System.out.println("yo " +numberOfChunks);

                                                    try {
                                                        for (int readNum; (readNum = fis.read(chunk)) != -1;  ) {
                                                            byteout.write(chunk, 0, readNum);
                                                            MusicFile musicfile = new MusicFile(id3v2Tag.getTitle(), id3v2Tag.getArtist(), id3v2Tag.getAlbum(),
                                                                    id3v2Tag.getGenreDescription(), chunk, counter, numberOfChunks);
                                                            System.out.println(musicfile.getMusicFileExtract());
                                                            chunk = new byte[chunk_size];
                                                            counter++;
                                                            System.out.println("Counter is "+counter+" in music if it is "+musicfile.getChunkId());

                                                            System.out.println("Value id is "+val.getMusicfile().getChunkId());
                                                            System.out.println("yo");

                                                            try {
                                                                this.out2.writeObject(musicfile);
                                                                System.out.println("Value id is ..."+val.getMusicfile().getChunkId());
                                                                this.out2.flush();
                                                                System.out.println("Chunk send");
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                        System.out.println("yo end");
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (found) {
                                        found2 = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (found2) {
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public Socket getSocket() {
        return this.requestSocket;
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

    @Override
    public void notifyFailure(BrokerNode broker){
        //TODO: write code
        //not being the right publisher or not having the song/artist?????
    }

    public static void main(String args[]){
        PublisherNode p = new PublisherNode('A', 'M', "localhost", 7654);
        PublisherNode p2 = new PublisherNode('M','Z',"localhost",8765);
        p.init();
        p2.init();
        p.updateList();
        p2.updateList();

        System.out.println(p.getArtistMap().toString());
        System.out.println(p2.getArtistMap().toString());

        ArrayList<PublisherNode> publishers = new ArrayList<>();
        publishers.add(p);
        publishers.add(p2);

        publishers.parallelStream().forEach((publisher) -> {

            try {
                Socket broker = publisher.getSocket();

                publisher.out = new ObjectOutputStream(broker.getOutputStream());
                publisher.in = new ObjectInputStream(broker.getInputStream());

                //send ip, port, start and end to broker
                publisher.out.writeUTF(publisher.getPublisherIP());
                publisher.out.writeInt(publisher.getPublisherPort());
                publisher.out.writeChar(publisher.getStart());
                publisher.out.writeChar(publisher.getEnd());

                //send map to broker
                publisher.out.writeObject(publisher.getArtistMap());
                publisher.out.flush();

                System.out.println("Waiting...");

                while(true) {
                    try {
                        publisher.requestSocket = publisher.providerSocket.accept();
                        publisher.out2 = new ObjectOutputStream(publisher.requestSocket.getOutputStream());
                        publisher.in2 = new ObjectInputStream(publisher.requestSocket.getInputStream());
                        ArtistName artist = (ArtistName) publisher.in2.readObject(); // pull
                        System.out.println("Artist received from broker is: " + artist);
                        Value value = (Value) publisher.in2.readObject();
                        System.out.println("Value received from broker is: " + value.getMusicfile().getTrackName());
                        System.out.println("Port of broker that will use push is :" + publisher.getPublisherPort());
                        publisher.push(artist, value);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
