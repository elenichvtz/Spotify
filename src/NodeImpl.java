import java.util.List;

public class NodeImpl implements Node{

    @Override
    public void init(int x){

    }

    public void AddBroker(Broker broker){
        brokers.add(broker);

    }


    @Override
    public List<Broker> getBrokers() {
        return brokers;
    }


    @Override
    public void connect(){



    }

    @Override
    public void disconnect(){

    }

    @Override
    public void updateNodes(){

    }
}

