package fr.u_paris.gla.project.itinerary;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A representation of a connection to another stop.
 * Corresponds to a graph edge for the algorithm.
 */
public class Connection{
    // Destination of the connection between the two stops
    private final Stop stop;

    // The line used for this connection
    private final String lineName;

    //Distance between the two stops
    private final double distance;

    //Travel time between the two stops
    private final int time;

    private final ArrayList<Integer> schedules;

    private final int bifurcation;

    /**
     * @param stop the stop where the connection is going.
     * @param lineName the name of the line used by the connection
     * @param distance the distance of the connection in km
     * @param time the travel time in s
     * @param bifurcation the bifurcation used
     */
    public Connection(Stop stop, String lineName, double distance, int time, int bifurcation){
        this.stop = stop;
        this.lineName=lineName;
        this.distance = distance;
        this.time = time;
        this.schedules = new ArrayList<>();
        this.bifurcation = bifurcation;
    }

    /**
     * @param stop the stop where the connection is going.
     * @param lineName the name of the line used by the connection
     * @param distance the distance of the connection in km
     * @param time the travel time in s
     */
    public Connection(Stop stop, String lineName, double distance, int time){
        this(stop, lineName, distance, time, 0);
    }


    /**
     * Returns the line name of the connection
     * @return the line name of the connection
     */
    public String getLineName() {
        return lineName;
    }


    /**
     * Returns the distance between the two connection stops.
     * @return distance in km
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Returns the travel time between the two stops.
     * @return time in s
     */
    public int getTime() {
        return time;
    }

    /**
     * Returns the stop to which the connection is going.
     * @return the destination stop
     */
    public Stop getStop() {
        return stop;
    }

    /**
     * Adds a schedule for the connection.
     * @param hours passage time in s from 00:00
     */
    public void addSchedule(int hours) {
        this.schedules.add(hours);
    }

    /**
     * Sort schedules.
     * Necessary to get the right passage time.
     */
    public void sortSchedule() {
        Collections.sort(this.schedules);
    }

    /**
     * Return to the schedule list
     * @return the schedule list
     */
    public ArrayList<Integer> getSchedules() {
        return this.schedules;
    }

    /**
     * Returns the number of bifurcation of the connection
     * @return the bifurcation
     */
    public int getBifurcation() {
        return this.bifurcation;
    }

    public double getCost() {
        return this.time;
    }

    /**
     * Returns the time of the next passage.
     * @param currentTime the current time
     * @return the time of the next passage
     */
    public double getNextTime(double currentTime) {
        if(this.schedules.size() == 0) {
            return currentTime;
        }

        int i = 0;
        while(i < this.schedules.size() && this.schedules.get(i) < currentTime) {
            i++;
        }
        if(i < this.schedules.size()) {
            return this.schedules.get(i);
        }
        return this.schedules.get(0);
    }

    /**
     * Returns the time before you can reach the next stop with this connection.
     * Corresponds to the sum of time to next stop and travel time.
     * @param currentTime the current time
     * @return time to reach the next stop
     */
    public double getCost(double currentTime) {
        if(this.schedules.size() == 0) {
            if(this.lineName.equals("WALK") || this.lineName.equals("")) {
                return this.time;
            }
            return this.time + 900;
        }
        double nextTime = this.getNextTime(currentTime);
        if(nextTime < currentTime) { nextTime += 86400;}
        return nextTime - currentTime + this.time;
    }

    @Override
    public String toString() {
        return lineName;
    }
}
