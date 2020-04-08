package src;
import java.io.*;

public class Serializable implements java.io.Serializable {

    public static void serialize(MusicFile track) throws IOException {

        OutputStream outStream = System.out;
        ObjectOutputStream objectOut = new ObjectOutputStream(outStream);
        objectOut.writeObject(track);
        objectOut.close();
        outStream.close();

    }

    public static void deserialize() throws IOException {

        InputStream inStream = System.in;
        MusicFile song = null;
        ObjectInputStream objectIn = new ObjectInputStream(inStream);
        try {
            song = (MusicFile)objectIn.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        inStream.close();
        objectIn.close();
    }
}
