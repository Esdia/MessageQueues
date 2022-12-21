package edu.uga.m2gi.ar.messagequeue.eventoriented;

import edu.uga.m2gi.ar.messagequeue.eventoriented.listener.MessageListener;

public interface MessageQueue {
    void setListener(MessageListener listener);
    boolean send(byte[] bytes);
    boolean send(byte[] bytes, int offset, int length);
    void close();
    boolean closed();
}
