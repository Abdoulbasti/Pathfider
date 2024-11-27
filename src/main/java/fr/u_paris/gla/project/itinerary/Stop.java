package fr.u_paris.gla.project.itinerary;

import fr.u_paris.gla.project.utils.GPS;

import java.util.HashSet;
import java.util.Set;

/**
 * A representation of a stop used as a node
 * for the path-finding algorithm.
 */
public class Stop {
    // The total number of stops
    private static int counter = 0;

    private final int id;

    // The set of all the lines the stop is on
    private final Set<String> lines;

    private final String name;

    private final double latitude;

    private final double longitude;

    private double f;

    //Maximal speed in m/s
    private final double MAX_SPEED = 14.;

    /**
     * @param line the line passing through the stop
     * @param name the name of the stop
     * @param latitude the latitude of the stop in decimal degrees (DD)
     * @param longitude the longitude of the stop in DD
     */
    public Stop(String line, String name, double latitude, double longitude) {
        lines = new HashSet<>();
        lines.add(line);
        this.id = counter++;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.f = 0;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId(){
        return id;
    }

    /**
     * Computes the heuristic cost of the node relative to the goal node
     * @param goalNode the node we're trying to reach
     * @return the heuristic cost
     */
    public double getHeuristicCost(Stop goalNode) {
        double distance = GPS.distance(this.latitude, this.longitude, goalNode.latitude, goalNode.longitude);
        return distance/MAX_SPEED;
    }

    public Set<Stop> getNeighbors() {
        return null;
    }

    public double getCost(Stop neighbor) {
        return 0;
    }

    public double getF() {
        return f;
    }

    public void setF(double value) {
        this.f = value;
    }

    /**
     * Returns the name of the stop
     * @return the name of the stop
     */
    public String getName(){
        return name;
    }

    /**
     * Returns latitude of the stop
     * @return stop latitude in DD
     */
    public double getLatitude(){
        return latitude;
    }

    /**
     * Returns longitude of the stop
     * @return stop longitude in DD
     */
    public double getLongitude(){
        return longitude;
    }

    /**
     * Add a transport line to the stop
     * @param s the line to add
     */
    public void addLine(String s){
        lines.add(s);
    }

    /**
     * Returns the lines
     * @return all transport lines passing through this stop.
     */
    public Set<String> getLines() { return this.lines; }
}
