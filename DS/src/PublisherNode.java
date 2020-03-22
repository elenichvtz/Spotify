import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//Client
public class PublisherNode extends NodeImpl implements Publisher{

    private Socket requestSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

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

    @Override
    public void disconnect() {
        try {
            requestSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void push(ArtistName artist,Value val){

        //find song in disk



        byte[] song = val.getMusicfile().getMusicFileExtract();
        int chunk_size = 512*1024; //512 KB at most
        try {
            out = new ObjectOutputStream(requestSocket.getOutputStream());  //initialize out

            for(int i =0; i < song.length; i+= chunk_size) { //send chunks of song

                out.write(song, 0, chunk_size+1);
                out.flush();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //TODO: Check if correct
    }

    public Value findSong(String path, Value val){

        File f = null;
        BufferedReader reader = null;

        try {
            Path dirPath = Paths.get("path");
            try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPath)) { //stores the folders ex. "Comedy"  in the zip
                for (Path file : dirPaths) { //for every folder in path
                    Path CurrentFolderContent = Paths.get(path.concat("//").concat(file.getFileName().toString()));
                    try (DirectoryStream<Path> currentsongs = Files.newDirectoryStream(CurrentFolderContent)) {//the songs in the current folder
                        for (Path songs : currentsongs) {
                            String foldercontents = path.concat("//").concat(file.getFileName().toString());
                            try{
                                String songname = songs.getFileName().toString(); //return the name of the song in string
                                Mp3File mp3file = null;
                                try {
                                    mp3file = new Mp3File(foldercontents.concat("//").concat(songs.getFileName().toString()));
                                } catch (UnsupportedTagException e) {
                                    e.printStackTrace();
                                } catch (InvalidDataException e) {
                                    e.printStackTrace();
                                }

                                File f2 = new File(foldercontents.concat("//").concat(songs.getFileName().toString()));
                                f2.length();

                                if (mp3file.hasId3v1Tag()) {
                                    ID3v1 id3v1Tag = mp3file.getId3v1Tag();

                                    if(val.getMusicfile().getArtistName().equals(id3v1Tag.getArtist()) && (val.getMusicfile().getTrackName().equals(id3v1Tag.getTrack()))){
                                        val.getMusicfile().setAlbumInfo(id3v1Tag.getAlbum());
                                        val.getMusicfile().setGenre(id3v1Tag.getGenre());

                                        val.getMusicfile().setMusicFileExtract(mp3file);
                                    }
                                }

                            }catch (IOException e){
                                e.printStackTrace();
                            }catch (InvalidDataException exception){
                                //add something

                            }

                        }


                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }catch(IOException e){
            e.printStackTrace();

        }

        return val;

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
