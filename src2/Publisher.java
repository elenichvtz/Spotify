<<<<<<< HEAD
import java.util.List;
import java.io.*;
=======
>>>>>>> 38f1844698dffb311f7865830279b91993cbe333

import java.io.*;
//client
public interface Publisher extends Node {

    public abstract void getBrokerList();

    public abstract Broker hashTopic(ArtistName artist);

    public abstract void push(ArtistName artist,Value val);

    public abstract void notifyFailure(Broker broker);
}
