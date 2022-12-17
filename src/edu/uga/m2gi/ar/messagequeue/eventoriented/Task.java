package edu.uga.m2gi.ar.messagequeue.eventoriented;

public abstract class Task extends Thread {
    protected final QueueBroker broker;

    public Task(QueueBroker broker) {
        super(broker.getName());
        this.broker = broker;
    }

    @Override
    public abstract void run();
}
