
public interface Consumer extends Node {

    public abstract void register(BrokerNode broker,ArtistName artist, String ip, int port);

    public abstract void disconnect(BrokerNode broker,ArtistName artist);

    public abstract void playData(ArtistName artist, Value val);
}
