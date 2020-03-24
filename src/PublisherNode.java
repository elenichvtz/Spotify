import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

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

import static java.lang.Math.ceil;


//Client
public class PublisherNode extends NodeImpl implements Publisher{



    private Socket requestSocket = null;
    private ServerSocket providerSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    String path = "distributed_project/dataset1";
    ArrayList<ArtistName> playlist = new ArrayList<>();
    char start;
    char end;


    PublisherNode(char start,char end){
        this.start = start;
        this.end = end;

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

                            //File f2 = new File(foldercontents.concat("//").concat(songs.getFileName().toString()));
                            //f2.length();

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
                                            try {
                                                out = new ObjectOutputStream(this.requestSocket.getOutputStream());  //initialize out
                                                out.writeObject(val);
                                                out.flush();
                                            } catch (IOException e) {
                                                e.printStackTrace();
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
    public void connect(){
        while(!this.requestSocket.isConnected()) {
            try {
                this.requestSocket = new Socket("127.0.0.1", 4321);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.providerSocket = new ServerSocket(4321, 10);
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

        System.out.println("ALL GOOD 😎");



    }
}
