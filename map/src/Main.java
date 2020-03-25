import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class Main {


    char start = 'A';
    char end = 'M';
    Map<String,ArrayList<String>> artistMap;



    public Map getMap(){
        return this.artistMap;
    }

    public void init(){
        String path = "/Users/emiliadan/Downloads/distributed_project/dataset1";
        File f = null;
        BufferedReader reader = null;

        Path dirPath = Paths.get(path);
        System.out.println(dirPath.getFileName());
        try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPath)) { //stores the folders ex. "Comedy"  in the zip
            for (Path file : dirPaths) { //for every folder in path
                if(!Files.isHidden(file)) {
                    //System.out.println(file.getName(3).toString());
                    Path CurrentFolderContent = Paths.get(path.concat("//").concat(file.getFileName().toString()));
                    System.out.println(CurrentFolderContent.getFileName());
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


                                    if ((id3v1Tag.getArtist().charAt(0) >= this.start && id3v1Tag.getArtist().charAt(0) <= this.end) && id3v1Tag.getArtist().isEmpty()) { //if artist already exists

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
                            } catch (IOException e) {
                                e.printStackTrace();
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
