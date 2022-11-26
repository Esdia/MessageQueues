package edu.uga.m2gi.ar.channels;

import java.util.ArrayList;
import java.util.List;

public class Broker {
    private final String m_name;
    private final List<Integer> occupiedPorts = new ArrayList<>();

    public Broker(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }


    void releasePort(int port) {
        // remove(port) would use port as an index to remove, not a value, hence the cast to Integer
        this.occupiedPorts.remove(Integer.valueOf(port));
    }

    public Channel accept(int port) {
        if (this.occupiedPorts.contains(port)) {
            throw new IllegalStateException("Port already in use");
        }

        this.occupiedPorts.add(port);
        String key = this.m_name + "_" + port;
        RendezVous rdv = RendezVous.getRdv(key);
        return new ChannelImpl(this, port, key, rdv.getAcceptIn(), rdv.getAcceptOut());
    }

    public Channel connect(String name, int port) {
        String key = name + "_" + port;
        RendezVous rdv = RendezVous.getRdv(key);
        return new ChannelImpl(key, rdv.getConnectIn(), rdv.getConnectOut());
    }
}
