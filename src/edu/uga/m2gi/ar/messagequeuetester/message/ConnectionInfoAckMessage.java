package edu.uga.m2gi.ar.messagequeuetester.message;

public class ConnectionInfoAckMessage extends Message {
    public ConnectionInfoAckMessage(Integer senderId) {
        super(senderId);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.CONNECTION_INFO_ACK;
    }
}
