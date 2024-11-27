/**
 *
 */
package fr.u_paris.gla.project.io;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * A tool class for the schedule format.
 *
 * @author Emmanuel Bigeon
 */
public final class ScheduleFormat {
    /** The amount of columns in the CSV file */
    public static final int NUMBER_COLUMNS = 4;

    /** The index of the line name in the schedule format */
    public static final int LINE_INDEX = 0;
    /** The index of the trip sequence in the schedule format */
    public static final int TRIP_SEQUENCE_INDEX = 1;
    /** The index of the terminus name in the schedule format */
    public static final int TERMINUS_INDEX = 2;
    /** The index of the time in the schedule format */
    public static final int TIME_INDEX = 3;

    /** Hidden constructor for tool class */
    private ScheduleFormat() {
        // Tool class
    }

    /**
     * Read a trip sequence from its string representation
     *
     * @param representation the representation
     * @return the sequence of branching
     */
    public static List<Integer> getTripSequence(String representation) {

        List<Integer> l = new ArrayList<>();
        for(String s :  representation.split(","))
            l.add(Integer.parseInt(s));
        return l;
        }


    /** Create a {@link java.time.format.DateTimeFormatter} object used to format Dates
     * @return the formatter
     */
    public static DateTimeFormatter getTimeFormatter() {
        return DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.LENIENT);
    }
}
