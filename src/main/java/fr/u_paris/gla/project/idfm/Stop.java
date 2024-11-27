package fr.u_paris.gla.project.idfm;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A representation of a stop with its connected stop and which bifurcation
 * is needed to go to each connected stop
 */
public class Stop {

    Map<String, BifStop> connected = new HashMap<>();
    public String name;

    public Stop(String name){
        this.name = name;
    }

    /**
     * Checks is stopName is connected to this one
     * @param stopName
     * @return True if stopName is connected to the current stop
     */
    public boolean isStopConnected(String stopName) {
        return connected.containsKey(stopName);
    }

    /**
     * Add Connected stop
     * @param stop connected stop with the bifurcation needed
     */
    public void addConnectedStop(BifStop stop) {
        connected.put(stop.stop.name, stop);
    }

    /**
     * Return the connected stop with the name : stopName
     * @param stopName
     * @return the connected stop with the name : stopName
     */
    public BifStop getConnectedStop(String stopName) {
        return connected.get(stopName);
    }

}

