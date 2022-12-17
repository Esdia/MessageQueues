package edu.uga.m2gi.ar.messagequeue.threadoriented.messagequeuetester;

import java.nio.ByteOrder;

public class Constants {
    /* CLIENT CONFIGURATION */
    public static final Integer STARTING_CLIENT_ID = 1;
    public static final Integer MESSAGE_SIZE = 5; // Size of the number sequences sent by clients
    public static final Integer SEQUENCE_SIZE = 496; // How many messages to send before disconnecting
    public static final Integer CLIENT_NUMBER = 3;

    /* SERVER CONSTANTS */
    public static final Integer SERVER_ID = 0;
    public static final String SERVER_NAME = Utils.idToName(SERVER_ID);
    public static final Integer SERVER_ENTRY_PORT = 1000;


    /* MISCELLANEOUS CONFIGURATION */
    public static final ByteOrder BYTE_ORDER = ByteOrder.nativeOrder();
}
