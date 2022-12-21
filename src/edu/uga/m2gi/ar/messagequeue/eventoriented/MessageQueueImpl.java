package edu.uga.m2gi.ar.messagequeue.eventoriented;

import edu.uga.m2gi.ar.channels.threadoriented.Channel;
import edu.uga.m2gi.ar.messagequeue.eventoriented.event.CloseEvent;
import edu.uga.m2gi.ar.messagequeue.eventoriented.event.SendEvent;
import edu.uga.m2gi.ar.messagequeue.eventoriented.listener.MessageListener;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class MessageQueueImpl implements MessageQueue, MessageQueueInternal {
    private static final int HEADER_SIZE = Integer.BYTES;
    private static final ByteOrder BYTE_ORDER = ByteOrder.nativeOrder();

    private final Cookie cookie;
    private final Channel channel;
    private MessageListener listener;

    MessageQueueImpl(Channel channel, String selfKey, String otherSideKey) {
        this.channel = channel;
        this.cookie = new Cookie(selfKey, otherSideKey);
        Executor.getInstance().setMessageQueue(selfKey, this);
    }

    @Override
    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean send(byte[] bytes) {
        return this.send(bytes, 0, bytes.length);
    }

    @Override
    public boolean send(byte[] bytes, int offset, int length) {
        SendEvent event = new SendEvent(this, bytes, offset, length, this.cookie);
        Executor.getInstance().putEvent(event);
        return true;
    }

    @Override
    public synchronized void close() {
        CloseEvent event = new CloseEvent(cookie);
        Executor.getInstance().putEvent(event);
    }

    @Override
    public boolean closed() {
        return this.channel.disconnected();
    }

    @Override
    public MessageListener getListener() {
        return this.listener;
    }

    private void fullyWrite(Channel channel, byte[] bytes, int offset, int length) {
        int nRemainingToWrite = length;
        int nWritten = 0;
        while (nRemainingToWrite != 0) {
            nWritten += channel.write(bytes, offset + nWritten, nRemainingToWrite);
            nRemainingToWrite = length - nWritten;
        }
    }

    private byte[] fullyRead(Channel channel, int length) {
        byte[] read = new byte[length];
        int nRemainingToRead = length;
        int nRead = 0;
        while (nRemainingToRead != 0) {
            nRead += channel.read(read, nRead, nRemainingToRead);
            nRemainingToRead = length - nRead;
        }

        return read;
    }

    @Override
    public void sendInternal(byte[] bytes, int offset, int length) throws QueueClosedException {
        if (this.channel.disconnected()) {
            throw new QueueClosedException();
        }

        byte[] header = ByteBuffer.allocate(HEADER_SIZE).order(BYTE_ORDER).putInt(length).array();
        this.fullyWrite(this.channel, header, 0, HEADER_SIZE);
        this.fullyWrite(this.channel, bytes, offset, length);
    }

    @Override
    public byte[] receiveInternal() throws QueueClosedException {
        if (this.channel.disconnected()) {
            throw new QueueClosedException();
        }

        byte[] header = this.fullyRead(this.channel, HEADER_SIZE);
        int length = ByteBuffer.allocate(HEADER_SIZE).order(BYTE_ORDER).put(header).getInt(0);
        return this.fullyRead(this.channel, length);
    }

    @Override
    public void closeInternal() {
        this.channel.disconnect();
    }
}
