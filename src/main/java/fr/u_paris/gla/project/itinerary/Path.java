package fr.u_paris.gla.project.itinerary;

/**
 * A representation of a path
 */
public class Path {
    private Stop current;

    private Stop next;

    private double startTime;

    private double travelTime;

    private double distance;

    private String line;

    private Connection connection;

    /**
     * @param current the start stop
     * @param connection the connection to the next stop
     * @param startTime departure time from node current
     */
    public Path(Stop current, Connection connection, double startTime) {
        this.current = current;
        this.connection = connection;
        this.next = connection.getStop();
        this.startTime = startTime;
        this.travelTime = connection.getTime();
        this.line = connection.getLineName();
    }

    /**
     * Returns the connection used by the path
     * @return the connection used
     */
    public Connection getConnection(){
        return this.connection;
    }

    /**
     * Returns the start stop
     * @return the start stop
     */
    public Stop getCurrentStop() {
        return this.current;
    }

    /**
     * Returns the next stop
     * @return the next stop
     */
    public Stop getNextStop() {
        return next;
    }

    /**
     * Returns stop start time
     * @return the time in s
     */
    public double getStartTime() {
        return this.startTime;
    }

    /**
     * Returns the travel time between the two stops.
     * @return the travel time in s
     */
    public double travelTime() {
        return this.travelTime;
    }

    /**
     * Returns the name of the line taken.
     * @return the name of the line
     */
    public String getLine() {
        return this.line;
    }
}
