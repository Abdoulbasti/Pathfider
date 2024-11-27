/**
 *
 */
package fr.u_paris.gla.project.idfm;

import fr.u_paris.gla.project.utils.CSVTools;
import fr.u_paris.gla.project.utils.GPS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.File;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;


/**
 * Code of an extractor for the data from IDF mobilite.
 *
 * @author Emmanuel Bigeon
 */
public class IDFMNetworkExtractor {

    /**
     * The logger for information on the process
     */
    private static final Logger LOGGER = Logger
            .getLogger(IDFMNetworkExtractor.class.getName());

    /**
     * the URL of the Trace CSV
     */
    // IDF mobilite API URLs
    private static final String TRACE_FILE_URL = "https://data.iledefrance-mobilites.fr/api/explore/v2.1/catalog/datasets/traces-des-lignes-de-transport-en-commun-idfm/exports/csv?lang=fr&timezone=Europe%2FBerlin&use_labels=true&delimiter=%3B";
    /**
     * The URL of the Stops CSV
     */
    private static final String STOPS_FILE_URL = "https://data.iledefrance-mobilites.fr/api/explore/v2.1/catalog/datasets/arrets-lignes/exports/csv?lang=fr&timezone=Europe%2FBerlin&use_labels=true&delimiter=%3B";

    private static final String TRACE_FILE_DOWNLOADED_NAME = "./trace_idfm.csv";
    private static final String STOPS_FILE_DOWNLOADED_NAME = "./arret_idfm.csv";
    /**
     * the index in the CSV of a Trace's ID
     */
    // IDF mobilite csv formats
    private static final int IDFM_TRACE_ID_INDEX = 0;
    /**
     * the index in the CSV of a Trace's Name
     */
    private static final int IDFM_TRACE_SNAME_INDEX = 1;
    /**
     * the index in the CSV of a Trace's shape
     */
    private static final int IDFM_TRACE_SHAPE_INDEX = 6;
    private static final int IDFM_TRACE_TYPE_INDEX = 3;

    /**
     * The index in the CSV of the Stops' id
     */
    private static final int IDFM_STOPS_RID_INDEX = 0;
    /**
     * The index in the CSV of the Stops' schedules
     */
    private static final int IDFM_STOPS_SCHEDULES_INDEX = 3;
    /**
     * The index in the CSV of the Stops' names
     */
    private static final int IDFM_STOPS_NAME_INDEX = 5;
    /**
     * The index in the CSV of the Stops' longitude
     */
    private static final int IDFM_STOPS_LON_INDEX = 6;
    /**
     * The index in the CSV of the Stops' latitude
     */
    private static final int IDFM_STOPS_LAT_INDEX = 7;
    private static final int IDFM_URL_INDEX  = 10;

    private static final String TRACE_FILE_NAME = "trace.csv";

    private static final String HOURS_FILE_NAME = "hours.csv";

    public static final String IMAGES_FILE_NAME = "images.csv";

    // Magically chosen values
    /**
     * A number of stops on each line
     */
    private static final int GUESS_STOPS_BY_LINE = 5;

    /**
     * The quarter of a kilometer as a static value
     */
    // Well named constants
    private static final double QUARTER_KILOMETER = .25;

    public static boolean checkFileExistence(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            LOGGER.log(Level.INFO, filePath+ " already exists.");
            return true;
        } else {
            LOGGER.log(Level.INFO, filePath + " does not exist.");
            return false;
        }
    }

    public static void buildFiles() {
        if (checkFileExistence("./"+HOURS_FILE_NAME) && checkFileExistence("./"+TRACE_FILE_NAME) && checkFileExistence(("./"+IMAGES_FILE_NAME))) {
            LOGGER.log(Level.INFO, "Files already exists.");
            return;
        }

        Map<String, TraceEntry> traces = new HashMap<>();
        try {
            CSVTools.readCSVFromFile(TRACE_FILE_DOWNLOADED_NAME,
                    (String[] line) -> addLine(line, traces));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while reading the line paths", e);
        }

        List<StopEntry> stops = new ArrayList<>(traces.size() * GUESS_STOPS_BY_LINE);
        try {
            CSVTools.readCSVFromFile(STOPS_FILE_DOWNLOADED_NAME,
                    (String[] line) -> addStop(line, traces, stops));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while reading the stops", e);
        }

        cleanTraces(traces);

        Map<String, Transport> transports = new HashMap<>();
        CSVStreamProvider provider = new CSVStreamProvider(traces.values().iterator(), transports);

        // Write into args[0]
        try {
            CSVTools.writeCSVToFile(TRACE_FILE_NAME, Stream.iterate(provider.next(),
                    t -> provider.hasNext(), t -> provider.next()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e,
                    () -> MessageFormat.format("Could not write in file {0}", TRACE_FILE_NAME));
        }

        System.out.println("******************Building bifurcations ************************");
        long startTime = System.currentTimeMillis();

        for (Transport entry : transports.values()) {
            entry.buildBifurcationOptimized();
        }

        long endTime = System.currentTimeMillis();
        long tempsPasse = endTime - startTime;

        long minutes = (tempsPasse / 1000) / 60;
        long seconds = (tempsPasse / 1000) % 60;
        long milliseconds = tempsPasse % 1000;

        System.out.println("Temps écoulé : " + minutes + " minutes, " + seconds + " secndes et " + milliseconds + " millis");

        System.out.println("******************Fin Building bifurcations ************************");

        CSVSchedulesProvider providerschedules = new CSVSchedulesProvider(transports.values().iterator());
        try {
            CSVTools.writeCSVToFile(HOURS_FILE_NAME, Stream.iterate(providerschedules.next(),
                    t -> providerschedules.hasNext(), t -> providerschedules.next()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e,
                    () -> MessageFormat.format("Could not write in file {0}", HOURS_FILE_NAME));
        }

        CSVImageProvider providerimage = new CSVImageProvider(transports.values().iterator());
        try {
            CSVTools.writeCSVToFile(IMAGES_FILE_NAME, Stream.iterate(providerimage.next(),
                t -> providerimage.hasNext(), t -> providerimage.next()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e,
                () -> MessageFormat.format("Could not write in file {0}", IMAGES_FILE_NAME));
        }
    }


    /** Clean the traces/remove the unresolved lines
     * @param traces  the traces to clean
     */
    private static void cleanTraces(Map<String, TraceEntry> traces) {
        Set<String> toRemove = new HashSet<>();
        for (Entry<String, TraceEntry> traceEntry : traces.entrySet()) {
            TraceEntry trace = traceEntry.getValue();
            if (!cleanLine(trace.getPaths())) {
                LOGGER.severe(() -> MessageFormat.format(
                        "Missing stop for line {0}. Line will be removed", trace.lname));
                toRemove.add(traceEntry.getKey());
            }
        }
        for (String string : toRemove) {
            traces.remove(string);
        }
    }

    /** Tells if the current trasport line has all its stops entries resolved
     * @param stops the stops list
     * @return if the line is "clean"*/
    private static boolean cleanLine(List<List<StopEntry>> stops) {
        for (List<StopEntry> path : stops) {
            for (int i = 0; i < path.size(); i++) {
                StopEntry stop = path.get(i);
                if (!(stop instanceof UnidentifiedStopEntry unidentified)) {
                    continue;
                }
                StopEntry stopResolution = unidentified.resolve();
                if (stopResolution == null) {
                    return false;
                }
                path.set(i, stopResolution);
            }
        }
        return true;
    }

    /** adds a stop to all related variables
     * @param line  the transport line involved with the new stop
     * @param traces the traces related to it
     * @param stops the general stops list
     */
    private static void addStop(String[] line, Map<String, TraceEntry> traces,
                                List<StopEntry> stops) {
        StopEntry entry = new StopEntry(line[IDFM_STOPS_NAME_INDEX],
                Double.parseDouble(line[IDFM_STOPS_LON_INDEX]),
                Double.parseDouble(line[IDFM_STOPS_LAT_INDEX]));
        String rid = line[IDFM_STOPS_RID_INDEX];

        //Add traces description if it's empty
        if (traces.containsKey(rid)) {
            TraceEntry tmp = traces.get(rid);
            if (tmp.isDescriptionEmpty()) {
                List<TraceDescription> descriptions = extractDescription(line[IDFM_STOPS_SCHEDULES_INDEX]);
                tmp.addDescriptions(descriptions);
            }
        }


        // Add terminus to the traces
        if (traces.containsKey(rid)) {
            extractTerminus(line[IDFM_STOPS_SCHEDULES_INDEX]).forEach(t -> traces.get(rid).addTerminus(t));
        }

        traces.computeIfPresent(rid,
                (String k, TraceEntry trace) -> addCandidate(trace, entry));
        stops.add(entry);
    }

    /** add a line to the related list of traces
     * @param line the line as a string
     * @param traces the traces
     */
    private static void addLine(String[] line, Map<String, TraceEntry> traces) {
        TraceEntry entry = new TraceEntry(line[IDFM_TRACE_SNAME_INDEX], line[IDFM_TRACE_ID_INDEX],line[IDFM_TRACE_TYPE_INDEX], line[IDFM_URL_INDEX]);
        List<List<StopEntry>> buildPaths = buildPaths(line[IDFM_TRACE_SHAPE_INDEX]);
        entry.getPaths().addAll(buildPaths);
        if (buildPaths.isEmpty()) {
            LOGGER.severe(() -> MessageFormat.format(
                    "Line {0} has no provided itinerary and was ignored", entry.lname));
        } else {
            traces.put(line[IDFM_TRACE_ID_INDEX], entry);
        }
    }

    /** add a new entry as a candidate to a trace
     * @param trace  the trace
     * @param entry  the entry
     * @return the trace in question
     */
    private static TraceEntry addCandidate(TraceEntry trace, StopEntry entry) {
        for (List<StopEntry> path : trace.getPaths()) {
            for (StopEntry stopEntry : path) {
                if (stopEntry instanceof UnidentifiedStopEntry unidentified
                        && GPS.distance(entry.latitude, entry.longitude,
                        stopEntry.latitude,
                        stopEntry.longitude) < QUARTER_KILOMETER) {
                    unidentified.addCandidate(entry);
                }
            }
        }
        return trace;
    }

    /** turn a JSON list of stops into a list of paths
     * @param pathsJSON the JSON String of all paths
     * @return the paths as a List of StopEntries
     */
    private static List<List<StopEntry>> buildPaths(String pathsJSON) {
        List<List<StopEntry>> all = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(pathsJSON);
            JSONArray paths = json.getJSONArray("coordinates");
            for (int i = 0; i < paths.length(); i++) {
                JSONArray path = paths.getJSONArray(i);
                List<StopEntry> stopsPath = new ArrayList<>();
                for (int j = 0; j < path.length(); j++) {
                    JSONArray coordinates = path.getJSONArray(j);

                    StopEntry entry = new UnidentifiedStopEntry(coordinates.getDouble(0),
                            coordinates.getDouble(1));

                    stopsPath.add(entry);
                }

                all.add(stopsPath);
            }
        } catch (JSONException e) {
            // Ignoring invalid element!
            LOGGER.log(Level.FINE, e,
                    () -> MessageFormat.format("Invalid json element {0}", pathsJSON)); //$NON-NLS-1$
        }
        return all;
    }

    /** extract the terminus out of a JSON file
     * @param JSON the JSON
     * @return a list of strings related to the terminus
     */
    private static List<String> extractTerminus(String JSON) {
        List<String> all = new ArrayList<>();
        try {
            JSONArray schedules = new JSONArray(JSON);
            for (int i = 0; i < schedules.length(); i++) {
                JSONObject stop = schedules.getJSONObject(i);
                String terminus = stop.getString("from");
                all.add(terminus);
            }
        } catch (

                JSONException e) {
            // Ignoring invalid element!
            LOGGER.log(Level.FINE, e,
                    () -> MessageFormat.format("Invalid json element {0}", JSON)); //$NON-NLS-1$
        }

        return all;
    }

    private static List<TraceDescription> extractDescription(String JSON) {
        List<TraceDescription> all = new ArrayList<>();
        try {
            JSONArray schedules = new JSONArray(JSON);
            for (int i = 0; i < schedules.length(); i++) {
                JSONObject stop = schedules.getJSONObject(i);
                String from = stop.getString("from");
                String to = stop.getString("to");
                String first = stop.getString("first");
                String last = stop.getString("last");
                //We skip the lines where from equals to
                // if(from.compareTo(to) != 0){
                all.add(new TraceDescription(from, to, first, last));
                // }
            }
        } catch (JSONException e) {
            // Ignoring invalid element!
            // e.printStackTrace();
        }

        return all;
    }
}
