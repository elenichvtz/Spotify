import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Info implements Serializable {

    HashMap<String,Integer> info = new HashMap<>();
    ArtistName artist;
    Info(HashMap<String,Integer> info ,ArtistName artist ){
        this.info = info;
        this.artist = artist;
    }

}

/*
    public Map<Integer, ArrayList<ArtistName>> updateList(List<BrokerNode> broker ) throws NoSuchAlgorithmException {
        Map<Integer, ArrayList<ArtistName>> map = new HashMap<>();
        //for(int i =0; i < getPublisherList().size();i++){
        for(String name: registeredPublishers.get(0).getArtistMap().keySet()) {

            ArtistName art = new ArtistName(name);
            System.out.println("Inside updateList"+name);
            if (registeredPublishers.get(0).hashTopic(art).port == broker.get(0).port) {
                ArrayList<ArtistName> m = new ArrayList<>();
                m.add(art);
                map.put(broker.get(0).port,m);
            }
            if (getPublisherList().get(0).hashTopic(art).port == broker.get(1).port) {
                ArrayList<ArtistName> m2 = new ArrayList<>();
                m2.add(art);
                map.put(broker.get(1).port,m2);
            }
            if (registeredPublishers.get(0).hashTopic(art).port == broker.get(2).port) {
                ArrayList<ArtistName> m3 = new ArrayList<>();
                m3.add(art);
                map.put(broker.get(2).port,m3);
            }

        }
        //}
        return map;
    }

 */