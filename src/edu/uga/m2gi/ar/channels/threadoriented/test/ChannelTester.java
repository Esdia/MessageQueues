package edu.uga.m2gi.ar.channels.threadoriented.test;

import edu.uga.m2gi.ar.channels.threadoriented.Broker;

import java.util.Random;

public class ChannelTester {
    private final static byte[] arrayA = new byte[100];
    private final static byte[] arrayB = new byte[100];

    private static void fillArrays() {
        Random random = new Random();
        random.nextBytes(arrayA);
        random.nextBytes(arrayB);
    }

    public static void main(String[] args) throws InterruptedException {
        fillArrays();

        Broker brokerA = new Broker("A");
        Broker brokerB = new Broker("B");

        TaskA taskA = new TaskA(brokerA, arrayA, arrayB);
        TaskB taskB = new TaskB(brokerB, arrayB, arrayA);

        taskA.start();
        taskB.start();

        taskA.join();
        taskB.join();
    }
}
