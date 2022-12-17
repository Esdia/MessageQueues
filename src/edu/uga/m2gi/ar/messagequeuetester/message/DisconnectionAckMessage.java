package edu.uga.m2gi.ar.messagequeuetester.message;

import edu.uga.m2gi.ar.messagequeuetester.Constants;

public class DisconnectionAckMessage extends Message {
    public DisconnectionAckMessage() {
        super(Constants.SERVER_ID);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DISCONNECTION_ACK;
    }
}
