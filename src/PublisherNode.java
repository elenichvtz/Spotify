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

//Client
public class PublisherNode implements Publisher{

    private Socket requestSocket = null;
    private ServerSocket providerSocket;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    String path = "C:\\Users\\eleni\\Downloads\\DS\\dataset1";
    char start;
    char end;
    String ip;
    int port;

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

        //initialize sockets
        try {
            this.requestSocket = new Socket(ip, port);
            //this.in = new ObjectInputStream(this.requestSocket.getInputStream());
            this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //send map to broker
        try {
            this.out.writeObject(this.artistMap);
            this.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*try {
            this.providerSocket = new ServerSocket(port, 10);
            this.in = new ObjectInputStream(this.requestSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //receive key from broker
        try {
            Object key = this.in.readObject();
            System.out.println(key.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public List<Broker> getBrokers() {
        return null;
    }

    @Override
    public Broker hashTopic(ArtistName artist) throws NoSuchAlgorithmException{
        //Hashes the ArtistName

        MessageDigest sha = MessageDigest.getInstance("SHA-256"); //το περιεχομενο εδω δεν ειμαι σιγουρη πως το δηλωνουμε , αλλου το εχει string αλλου σκετο
        String name = artist.getArtistName();

        byte[] namehash = sha.digest(name.getBytes());
        BigInteger big1 = new BigInteger(1,namehash); // 1 means positive

        BigInteger max = getBrokers().get(0).calculateKeys();

        for(int i=1; i<=2; i++){                               //vriskoume to megalutero kleidi ton brokers
            if ( getBrokers().get(i).calculateKeys().compareTo(max) > 1){
                max = getBrokers().get(i).calculateKeys();
            }
        }

        BigInteger hash2 = new BigInteger("max");

        BigInteger hashNumber = big1.mod(hash2);

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

    @Override
    public void connect() {
        try {
            this.requestSocket = this.providerSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect(){
        try {
            this.requestSocket.close();
            this.providerSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void notifyFailure(Broker broker){
        //TODO: write code
        //not being the right publisher or not having the song/artist?????
    }

    public static void main(String args[]){
        PublisherNode p = new PublisherNode('A', 'M', "127.0.0.1", 4321);
        p.init();
    }
}
