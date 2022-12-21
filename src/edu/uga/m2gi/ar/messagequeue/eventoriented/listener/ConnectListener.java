package edu.uga.m2gi.ar.messagequeue.eventoriented.listener;

import edu.uga.m2gi.ar.messagequeue.eventoriented.MessageQueue;

public interface ConnectListener {
    void connected(String name, int port, MessageQueue queue);
    void refused(String name, int port);
}
