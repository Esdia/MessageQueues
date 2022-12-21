package edu.uga.m2gi.ar.messagequeue.eventoriented;

public interface QueueBrokerInternal extends QueueBroker {
    MessageQueue acceptInternal(int port);
    MessageQueue connectInternal(String name, int port);
}
