package fr.u_paris.gla.project.idfm;

/**
 * A representation of a stop with the bifurcation that is needed. All stops
 * have a list of bifstop called connected. BifStop is just composed of a
 * connected stop and the bifurcation used to go from the first stop to the
 * connected one.
 */
public class BifStop {
    // The bifurcation
    public int bifurc;
    public Stop stop;

    public BifStop(int bif, Stop stop){
        bifurc = bif;
        this.stop = stop;
    }
}
