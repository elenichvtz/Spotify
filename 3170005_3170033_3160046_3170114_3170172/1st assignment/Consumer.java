package com.example.spotify;

public interface Consumer extends Node {

    public abstract void register(BrokerNode broker, ArtistName artist, String ip, int port);

    public abstract void disconnect();

    public abstract void playData(ArtistName artist, Value val);
}
