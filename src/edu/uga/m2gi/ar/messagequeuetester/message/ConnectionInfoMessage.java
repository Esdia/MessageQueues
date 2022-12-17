package edu.uga.m2gi.ar.messagequeuetester.message;

import edu.uga.m2gi.ar.messagequeuetester.Constants;

public class ConnectionInfoMessage extends Message {
    private final Integer port;

    public ConnectionInfoMessage(Integer port) {
        super(Constants.SERVER_ID);
        this.port = port;
    }

    public Integer getPort() {
        return this.port;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.CONNECTION_INFO;
    }
}
