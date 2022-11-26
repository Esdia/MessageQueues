package edu.uga.m2gi.ar.channels;

import java.util.ArrayList;
import java.util.List;

public class Broker {
    private final String m_name;
    private final List<Integer> occupiedPorts = new ArrayList<>();

    public Broker(String name) {
        m_name = name;
    }

    public String name() {
        return m_name;
    }

    private RendezVous getRdv(String key) {
        RendezVous rdv = RendezVous.getInstance(key);

        try {
            if (rdv == null) {
                rdv = RendezVous.createInstance(key);
                synchronized (rdv) {
                    wait();
                }
            } else {
                synchronized (rdv) {
                    notify();
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return rdv;
    }

    void releasePort(int port) {
        this.occupiedPorts.remove(port);
    }

    public Channel accept(int port) {
        if (this.occupiedPorts.contains(port)) {
            throw new IllegalStateException("Port already in use");
        }

        this.occupiedPorts.add(port);
        String key = this.m_name + "_" + port;
        RendezVous rdv = this.getRdv(key);
        return new Channel(this, port, key, rdv.getAcceptIn(), rdv.getAcceptOut());
    }

    public Channel connect(String name, int port) {
        String key = name + "_" + port;
        RendezVous rdv = this.getRdv(key);
        return new Channel(key, rdv.getConnectIn(), rdv.getConnectOut());
    }
}
