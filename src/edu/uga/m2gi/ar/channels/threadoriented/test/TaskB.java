package edu.uga.m2gi.ar.channels.threadoriented.test;

import edu.uga.m2gi.ar.channels.threadoriented.Broker;

public class TaskB extends TesterTask {
    public TaskB(Broker broker, byte[] toWrite, byte[] toRead) {
        super(broker, toWrite, toRead);
    }

    @Override
    public void run() {
        this.channel = this.m_broker.connect("A", 1000);
        super.run();

        try {
            // Give enough time to the other task to disconnect
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int n1 = this.channel.write(new byte[]{1}, 0, 1);
        int n2 = this.channel.read(new byte[1], 0, 1);

        if (n1 != -1) {
            System.out.println(this.m_broker.getName() + " write after disconnect : KO");
        }
        if (n2 != -1) {
            System.out.println(this.m_broker.getName() + " read after disconnect : KO");
        }
        if (n1 == -1 && n2 == -1) {
            System.out.println(this.m_broker.getName() + " read/write after disconnect : OK");
        }
    }
}
