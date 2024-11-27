/**
 * 
 */
package fr.u_paris.gla.project.io;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Locale;

/** Definition of the formats of files for this project.
 * 
 * @author Emmanuel Bigeon */
public final class NetworkFormat {
    /** The amount of columns in the CSV file */
    public static final int NUMBER_COLUMNS = 8;

    /** The amount of decimal places in the GPS' coordinates */
    public static final int GPS_PRECISION  = 18;

    /** The index of the line name in the network format */
    public static final int LINE_INDEX     = 0;
    /** The index of the variant number of the segment in the network format */
    public static final int VARIANT_INDEX  = 1;
    /** The index of the segment trip duration in the network format */
    public static final int DURATION_INDEX = 6;
    /** The index of the segment distance in the network format */
    public static final int DISTANCE_INDEX = 7;
    /** The index of the segment end stop name in the network format */
    public static final int STOP_INDEX     = 4;
    /** The index of the segment starting stop name in the network format */
    public static final int START_INDEX    = 2;

    private static final DateTimeFormatter DURATION_FORMATTER         = DateTimeFormatter
            .ofPattern("HH:mm:ss");
    private static final NumberFormat DURATION_INDIVIDUAL_FORMATTER = NumberFormat
            .getIntegerInstance(Locale.ENGLISH);
    static {
        DURATION_INDIVIDUAL_FORMATTER.setMinimumIntegerDigits(2);
    }
    private static final Temporal ZERO = LocalTime.parse("00:00:00");

    /** Hidden constructor for utility class */
    private NetworkFormat() {
        // Tool class
    }

    /** Convert a {@link java.lang.String} into a {@link java.time.Duration}
     * @param duration the {@link java.lang.String}
     * @return the {@link java.time.Duration} object
     */
    public static Duration parseDuration(String duration) {
        LocalTime time = LocalTime.parse("00:" + duration, DURATION_FORMATTER);
        return Duration.between(time, ZERO);
    }

    /** Format a {@link java.time.Duration} into a {@link java.lang.String}
     * @param duration an object of type {@link java.time.Duration}
     * @return a String that depicts the duration in format MM:SS
     */
    public static String formatDuration(Duration duration) {
        return DURATION_INDIVIDUAL_FORMATTER.format(duration.toMinutes()) + ":"
                + DURATION_INDIVIDUAL_FORMATTER.format(duration.toSecondsPart());
    }

    /** Get a formatter for the numbers in a GPS coordinate pair
     * 
     * @return the {@link java.text.NumberFormat} formatter for numbers in a GPS coordinate pair */
    public static NumberFormat getGPSFormatter() {
        NumberFormat instance = NumberFormat.getNumberInstance(Locale.ENGLISH);
        instance.setMaximumFractionDigits(GPS_PRECISION);
        return instance;
    }
}