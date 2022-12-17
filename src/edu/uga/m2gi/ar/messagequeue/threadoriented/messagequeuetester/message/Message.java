package edu.uga.m2gi.ar.messagequeue.threadoriented.messagequeuetester.message;

import java.io.*;

public abstract class Message implements Serializable {
    private final Integer senderId;

    public Message(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getSenderId() {
        return this.senderId;
    }

    public abstract MessageType getMessageType();

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        return bos.toByteArray();
    }

    public static Message fromByteArray(byte[] bytes) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message;
        try {
            message = (Message) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return message;
    }
}
