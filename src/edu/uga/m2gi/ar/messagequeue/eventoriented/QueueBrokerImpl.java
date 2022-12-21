package edu.uga.m2gi.ar.messagequeue.eventoriented;

import edu.uga.m2gi.ar.channels.threadoriented.Broker;
import edu.uga.m2gi.ar.channels.threadoriented.Channel;
import edu.uga.m2gi.ar.messagequeue.eventoriented.event.BindEvent;
import edu.uga.m2gi.ar.messagequeue.eventoriented.event.ConnectEvent;
import edu.uga.m2gi.ar.messagequeue.eventoriented.listener.AcceptListener;
import edu.uga.m2gi.ar.messagequeue.eventoriented.listener.ConnectListener;

public class QueueBrokerImpl implements QueueBroker, QueueBrokerInternal {
    private final Broker broker;

    public QueueBrokerImpl(String name) {
        this.broker = new Broker(name);
    }

    @Override
    public String getName() {
        return this.broker.getName();
    }

    @Override
    public boolean bind(int port, AcceptListener listener) {
        BindEvent event = new BindEvent(this, port, listener);
        Executor.getInstance().putEvent(event);
        return true;
    }

    @Override
    public boolean connect(String name, int port, ConnectListener listener) {
        ConnectEvent event = new ConnectEvent(this, name, port, listener);
        Executor.getInstance().putEvent(event);
        return true;
    }

    private String getCookieKey(String name, int port, String suffix) {
        return name + "_" + port + "_" + suffix;
    }

    private String getSelfCookieKey(String name, int port, boolean isAcceptSide) {
        return this.getCookieKey(name, port, isAcceptSide ? "accept" : "connect");
    }

    private String getOtherCookieKey(String name, int port, boolean isAcceptSide) {
        return this.getCookieKey(name, port, isAcceptSide ? "connect" : "accept");
    }

    @Override
    public MessageQueue acceptInternal(int port) {
        Channel channel = this.broker.accept(port);
        String selfCookieKey = this.getSelfCookieKey(this.getName(), port, true);
        String otherCookieKey = this.getOtherCookieKey(this.getName(), port, true);
        return new MessageQueueImpl(channel, selfCookieKey, otherCookieKey);
    }

    @Override
    public MessageQueue connectInternal(String name, int port) {
        Channel channel = this.broker.connect(name, port);
        String selfCookieKey = this.getSelfCookieKey(this.getName(), port, false);
        String otherCookieKey = this.getOtherCookieKey(this.getName(), port, false);
        return new MessageQueueImpl(channel, selfCookieKey, otherCookieKey);
    }
}
