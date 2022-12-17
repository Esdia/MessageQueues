package edu.uga.m2gi.ar.messagequeuetester.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NormalMessage extends Message {
    private final Integer sequenceNumber;
    private byte[] bytes;
    private transient ByteArrayOutputStream bos;

    public NormalMessage(Integer senderId, Integer sequenceNumber) {
        super(senderId);
        this.sequenceNumber = sequenceNumber;
        bos = new ByteArrayOutputStream();
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DEFAULT;
    }

    public Integer getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void putByes(byte[] bytes) {
        try {
            this.bos.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getBytes() {
        return this.bos.toByteArray();
    }

    @Override
    public byte[] toByteArray() throws IOException {
        this.bytes = this.bos.toByteArray();
        return super.toByteArray();
    }

    public static NormalMessage fromMessage(Message message) {
        try {
            NormalMessage normalMessage = (NormalMessage) message;
            normalMessage.bos = new ByteArrayOutputStream();
            normalMessage.bos.write(normalMessage.bytes);
            return normalMessage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
