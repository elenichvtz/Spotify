
public class Address {

    String ip;
    int port;

    Address(){
        this.ip = "127.0.0.0";
        this.port = 4321;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
