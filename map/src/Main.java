import com.mpatric.mp3agic.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {


    char start = 'A';
    char end = 'M';
    Map<String,ArrayList<String>> artistMap = new HashMap<String, ArrayList<String>>();

    public Map getMap(){
        return this.artistMap;
    }

    public void init() {
        String path = "C:/Users/eleni/Downloads/DS/dataset1";
        File f = null;
        BufferedReader reader = null;

        Path dirPath = Paths.get(path);
        System.out.println(dirPath.getFileName());
        try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPath)) { //stores the folders ex. "Comedy"  in the zip
            for (Path file : dirPaths) { //for every folder in path
                if (Files.isDirectory(file)) {
                    //System.out.println(file.getName(3).toString());
                    Path CurrentFolderContent = Paths.get(path.concat("/").concat(file.getFileName().toString()));
                    System.out.println(CurrentFolderContent.getFileName());
                    //if(CurrentFolderContent.startsWith(".")){
                    System.out.println("fghug");
                    try (DirectoryStream<Path> currentsongs = Files.newDirectoryStream(CurrentFolderContent)) {//the songs in the current folder
                        if (!currentsongs.toString().startsWith(".")) {
                            for (Path songs : currentsongs) {
                                String foldercontents = path.concat("/").concat(file.getFileName().toString());

                                //System.out.println(foldercontents+" giuhgfrhurfhjfrghfrg");
                                if (!songs.getFileName().toString().startsWith(".")) {
                                    try {
                                        //String songname = songs.getFileName().toString(); //return the name of the song in string
                                        //System.out.println(songname);
                                        Mp3File mp3file = new Mp3File(songs);
                                        System.out.println("575757");
                                        //Mp3File mp3file = new Mp3File(foldercontents.concat("/").concat(songs.getFileName().toString()));
                                        System.out.println(mp3file.getId3v2Tag().getTitle());

                                        if (mp3file.hasId3v2Tag()) {
                                            System.out.println("YES FOR ID2");
                                            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                                            System.out.println("Name of artist with tag version 2 is " + id3v2Tag.getArtist());

                                            if (id3v2Tag.getArtist()!=null && (id3v2Tag.getArtist().charAt(0) >= this.start && id3v2Tag.getArtist().charAt(0) <= this.end)) {
                                                System.out.println("2228328384756547");
                                                System.out.println(id3v2Tag.getArtist());
                                                System.out.println(artistMap.get(id3v2Tag.getArtist()));
                                                if (!this.artistMap.containsKey(id3v2Tag.getArtist())) {
                                                    System.out.println("rguhgfhu");
                                                    ArrayList<String> playlist = new ArrayList<String>();
                                                    playlist.add(id3v2Tag.getTitle());
                                                    this.artistMap.put(id3v2Tag.getArtist(), playlist);
                                                } else {
                                                    ArrayList<String> playlist2 = this.artistMap.get(id3v2Tag.getArtist());
                                                    playlist2.add(id3v2Tag.getTitle());
                                                    this.artistMap.put(id3v2Tag.getArtist(), playlist2);
                                                }
                                            }
                                            else if(id3v2Tag.getArtist()==null && ('U'>= this.start && 'U'<=this.end)) {
                                                ArrayList<String> playlist3 = new ArrayList<String>();
                                                playlist3.add(id3v2Tag.getTitle());
                                                id3v2Tag.setArtist("Unknown");
                                                System.out.println(id3v2Tag.getArtist());
                                                this.artistMap.put(id3v2Tag.getArtist(),playlist3);
                                            }
                                        }

                                        if (mp3file.hasId3v1Tag()) {
                                            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                                            System.out.println("YES");


                                            if ((id3v1Tag.getArtist().charAt(0) >= this.start && id3v1Tag.getArtist().charAt(0) <= this.end) && !id3v1Tag.getArtist().isEmpty()) { //if artist already exists
                                                System.out.println("YES");
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
    }

    public static void main(String[] args) {
	// write your code here
        Main m = new Main();
        m.init();
        Map<String , ArrayList<String>> k = m.getMap();
        for (String name: k.keySet()){

            String value = k.get(name).toString();
            System.out.println(name + " " + value);
        }

    }
}
