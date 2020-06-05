package com.example.spotify;

import java.security.NoSuchAlgorithmException;

public interface Publisher extends Node {

    public abstract Broker hashTopic(ArtistName artist) throws NoSuchAlgorithmException;

    public  abstract void push(ArtistName artist, Value val);
}
