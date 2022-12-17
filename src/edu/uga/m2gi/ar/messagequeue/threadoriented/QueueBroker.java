package edu.uga.m2gi.ar.messagequeue.threadoriented;

import edu.uga.m2gi.ar.channels.threadoriented.Broker;
import edu.uga.m2gi.ar.channels.threadoriented.Channel;

public class QueueBroker {
    private final Broker broker;

    public QueueBroker(String name) {
        this.broker = new Broker(name);
    }

    public String getName() {
        return this.broker.getName();
    }

    public MessageQueue accept(int port) {
        Channel channel = this.broker.accept(port);
        return new MessageQueueImpl(channel);
    }

    public MessageQueue connect(String name, int port) {
        Channel channel = this.broker.connect(name, port);
        return new MessageQueueImpl(channel);
    }
}
