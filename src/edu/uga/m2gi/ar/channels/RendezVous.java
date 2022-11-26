package edu.uga.m2gi.ar.channels;

import edu.uga.m2gi.ar.circularbuffer.CircularBuffer;

import java.util.HashMap;
import java.util.Map;

class RendezVous {
    private static final Map<String, RendezVous> objects = new HashMap<>();
    private final CircularBuffer acceptIn;
    private final CircularBuffer acceptOut;
    private final CircularBuffer connectIn;
    private final CircularBuffer connectOut;

    static RendezVous getInstance(String key) {
        return objects.get(key);
    }

    static RendezVous createInstance(String key) {
        RendezVous rdv = new RendezVous();
        objects.put(key, rdv);
        return rdv;
    }

    static boolean instanceExists(String key) {
        return objects.containsKey(key);
    }

    static void deleteInstance(String key) {
        objects.remove(key);
    }

    RendezVous() {
        this.acceptIn = new CircularBuffer(1000);
        this.acceptOut = new CircularBuffer(1000);
        this.connectIn = this.acceptOut;
        this.connectOut = this.acceptIn;
    }

    public CircularBuffer getAcceptIn() {
        return acceptIn;
    }

    public CircularBuffer getAcceptOut() {
        return acceptOut;
    }

    public CircularBuffer getConnectIn() {
        return connectIn;
    }

    public CircularBuffer getConnectOut() {
        return connectOut;
    }

    public static RendezVous getRdv(String key) {
        RendezVous rdv;
        boolean exists;
        synchronized (objects) {
            exists = RendezVous.instanceExists(key);
            if (!exists) {
                rdv = RendezVous.createInstance(key);
            } else {
                rdv = RendezVous.getInstance(key);
            }
        }

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (rdv) {
            try {
                if (exists) {
                    rdv.notify();
                } else {
                    rdv.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        return rdv;
    }
}
