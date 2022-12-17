package edu.uga.m2gi.ar.channels.threadoriented;

public interface Channel {
    int read(byte[] bytes, int offset, int length);

    int write(byte[] bytes, int offset, int length);

    void disconnect();

    boolean disconnected();
}
