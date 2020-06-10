package com.example.spotify;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Scanner;

//Client
public class ConsumerNode extends Thread implements Consumer,Serializable {

    Socket requestSocket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    String ip;
    int port;
    ArrayList<String> listofsongs = new ArrayList<String>();
    int exist;


    ConsumerNode(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void init() {
        try {
            this.requestSocket = new Socket(this.ip, this.port + 1);
            this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
            this.in = new ObjectInputStream(this.requestSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect(int port) {
        try {
            requestSocket = new Socket(this.ip, port + 1);
            this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
            this.in = new ObjectInputStream(this.requestSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(BrokerNode broker, ArtistName artist, String ip, int port) {

        try {
            this.out.writeUTF(ip);
            this.out.writeInt(port);
            this.out.writeUTF(artist.getArtistName());
            this.out.flush();


            int brokerport = this.in.readInt();

            if (brokerport != broker.getBrokerPort()) {

                disconnect();

                connect(brokerport);

                this.out.writeUTF(ip);
                this.out.writeInt(port);

                this.out.writeUTF(artist.getArtistName()); //successfully sends artistName to BrokerNode
                this.out.flush();
            }
            int portbroker = this.in.readInt();
            exist = this.in.readInt();

            BrokerNode b = new BrokerNode("192.168.1.15", 7654);
            Scanner userInput = new Scanner(System.in);
            if (exist == 0) {

                System.out.println("Pick an artist: ");

                String artistN = userInput.nextLine();
                ArtistName artistName = new ArtistName(artistN);
                disconnect();
                connect(b.port);

                register(b, artistName, this.ip, this.port);

            }


            if (exist == 1) {

                //list of songs of the requested artist

                int size = this.in.readInt();
                System.out.println("size: "+ size);
                for(int i=0; i<size;i++) {
                    this.listofsongs.add(this.in.readUTF());
                }

                System.out.println(this.listofsongs.toString());



                boolean songflag = false;

                while (songflag == false) {
                    System.out.println("Pick a song: ");
                    String song = userInput.nextLine();
                    for (int i = 0; i < listofsongs.size(); i++) {
                        if (song.equals(listofsongs.get(i))) {
                            songflag = true;
                            this.out.writeUTF(song);
                            this.out.flush();
                            break;
                        }

                    }
                }
                MusicFile ms = new MusicFile(null, null, null, null, null, 0, 0);
                Value value = new Value(ms);
                playData(artist, value);

                disconnect();
            } else {
                System.out.println("The artist you searched doesn't exist.");
                System.out.println("Please try again.");
            }

        } catch (IOException  e) {
            e.printStackTrace();
        }

    }

    @Override
    public void disconnect() {
        try {
            this.out.close();
            this.in.close();
            this.requestSocket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playData(ArtistName artist, Value val) {

        int chunks = 0;
        String str ;
        //System.out.println("Song's name: " +str);
        ArrayList<Value> pieces = new ArrayList<>();

        try {
            str = in.readUTF();
            chunks = in.readInt();
            if(str==null) str="songReceived";

            FileOutputStream fileOuputStream = new FileOutputStream(str.concat(".mp3"));

            for (int i = 1; i <= chunks; i++) {
                Value value = new Value((MusicFile) in.readObject());

                fileOuputStream.write(value.getMusicfile().getMusicFileExtract());
                fileOuputStream.flush();

                pieces.add(value); //saves chunks locally
                out.writeUTF("ok");
                out.flush();
           }
            fileOuputStream.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws IOException {
        int i = 0;
        String str = "192.168.1.15";

        BrokerNode b = new BrokerNode(str, 7654);

        ConsumerNode cn = new ConsumerNode(str, 7654 );



        cn.init();
        System.out.println("ip and port: " + cn.ip + " " + cn.port);

        System.out.println("Pick an artist: ");

        Scanner userInput = new Scanner(System.in);
        String artist = userInput.nextLine();

        ArtistName artistName = new ArtistName(artist);
        int j = 0;

        cn.register(b, artistName, cn.ip, cn.port);

          /*  while (cn.exist == 0) {

                System.out.println("Pick an artist: ");
                userInput = new Scanner(System.in);
                artist = userInput.nextLine();
                artistName = new ArtistName(artist);
                cn.disconnect();
                cn.connect(b.port);

                cn.register(b, artistName, cn.ip, cn.port);

            }*/









    }
}




