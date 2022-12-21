package edu.uga.m2gi.ar.messagequeue.eventoriented;

import edu.uga.m2gi.ar.messagequeue.eventoriented.listener.AcceptListener;
import edu.uga.m2gi.ar.messagequeue.eventoriented.listener.ConnectListener;

public interface QueueBroker {
    String getName();
    boolean bind(int port, AcceptListener listener);
    boolean connect(String name, int port, ConnectListener listener);
}
