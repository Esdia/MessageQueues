package edu.uga.m2gi.ar.channels.test;

import edu.uga.m2gi.ar.channels.Broker;

public class TaskB extends TesterTask {
    public TaskB(Broker broker, byte[] toWrite, byte[] toRead) {
        super(broker, toWrite, toRead);
    }

    @Override
    public void run() {
        this.channel = this.m_broker.connect("A", 1000);
        super.run();
    }
}
