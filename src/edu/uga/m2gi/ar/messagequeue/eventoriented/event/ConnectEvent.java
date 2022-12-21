package edu.uga.m2gi.ar.messagequeue.eventoriented.event;

import edu.uga.m2gi.ar.messagequeue.eventoriented.QueueBrokerImpl;
import edu.uga.m2gi.ar.messagequeue.eventoriented.listener.ConnectListener;

public class ConnectEvent extends Event {
    private final QueueBrokerImpl broker;
    private final String name;
    private final int port;
    private final ConnectListener listener;

    public ConnectEvent(QueueBrokerImpl broker, String name, int port, ConnectListener listener) {
        this.broker = broker;
        this.name = name;
        this.port = port;
        this.listener = listener;
    }

    public QueueBrokerImpl getBroker() {
        return this.broker;
    }

    public String getName() {
        return this.name;
    }

    public int getPort() {
        return this.port;
    }

    public ConnectListener getListener() {
        return this.listener;
    }

    @Override
    public EventType getType() {
        return EventType.CONNECT;
    }
}
