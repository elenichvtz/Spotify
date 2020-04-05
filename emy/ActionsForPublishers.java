import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionsForPublishers extends Thread {

    ObjectInputStream in;
    ObjectOutputStream out;
    List<PublisherNode> registeredPublishers = new ArrayList<>();
    PublisherNode pn;
    Map<String,ArrayList<String>> publishermap = new HashMap<String, ArrayList<String>>();


    public ActionsForPublishers(Socket publisher, List<PublisherNode> registeredPublishers) {
        try {
            out = new ObjectOutputStream(publisher.getOutputStream());
            in = new ObjectInputStream(publisher.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public PublisherNode getPublisher(){
        return pn;
    }

    public Map getPublishermap() {
        return publishermap;
    }

    public void run() {
        try {
            //ObjectInputStream in = new ObjectInputStream(publisher.getInputStream());

            //receive map, ip and port from publisher
            String publisherip = in.readUTF();
            System.out.println(publisherip);
            int publisherport = in.readInt();
            System.out.println(publisherport);
            char start = in.readChar();
            char end = in.readChar();
            System.out.println(start + " & " + end);

            publishermap = (Map<String, ArrayList<String>>)in.readObject();
            System.out.println(publishermap.toString());

            pn = new PublisherNode(start, end, publisherip, publisherport);

            //out.writeObject(pn);
            //registeredPublishers.add(pn);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}
