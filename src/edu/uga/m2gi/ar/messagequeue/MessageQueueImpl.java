package edu.uga.m2gi.ar.messagequeue;

import edu.uga.m2gi.ar.channels.Channel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class MessageQueueImpl implements MessageQueue {
    private static final int HEADER_SIZE = Integer.BYTES;
    private static final ByteOrder BYTE_ORDER = ByteOrder.nativeOrder();

    private final Channel channel;

    MessageQueueImpl(Channel channel) {
        this.channel = channel;
    }

    private void fullyWrite(byte[] bytes, int offset, int length) {
        int nRemainingToWrite = length;
        int nWritten = 0;
        while (nRemainingToWrite != 0) {
            nWritten += this.channel.write(bytes, offset + nWritten, nRemainingToWrite);
            nRemainingToWrite = length - nWritten;
        }
    }

    private byte[] fullyRead(int length) {
        byte[] read = new byte[length];
        int nRemainingToRead = length;
        int nRead = 0;
        while (nRemainingToRead != 0) {
            nRead += this.channel.read(read, nRead, nRemainingToRead);
            nRemainingToRead = length - nRead;
        }

        return read;
    }

    @Override
    public void send(byte[] bytes, int offset, int length) throws QueueClosedException {
        if (this.closed()) {
            throw new QueueClosedException();
        }

        byte[] header = ByteBuffer.allocate(HEADER_SIZE).order(BYTE_ORDER).putInt(length).array();
        this.fullyWrite(header, 0, HEADER_SIZE);
        this.fullyWrite(bytes, offset, length);
    }

    @Override
    public byte[] receive() throws QueueClosedException {
        if (this.closed()) {
            throw new QueueClosedException();
        }

        byte[] header = this.fullyRead(HEADER_SIZE);
        int length = ByteBuffer.allocate(HEADER_SIZE).order(BYTE_ORDER).put(header).getInt(0);
        return this.fullyRead(length);
    }

    @Override
    public synchronized void close() {
        this.channel.disconnect();
    }

    @Override
    public boolean closed() {
        return this.channel.disconnected();
    }
}
