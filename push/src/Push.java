import com.mpatric.mp3agic.*;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Math.ceil;

public class Push {

    public void push(ArtistName artist,Value val) { //stin main tou publisher

        String path = "/Users/emiliadan/Downloads/distributed_project/dataset1";

        File f = null;
        BufferedReader reader = null;
        int chunk_size = 512 * 1024;
        int counter = 1;

        Path dirPath = Paths.get(path);
        try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPath)) { //stores the folders ex. "Comedy"  in the zip
            for (Path file : dirPaths) { //for every folder in path
                if (Files.isDirectory(file)) {
                    Path CurrentFolderContent = Paths.get(path.concat("/").concat(file.getFileName().toString()));
                    System.out.println("Push "+CurrentFolderContent.getFileName());
                    try (DirectoryStream<Path> currentsongs = Files.newDirectoryStream(CurrentFolderContent)) {//the songs in the current folder
                        if (!currentsongs.toString().startsWith(".")) {
                            boolean l = false;
                            for (Path songs : currentsongs) {
                                System.out.println(songs.getFileName());
                                if (!songs.getFileName().toString().startsWith(".")) {
                                    String foldercontents = path.concat("/").concat(file.getFileName().toString());

                                    try {
                                        String songname = songs.getFileName().toString(); //return the name of the song in string
                                        Mp3File mp3file = null;
                                        try {
                                            mp3file = new Mp3File(songs);
                                            System.out.println("Inside push...");
                                        } catch (UnsupportedTagException e) {
                                            e.printStackTrace();
                                        } catch (InvalidDataException e) {
                                            e.printStackTrace();
                                        }

                                        if (mp3file.hasId3v1Tag()) {
                                            System.out.println("Id3v1");
                                            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                                            //System.out.println("Our artist is: "+val.getMusicfile().getArtistName());
                                            //System.out.println("Are they equal? "+val.getMusicfile().getArtistName().equals(id3v1Tag.getArtist()));
                                            //System.out.println("The current artist found "+id3v1Tag.getArtist());
                                            //System.out.println(id3v1Tag.getArtist()+ " song is : "+id3v1Tag.getTitle());
                                            //System.out.println("Is id3v1 "+val.getMusicfile().getTrackName());
                                            if (val.getMusicfile().getArtistName().equals(id3v1Tag.getArtist()) && (val.getMusicfile().getTrackName().equals(id3v1Tag.getTitle()))) {
                                                System.out.println("Found the song");
                                                l = true;
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

                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            break; //so it doesn't need to check next if
                                        }

                                        if (mp3file.hasId3v2Tag()) {
                                            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                                            System.out.println("Id3v2");

                                            if (val.getMusicfile().getArtistName().equals(id3v2Tag.getArtist()) && (val.getMusicfile().getTrackName().equals(id3v2Tag.getTitle()))) {
                                                System.out.println("Found the song");
                                                ByteArrayOutputStream byteout = new ByteArrayOutputStream();
                                                System.out.println("FOUND SONG");

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
                            if (l) {
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void main(String args[]) {

        MusicFile f = new MusicFile("Champ de tournesol", "Komiku", null, null, null, 0, 0);
        Value value = new Value(f);
        Push p = new Push();
        ArtistName artist = new ArtistName("Komiku");
        p.push(artist,value);

    }
}
