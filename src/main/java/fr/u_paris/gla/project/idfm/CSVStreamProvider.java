/**
 *
 */
package fr.u_paris.gla.project.idfm;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import fr.u_paris.gla.project.io.NetworkFormat;
import fr.u_paris.gla.project.utils.GPS;

/**
 * CSV Stream Provider class
 */
public final class CSVStreamProvider {

    private static final HashMap<String, Double> two_acceleration_distance_by_type = new HashMap<String, Double>(){{
        put("Bus", 0.1);
        put("Funicular", 0.1);
        put("Tram", 0.1);
        put("Rail", 0.2);
        put("Subway", 0.1);
    }};

    private static final HashMap<String, Double> max_speed_by_type = new HashMap<String, Double>(){{
        put("Bus", 10.0);
        put("Funicular", 5.0);
        put("Tram", 20.0);
        put("Rail", 50.0);
        put("Subway", 30.0);
    }};

    /**
     * Formatter from numbers into GPS Coordinates
     */
    private static final NumberFormat GPS_FORMATTER            = NetworkFormat
            .getGPSFormatter();
    /**
     * Formatter from numbers into MM:SS
     */
    private static final NumberFormat MINUTES_SECOND_FORMATTER = NumberFormat
            .getInstance(Locale.ENGLISH);
    static {
        MINUTES_SECOND_FORMATTER.setMinimumIntegerDigits(2);
    }
    /** Number of seconds in a minute. */
    private static final int  SECONDS_IN_MINUTES = 60;
    /**
     * Number of seconds in an hour
     */
    private static final long SECONDS_IN_HOURS   = 3_600;
    // Magically chosen values
    /** Maximal speed in km/h */
    private static final double MAX_SPEED             = 5;
    /** Distance to reach maximal speed in km */
    private static final double TWO_ACCELERATION_DISTANCE = 0.2;

    /**
     * Current CSV Line
     */
    private final String[] line = new String[NetworkFormat.NUMBER_COLUMNS];

    /**
     * Current CSV transport line iterator
     */
    private final Iterator<TraceEntry>      currentTrace;
    /**
     * Current Stop path iterator
     */
    private Iterator<List<StopEntry>> currentPath         = Collections.emptyIterator();
    /**
     * current iterator for the begin of the line
     */
    private Iterator<StopEntry>       currentSegmentStart = Collections.emptyIterator();
    /**
     * current iterator for the end of the line
     */
    private Iterator<StopEntry>       currentSegmentEnd   = Collections.emptyIterator();
    /**
     * HashMap of the current line's segments
     */
    Map<StopEntry, Set<StopEntry>>    lineSegments        = new HashMap<>();
    // The transport id with its value
    private final Map<String, Transport> transports;
    List <TraceDescription> descriptions = new ArrayList<>();


    /**
     * current begin of line
     */
    private StopEntry start = null;
    /**
     * current end of line
     */
    private StopEntry end   = null;

    /**
     * csv stream iterator checker
     */
    private boolean hasNext = false;
    /**
     * tells if we're already on the next
     */
    private boolean onNext  = false;

    private String traceId = "";
    private String traceType = "";
    private String url_image = "";

    /** Create the stream provider
     *  @param traces an iterator of the possible traces
     * @param t map of transports */
    public CSVStreamProvider(Iterator<TraceEntry> traces, Map<String, Transport> t) {
        this.currentTrace = traces;
        transports = t;
    }

    /** Method that tells if we have segments or paths to go through
     * @return if there are next elements or not
     */
    public boolean hasNext() {
        if (!this.onNext) {
            skipToNext();
        }
        return this.hasNext;
    }

    /**
     * Skip to either the next segment or the next path
     */
    private void skipToNext() {
        if (this.onNext) {
            return;
        }
        while (!this.onNext) {
            if (!this.currentSegmentEnd.hasNext()) {
                skipToNextCandidatePath();
            }
            if (this.onNext) {
                return;
            }
            skipToNextNewSegment();
        }
    }

    /**
     * Skips to the next segment
     */
    private void skipToNextNewSegment() {
        do {
            this.start = this.currentSegmentStart.next();
            this.lineSegments.putIfAbsent(this.start, new HashSet<>());
            this.end = this.currentSegmentEnd.next();
        } while (this.lineSegments.get(this.start).contains(this.end)
                && this.currentSegmentEnd.hasNext());
        if (!this.lineSegments.get(this.start).contains(this.end)) {
            this.lineSegments.get(this.start).add(this.end);
            this.onNext = true;
            this.hasNext = true;
        }
    }

    /** Move the reading head of path to the next one that has at least two
     * elements */
    private void skipToNextCandidatePath() {
        currentSegmentStart = null;
        do {
            while (!this.currentPath.hasNext()) {
                if (!this.currentTrace.hasNext()) {
                    this.hasNext = false;
                    this.onNext = true;
                    return;
                }
                TraceEntry trace = this.currentTrace.next();

                this.traceId = trace.id;
                this.traceType = trace.type;
                this.url_image = trace.url;
                this.descriptions.clear();
                this.descriptions.addAll(trace.descriptions);

                this.currentPath = trace.getPaths().iterator();
                this.line[NetworkFormat.LINE_INDEX] = trace.lname;
                this.lineSegments.clear();
            }
            List<StopEntry> path = this.currentPath.next();
            this.currentSegmentEnd = path.iterator();
            if (this.currentSegmentEnd.hasNext()) {
                this.currentSegmentEnd.next();
                this.currentSegmentStart = path.iterator();
            }
        } while (currentSegmentStart == null);
    }

    /** Store current trace data as a String array
     * @return The newly generated line of text
     */
    public String[] next() {
        if (!this.onNext) {
            skipToNext();
        }
        this.onNext = false;

        fillStation(this.start, this.line, NetworkFormat.START_INDEX);
        fillStation(this.end, this.line, NetworkFormat.STOP_INDEX);
        double distance = GPS.distance(this.start.latitude, this.start.longitude,
                this.end.latitude, this.end.longitude);
        this.line[NetworkFormat.DISTANCE_INDEX] = NumberFormat.getInstance(Locale.ENGLISH)
                .format(distance);
        this.line[NetworkFormat.DURATION_INDEX] = formatTime(
                (long) Math.ceil(distanceToTime(distance,this.traceType) * SECONDS_IN_HOURS));
        int bifurcation = this.lineSegments.get(this.start).size() - 1;
        this.line[NetworkFormat.VARIANT_INDEX] = Integer
                .toString(bifurcation);
        fillTransports(bifurcation);
        return Arrays.copyOf(this.line, this.line.length);
        // return new String[][]{Arrays.copyOf(this.line, this.line.length)};

    }

    /** creates adds a station into the next line String
     * @param stop  the stop
     * @param nextLine the next line
     * @param index the stop index in the next line */
    private static void fillStation(StopEntry stop, String[] nextLine, int index) {
        nextLine[index] = stop.lname;
        nextLine[index + 1] = MessageFormat.format("{0}, {1}", //$NON-NLS-1$
                GPS_FORMATTER.format(stop.latitude),
                GPS_FORMATTER.format(stop.longitude));

    }

    /** turns a number into a formatted time string
     * @param time the time value
     * @return the time as a String */
    private static String formatTime(long time) {
        return MessageFormat.format("{0}:{1}", //$NON-NLS-1$
                MINUTES_SECOND_FORMATTER.format(time / SECONDS_IN_MINUTES), MINUTES_SECOND_FORMATTER.format(time % SECONDS_IN_MINUTES));
    }

    // /** A tool method to give a delay to go through a certain distance.
    //  * <p>
    //  * This is a model with an linear acceleration and deceleration periods and a
    //  * constant speed in between.
    //  *
    //  * @param distance the distance (in km)
    //  * @return the duration of the trip (in hours) */
    // private static double distanceToTime(double distance) {
    //     return Math.max(0, distance - TWO_ACCELERATION_DISTANCE) / MAX_SPEED
    //             + Math.pow(Math.min(distance, TWO_ACCELERATION_DISTANCE) / MAX_SPEED, 2);
    // }

    /** A tool method to give a delay to go through a certain distance.
     * <p>
     * This is a model with an linear acceleration and deceleration periods and a
     * constant speed in between.
     *
     * @param distance the distance (in km)
     * @return the duration of the trip (in hours) */
    private static double distanceToTime(double distance, String type) {
        Double max_speed = max_speed_by_type.get(type);
        Double two_acc_distance = two_acceleration_distance_by_type.get(type);
        return Math.max(0, distance - two_acc_distance) / max_speed
                + 2 * Math.sqrt(Math.min(distance, two_acc_distance) * two_acc_distance)/max_speed;
    }

    private void fillTransports(int bif) {
        if(transports != null){
            String nameTransport = this.line[NetworkFormat.LINE_INDEX];
            String start_p = this.line[NetworkFormat.START_INDEX];
            String end_p = this.line[NetworkFormat.STOP_INDEX];
            // String bifurcation = this.line[NetworkFormat.VARIANT_INDEX];

            Transport transp = null;
            if(!transports.containsKey(traceId)){
                transp = new Transport(nameTransport, traceType, url_image);
                transports.put(traceId, transp);
            }else{
                transp = transports.get(traceId);
            }
            transp.addStop(start_p, end_p, bif);
            if(transp.descriptions.isEmpty()){
                transp.addDescriptions(descriptions);
            }
        }

    }


}
