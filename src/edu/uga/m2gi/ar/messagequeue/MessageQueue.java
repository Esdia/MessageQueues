package edu.uga.m2gi.ar.messagequeue;

public interface MessageQueue {
    void send(byte[] bytes, int offset, int length) throws QueueClosedException;
    byte[] receive() throws QueueClosedException;
    void close();
    boolean closed();
}
