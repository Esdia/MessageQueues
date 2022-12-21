package edu.uga.m2gi.ar.messagequeue.eventoriented.listener;

import edu.uga.m2gi.ar.messagequeue.eventoriented.MessageQueue;

public interface AcceptListener {
    void accepted(int port, MessageQueue queue);
}
