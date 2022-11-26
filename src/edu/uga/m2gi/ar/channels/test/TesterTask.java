package edu.uga.m2gi.ar.channels.test;

import edu.uga.m2gi.ar.channels.Broker;
import edu.uga.m2gi.ar.channels.Channel;
import edu.uga.m2gi.ar.channels.Task;

public abstract class TesterTask extends Task {
    private final byte[] toWrite;
    private final byte[] toRead;
    protected Channel channel;

    public TesterTask(Broker broker, byte[] toWrite, byte[] toRead) {
        super(broker);
        this.toWrite = toWrite;
        this.toRead = toRead;
    }

    @Override
    public void run() {
        int nRemainingToWrite = this.toWrite.length;
        int nWritten = 0;
        while (nRemainingToWrite != 0) {
            nWritten += this.channel.write(this.toWrite, nWritten, nRemainingToWrite);
            nRemainingToWrite = this.toWrite.length - nWritten;
        }

        int nRemainingToRead = this.toRead.length;
        byte[] read = new byte[nRemainingToRead];
        int nRead = 0;
        while (nRemainingToRead != 0) {
            nRead += this.channel.read(read, nRead, nRemainingToRead);
            nRemainingToRead = this.toRead.length - nRead;
        }

        if (this.equals(read, this.toRead)) {
            System.out.println(this.m_broker.getName() + " : OK");
        } else {
            System.out.println(this.m_broker.getName() + " : KO");
        }
    }

    private boolean equals(byte[] array1, byte[] array2) {
        if (array1.length != array2.length) {
            return false;
        }

        for (int i = 0; i < array1.length; i++) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }

        return true;
    }
}
