package edu.uga.m2gi.ar.messagequeue.threadoriented.messagequeuetester.message;

public class DisconnectionMessage extends Message {
    public DisconnectionMessage(Integer senderId) {
        super(senderId);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DISCONNECTION;
    }
}
