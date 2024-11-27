package fr.u_paris.gla.project.idfm;

import java.util.List;
import java.util.ArrayList;

/**
 * A representation of a transport description encompansing its first and last
 * stop in all of its direction, the first and last schedule and all the
 * bifurcation that direction takes. The description comes from the fourth column  * of the stop csv file.
 */
public class TraceDescription {
    public String from;
    public String to;
    public String first;
    public String last;
    List<Integer> bifurcation = new ArrayList<>();

    public TraceDescription(String from,String to, String first, String last){
        this.from = from;
        this.to = to;
        this.first = first;
        this.last = last;
    }

    @Override
    public String toString() {
        return "From: " + from + ", To: " + to + ", First: " + first + ", Last: " + last + ", Bifurcation: " + bifurcation;
    }
}
