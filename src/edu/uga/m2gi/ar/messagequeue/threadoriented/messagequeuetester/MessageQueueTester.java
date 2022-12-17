package edu.uga.m2gi.ar.messagequeue.threadoriented.messagequeuetester;

public class MessageQueueTester {
    public static void main(String[] args) {
        new Server().start();
        for (int i = 0; i < Constants.CLIENT_NUMBER; i++) {
            new Client(Utils.nextClientId()).start();
        }
    }
}
