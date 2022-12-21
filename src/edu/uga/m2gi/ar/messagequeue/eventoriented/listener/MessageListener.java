package edu.uga.m2gi.ar.messagequeue.eventoriented.listener;

public interface MessageListener {
    void received(byte[] message);
    void sent(byte[] message, int offset, int length);
    void closed();
}
