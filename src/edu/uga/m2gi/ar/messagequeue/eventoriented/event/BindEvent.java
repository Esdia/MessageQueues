package edu.uga.m2gi.ar.messagequeue.eventoriented.event;

import edu.uga.m2gi.ar.messagequeue.eventoriented.QueueBrokerImpl;
import edu.uga.m2gi.ar.messagequeue.eventoriented.listener.AcceptListener;

public class BindEvent extends Event {
    private final QueueBrokerImpl broker;
    private final int port;
    private final AcceptListener acceptListener;

    public BindEvent(QueueBrokerImpl broker, int port, AcceptListener acceptListener) {
        this.broker = broker;
        this.port = port;
        this.acceptListener = acceptListener;
    }

    public QueueBrokerImpl getBroker() {
        return this.broker;
    }

    public int getPort() {
        return this.port;
    }

    public AcceptListener getAcceptListener() {
        return this.acceptListener;
    }

    @Override
    public EventType getType() {
        return EventType.BIND;
    }
}
