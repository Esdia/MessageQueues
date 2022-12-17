package edu.uga.m2gi.ar.channels.threadoriented.test;

import edu.uga.m2gi.ar.channels.threadoriented.Broker;

public class TaskA extends TesterTask {
    public TaskA(Broker broker, byte[] toWrite, byte[] toRead) {
        super(broker, toWrite, toRead);
    }

    @Override
    public void run() {
        this.channel = this.m_broker.accept(1000);
        super.run();

        this.channel.disconnect();
        System.out.println(this.m_broker.getName() + " disconnected");
    }
}
