package fr.u_paris.gla.project.idfm;

import fr.u_paris.gla.project.io.ScheduleFormat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.LocalDateTime;
// import java.time.format.ResolverStyle;
import java.text.NumberFormat;
import java.util.*;

public class CSVSchedulesProvider {
    private static final DateTimeFormatter HOUR_MINUTE_FORMATTER = ScheduleFormat.getTimeFormatter();

    private static final HashMap<String, int[]> timings = new HashMap<String, int[]>(){{
        put("Bus", new int[]{11, 8, 7});
        put("Funicular", new int[]{15, 25, 20});
        put("Tram", new int[]{6, 7, 8, 9});
        put("Rail", new int[]{10, 11,15,12,20});
        put("Subway", new int[]{4, 2, 6,3,3,4});
    }};

    

    // Time between 2 passages for the transports with a new type we don't know yet
    private static int DEFAULT_TIMING = 6;
    private static final NumberFormat MINUTES_SECOND_FORMATTER = NumberFormat
            .getInstance(Locale.ENGLISH);

    static {
        MINUTES_SECOND_FORMATTER.setMinimumIntegerDigits(2);
    }

    private final String[] line = new String[ScheduleFormat.NUMBER_COLUMNS];
    private final Iterator<Transport> currentTransport;
    private Iterator<TraceDescription> currentDescription = Collections.emptyIterator();

    private String current_tansport_type = "";
    private LocalDateTime currentHour = null;
    private LocalDateTime lastHour = null;




    /**
     * Create the stream provider
     */
    public CSVSchedulesProvider(Iterator<Transport> transports) {
        this.currentTransport = transports;
    }

    public boolean hasNext() {
        return currentTransport.hasNext() || currentDescription.hasNext();
    }

    private void skipToNext() {
        if(currentHour == null || lastHour == null){
            skipToNextTransport();
        }else if(currentHour.compareTo(lastHour) < 0){
            // System.out.println("**Skip: Le current hour est plus petit "+currentHour+"|||"+lastHour);
            addRandomMinutes();
        }else if (currentHour.compareTo(lastHour) >= 0) {
            // System.out.println("**Skip: Le current hour est plus grand "+currentHour+"|||"+lastHour);
            skipToNextDescription();
        }
        else if (!this.currentDescription.hasNext()) {
            skipToNextTransport();
        }
        
    }

    /**
     * Move to the nextDescription of a Transport line
     */
    private void skipToNextDescription() {
        if (this.currentDescription.hasNext()) {
            TraceDescription description = this.currentDescription.next();

            currentHour = convertIntoLocalDateTime(description.first);
            lastHour = convertIntoLocalDateTime(description.last);

            if(lastHour.compareTo(currentHour) <= 0){
                lastHour = lastHour.plusDays(1);
            }
            this.line[ScheduleFormat.TERMINUS_INDEX] = description.from;
            this.line[ScheduleFormat.TRIP_SEQUENCE_INDEX] = description.bifurcation.toString();
            this.line[ScheduleFormat.TIME_INDEX] = currentHour.format(HOUR_MINUTE_FORMATTER);

        }else{
            skipToNextTransport();
        }
    }


    /**
     * Move to the next Transport line
     */
    private void skipToNextTransport() {
        if (this.currentTransport.hasNext()) {
            Transport transport = this.currentTransport.next();
            this.line[ScheduleFormat.LINE_INDEX] = transport.name;

            current_tansport_type = transport.type;
            this.currentDescription = transport.descriptions.iterator();
            skipToNextDescription();
        }
    }

    public String[] next() {
        if (!hasNext()) {
            return null;
        }
        skipToNext();
        return Arrays.copyOf(this.line, this.line.length);

        // return new String[][]{Arrays.copyOf(this.line, this.line.length)};

    }

    /**
     * Add random minutes for the next passage of a transport.
     * The random minutes depends on the type of the transport
     */
    private void addRandomMinutes() {
        // System.out.println("** addM: AVANT: "+currentHour);
        currentHour = currentHour.plusMinutes(pickMinute(current_tansport_type));
        this.line[ScheduleFormat.TIME_INDEX] = currentHour.format(HOUR_MINUTE_FORMATTER);
        // System.out.println("** addM: APRES: "+currentHour);
        // debut ++;
        // if(debut == 7) throw new IllegalArgumentException();
    }

    /**
     * 
     * @param transportType the type of a transport
     * @return a random minute depending on the type of the transport
     */
    public static int pickMinute(String transportType) {
        if (!timings.containsKey(transportType)) {
            return DEFAULT_TIMING;
        }
        int[] temps = timings.get(transportType);
        Random random = new Random();
        int indexAleatoire = random.nextInt(temps.length);
        return temps[indexAleatoire];
    }

    /**
     * 
     * @param hourMinute hour and minute representation. Ex:  "14:03"
     * @return a datetime of today but using hourMinute
     */
    public static LocalDateTime convertIntoLocalDateTime(String hourMinute) {
        LocalDateTime aujourdHui = LocalDateTime.now();
        LocalTime time = LocalTime.parse(hourMinute, HOUR_MINUTE_FORMATTER);

        return aujourdHui.withHour(time.getHour()).withMinute(time.getMinute()).withSecond(0).withNano(0);
    }
}