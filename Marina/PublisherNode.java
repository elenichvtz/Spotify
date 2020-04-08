package src;
import java.io.Serializable;
import com.mpatric.mp3agic.*;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


import static java.lang.Math.ceil;

//Client
public class PublisherNode implements Publisher,Serializable {

    Socket requestSocket = null;
    ServerSocket providerSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    String path = "C:\\Users\\user\\Documents\\mathimata\\6o Εξάμηνο\\Κατανεμημένα Συστήματα\\Project\\dataset1";
    char start;
    char end;
    String ip;
    int port;
    Object brokerkey;

    int BrokerPort1 = 7654;
    int BrokerPort2 = 8765;
    int BrokerPort3 = 9876;

    Map<ArtistName, ArrayList<String>> artistMap = new HashMap<ArtistName, ArrayList<String>>();
    ArrayList<BrokerNode> brokerKeys = new ArrayList<>();

    PublisherNode(char start, char end, String ip, int port) {
        this.start = start;
        this.end = end;
        this.ip = ip;
        this.port = port;
    }

    public Map<ArtistName, ArrayList<String>> getArtistMap() {
        return this.artistMap;
    }

    @Override
    public void init(int x) {
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

                                            ArtistName artist = new ArtistName(id3v2Tag.getArtist());
                                            //System.out.println("Name of artist with tag version 2 is " + id3v2Tag.getArtist());

                                            if (id3v2Tag.getArtist() != null && !id3v2Tag.getArtist().isBlank()) {
                                                if (id3v2Tag.getArtist().charAt(0) >= this.start && id3v2Tag.getArtist().charAt(0) <= this.end) {

                                                    //System.out.println(id3v2Tag.getArtist());
                                                    //System.out.println(artistMap.get(id3v2Tag.getArtist()));
                                                    if (!this.artistMap.containsKey(artist)) {

                                                        ArrayList<String> playlist = new ArrayList<String>();
                                                        playlist.add(id3v2Tag.getTitle());


                                                        this.artistMap.put(artist, playlist);
                                                    } else {
                                                        ArrayList<String> playlist2 = this.artistMap.get(artist);
                                                        playlist2.add(id3v2Tag.getTitle());
                                                        this.artistMap.put(artist, playlist2);
                                                    }
                                                }
                                            } else if ((id3v2Tag.getArtist() == null || id3v2Tag.getArtist().isBlank()) && ('U' >= this.start && 'U' <= this.end)) {
                                                ArrayList<String> playlist3 = new ArrayList<String>();
                                                playlist3.add(id3v2Tag.getTitle());
                                                id3v2Tag.setArtist("Unknown");
                                                //System.out.println(id3v2Tag.getArtist());
                                                this.artistMap.put(artist, playlist3);
                                            }
                                        }

                                        if (mp3file.hasId3v1Tag()) {
                                            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                                            ArtistName artist = new ArtistName(id3v1Tag.getArtist());
                                            //System.out.println("YES");

                                            if (id3v1Tag.getArtist() != null && !id3v1Tag.getArtist().isBlank()) {
                                                if ((id3v1Tag.getArtist().charAt(0) >= this.start && id3v1Tag.getArtist().charAt(0) <= this.end)) { //if artist already exists

                                                    if (this.artistMap.containsKey(artist)) {
                                                        // playlist.add(id3v1Tag.getTitle());
                                                        ArrayList<String> playlist = this.artistMap.get(artist);
                                                        playlist.add(id3v1Tag.getTitle());
                                                        this.artistMap.put(artist, playlist);
                                                    } else {
                                                        ArrayList<String> playlist2 = new ArrayList<String>();
                                                        playlist2.add(id3v1Tag.getTitle());
                                                        this.artistMap.put(artist, playlist2);
                                                    }
                                                }
                                            } else if (id3v1Tag.getArtist() == null && ('U' >= this.start && 'U' <= this.end)) {
                                                ArrayList<String> playlist3 = new ArrayList<String>();
                                                playlist3.add(id3v1Tag.getTitle());
                                                id3v1Tag.setArtist("Unknown");
                                                //System.out.println(id3v1Tag.getArtist());
                                                this.artistMap.put(artist, playlist3);
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
        if(x==1) {
            try {
                InetAddress host = InetAddress.getLocalHost();
                this.requestSocket = new Socket(host.getHostName(), this.port);
                System.out.println("wait");
                out.writeObject(artistMap);

                //this.in = new ObjectInputStream(this.requestSocket.getInputStream());
                //this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else{
            try {
                this.providerSocket = new ServerSocket(this.port + 2, 10);
                //this.in = new ObjectInputStream(this.requestSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateList() {
        BrokerNode b = new BrokerNode("localhost", BrokerPort1); //na to ksanadw
        brokerKeys.add(b);
        BrokerNode b2 = new BrokerNode("localhost", BrokerPort2);
        brokerKeys.add(b2);
        BrokerNode b3 = new BrokerNode("localhost", BrokerPort3);
        brokerKeys.add(b3);
    }

    public BigInteger findMax() {
        BigInteger max = new BigInteger("-1");


        for (int i = 0; i < brokerKeys.size(); i++) {
            if (brokerKeys.get(i).calculateKeys().compareTo(max) > 1) {
                max = brokerKeys.get(i).calculateKeys();
            }
        }
        return max;
    }

    @Override
    public List<BrokerNode> getBrokers() {
        return brokers;
    }

    @Override
    public BrokerNode hashTopic(ArtistName artist) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        String name = artist.getArtistName();

        byte[] namehash = sha.digest(name.getBytes());
        BigInteger big1 = new BigInteger(1, namehash); // 1 means positive BigInteger for hash(nameartist)

        BigInteger max = new BigInteger("-1");

        if (brokerKeys.size() == 0) {
            updateList();
        }


        for (int i = 0; i < brokerKeys.size(); i++) {
            if (brokerKeys.get(i).calculateKeys().compareTo(max) == 1) {
                max = brokerKeys.get(i).calculateKeys();

            }
        }

        ArrayList<BigInteger> keys = new ArrayList<>();
        for (int i = 0; i < brokerKeys.size(); i++) {
            keys.add(brokerKeys.get(i).calculateKeys());

        }

        Collections.sort(keys);
        ArrayList<BrokerNode> brokerNodes = new ArrayList<>();

        for (int i = 0; i < keys.size(); i++) {
            for (int j = 0; j < brokerKeys.size(); j++) {
                if ((keys.get(i)).compareTo(brokerKeys.get(j).calculateKeys()) == 0) {

                    brokerNodes.add(brokerKeys.get(j));

                }
            }
        }

        BigInteger hashNumber = big1.mod(max);

        if ((hashNumber.compareTo(keys.get(0)) == 1) && (hashNumber.compareTo(keys.get(1)) == -1)) {
            return brokerNodes.get(1);

        }
        if ((hashNumber.compareTo(keys.get(1)) == 1) && (hashNumber.compareTo(keys.get(2)) == -1)) {
            return brokerNodes.get(2);

        }
        return brokerNodes.get(0);
    }


    public void push(ArtistName artist, Value val) { //stin main tou publisher


        File f = null;
        BufferedReader reader = null;
        int chunk_size = 512 * 1024;
        int counter = 1;

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
                                    int numberOfChunks = (int) ceil(file2.length() / chunk_size);
                                    try {
                                        for (int readNum; (readNum = fis.read(chunk)) != -1; ) {
                                            byteout.write(chunk, 0, readNum);
                                            MusicFile musicfile = new MusicFile(id3v1Tag.getTitle(), id3v1Tag.getArtist(), id3v1Tag.getAlbum(),
                                                    id3v1Tag.getGenreDescription(), chunk, counter, numberOfChunks);

                                            counter++;
                                            val.setMusicfile(musicfile);

                                            //send chunk through socket
                                            while (true) { // why while???????????????????????
                                                try {
                                                    this.requestSocket = this.providerSocket.accept();
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

                            if (mp3file.hasId3v2Tag()) {
                                ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                                if (val.getMusicfile().getArtistName().equals(id3v2Tag.getArtist()) && (val.getMusicfile().getTrackName().equals(id3v2Tag.getTrack()))) {
                                    ByteArrayOutputStream byteout = new ByteArrayOutputStream();

                                    File file2 = new File(foldercontents.concat("//").concat(songs.getFileName().toString()));
                                    FileInputStream fis = new FileInputStream(file2);

                                    byte[] chunk = new byte[chunk_size];
                                    int numberOfChunks = (int) ceil(file2.length() / chunk_size);
                                    try {
                                        for (int readNum; (readNum = fis.read(chunk)) != -1; ) {
                                            byteout.write(chunk, 0, readNum);
                                            MusicFile musicfile = new MusicFile(id3v2Tag.getTitle(), id3v2Tag.getArtist(), id3v2Tag.getAlbum(),
                                                    id3v2Tag.getGenreDescription(), chunk, counter, numberOfChunks);

                                            counter++;
                                            val.setMusicfile(musicfile);

                                            //send chunk through socket
                                            while (true) {
                                                try {
                                                    this.requestSocket = this.providerSocket.accept();
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
        //else{
        //notifyFailure();
        //}

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

    @Override
    public void disconnect(int x) {
        try {
            if(x==1)  this.requestSocket.close();
            else this.providerSocket.close();
        } catch (IOException e) {
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

    @Override
    public void notifyFailure(BrokerNode broker) {
        //TODO: write code
        try {
            out.writeUTF(broker.getBrokerIP());
            out.writeInt(broker.getBrokerPort());
            out.writeObject("Song doesn't exist"); //successfully sends artistName to BrokerNode
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //not having the song/artist
    }

    public static void main(String args[]) {

            PublisherNode p = new PublisherNode('A', 'M', "localhost", 7654);
            PublisherNode p2 = new PublisherNode('M', 'Z', "localhost", 8765);


            p.init(1);
            p.disconnect(1);
            p.init(2);
            p2.init(2);

            ArrayList<PublisherNode> publishers = new ArrayList<>();
            publishers.add(p);
            publishers.add(p2);



        publishers.parallelStream().forEach((publisher) -> {

                Thread t1 = new Thread() {
                    public void run() {
                //for(int i=0; i<3; i++) {
                try {


                    Socket broker = publisher.getSocket();

                    ObjectOutputStream out = new ObjectOutputStream(broker.getOutputStream());
                    //ObjectInputStream in = new ObjectInputStream(broker.getInputStream());

                    //send ip, port, start and end to broker
                    out.writeUTF(publisher.getPublisherIP());
                    out.writeInt(publisher.getPublisherPort());
                    out.writeChar(publisher.getStart());
                    out.writeChar(publisher.getEnd());

                    System.out.println("sent");

                    //send map to broker
                    out.writeObject(publisher.getArtistMap());
                    out.flush();
                    //p.push();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //}

            }
        };
                t1.start();



    });
    }
}
