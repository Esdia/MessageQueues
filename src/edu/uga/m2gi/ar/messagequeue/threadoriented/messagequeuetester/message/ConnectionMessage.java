package edu.uga.m2gi.ar.messagequeue.threadoriented.messagequeuetester.message;

public class ConnectionMessage extends Message {
    public ConnectionMessage(Integer senderId) {
        super(senderId);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.CONNECTION;
    }
}
