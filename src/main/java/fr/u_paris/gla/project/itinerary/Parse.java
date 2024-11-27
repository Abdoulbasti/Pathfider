package fr.u_paris.gla.project.itinerary;

import fr.u_paris.gla.project.idfm.*;
import fr.u_paris.gla.project.utils.CSVTools;
import fr.u_paris.gla.project.utils.GPS;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CSV file parser to generate the network graph
 */
public class Parse {
    private static final Logger LOGGER = Logger
            .getLogger(IDFMNetworkExtractor.class.getName());

    // IDF mobilite generated file
    private static final String TRACE_FILE_NAME = "./trace.csv";

    private static final String HOURS_FILE_NAME = "./hours.csv";

    // IDF mobilite file format

    private static final int IDFM_TRACE_ID_INDEX = 0;

    private static final int IDFM_TRACE_DERIV_INDEX = 1;

    private static final int IDFM_TRACE_FROM_INDEX = 2;

    private static final int IDFM_TRACE_FROM_GPS_INDEX = 3;

    private static final int IDFM_TRACE_TO_INDEX= 4;

    private static final int IDFM_TRACE_TO_GPS_INDEX = 5;

    private static final int IDFM_TRACE_TIME_INDEX = 6;

    private static final int IDFM_TRACE_DISTANCE_INDEX = 7;

    private static final double ERROR_MARGIN = 1.;

    //The time public vehicles spend at each stop in seconds.
    private static final int STOP_TIME = 30;

    //Walking speed in m/s
    public static final double WALK_SPEED = 1.;

    private HashSet<Stop> nodes = new HashSet<>();
    private HashMap<Stop, Set<Connection>> connections = new HashMap<>();
    private HashMap<String, ArrayList<Stop>> tmp = new HashMap<>();

    /**
     * Returns the coordinates from a String to a double array:
     * "49.08, 3.07" -> {49.08, 3.07}
     * @param gps the string representation
     * @return the double array
     */
    private static double[] getCoords(String gps) {
        String []stringCoords = gps.split(", ");
        return new double[] {Double.parseDouble(stringCoords[0]), Double.parseDouble(stringCoords[1])};
    }

    public HashMap<String, ArrayList<Stop>> getTmp(){
        return tmp;
    }
    /**
     * Searchs for a stop with the same name and GPS coordinates in the graph, and creates it if non existant
     * @param nodes a graph of the stops
     * @param tmp list of the created stops
     * @param name the name of the stop
     * @param gps the coordinate of the stop
     * @param lineId the line the stop is on
     * @return the Stop object
     */
    private static Stop getOrCreateStop(HashSet<Stop> nodes, HashMap<String, ArrayList<Stop>> tmp, String name, String gps, String lineId, HashMap<Stop, Set<Connection>> connections) {
        ArrayList<Stop> stopList = tmp.get(name);
        double[] coords = getCoords(gps);

        // First we search by name, and then compare the coordinates since multiple stations can have the same name. A margin of error is necessary since stops can have multiple GPS coordinates
        ArrayList<Stop> lineChanges = new ArrayList<>();
        if (stopList != null) {
            for(Stop stop : stopList) {

                double dist = GPS.distance(coords[0], coords[1], stop.getLatitude(), stop.getLongitude());
                if(dist == 0) {
                    stop.addLine(lineId);
                    return stop;
                }
                if(dist < ERROR_MARGIN) {
                    lineChanges.add(stop);
                }
            }
        }

        Stop newStop = new Stop(lineId, name, coords[0], coords[1]);
        nodes.add(newStop);
        stopList = stopList == null ? new ArrayList<>() : stopList;
        stopList.add(newStop);
        tmp.put(name, stopList);
        for(Stop s : lineChanges) {
            double dist = GPS.distance(coords[0], coords[1], s.getLatitude(), s.getLongitude());
            int time = (int) (dist*1000/WALK_SPEED);
            Connection c1 = new Connection(s, "WALK", dist, time);
            connections.computeIfAbsent(newStop, k -> new HashSet<>()).add(c1);

            Connection c2 = new Connection(newStop, "WALK", dist, time);
            connections.computeIfAbsent(s, k -> new HashSet<>()).add(c2);
        }
        return newStop;
    }

    /**
     * Adds into the graph the connection between two stops, parsed from a CSV line
     * @param line the current line we want to parse
     * @param nodes the graph of stops
     * @param tmp list of the created stops
     * @param connections
     */
    private static void addLine(String[] line, HashSet<Stop> nodes, HashMap<String, ArrayList<Stop>> tmp, HashMap<Stop, Set<Connection>> connections) {
        Stop fromStop = getOrCreateStop(nodes, tmp, line[IDFM_TRACE_FROM_INDEX], line[IDFM_TRACE_FROM_GPS_INDEX], line[IDFM_TRACE_ID_INDEX], connections);
        Stop toStop = getOrCreateStop(nodes, tmp, line[IDFM_TRACE_TO_INDEX], line[IDFM_TRACE_TO_GPS_INDEX], line[IDFM_TRACE_ID_INDEX], connections);

        String[] timeString = line[IDFM_TRACE_TIME_INDEX].split(":");
        String time0WithoutComma = timeString[0].replace(",", "");
        int time = Integer.parseInt(time0WithoutComma) * 60 + Integer.parseInt(timeString[1]);

        Connection connection = new Connection(toStop, line[IDFM_TRACE_ID_INDEX], Double.parseDouble(line[IDFM_TRACE_DISTANCE_INDEX]), time, Integer.parseInt(line[IDFM_TRACE_DERIV_INDEX]));

        connections.computeIfAbsent(fromStop, k -> new HashSet<>()).add(connection);
    }

    private static void addScheduleRec(Stop current, Stop previous, String line, ArrayList<Integer> bifurcations, int time, HashMap<String, ArrayList<Stop>> stopsHashSet, HashMap<Stop, Set<Connection>> connections, HashSet<Stop> processed){
        time = time%86400;
        //If the stop has already been processed, it is not reprocessed.
        if(processed.contains(current)) {return;}
        processed.add(current);

        Set<Connection> neighborhood = connections.get(current);
        if(neighborhood == null) {return;}


        ArrayList<Connection> directions = new ArrayList<>();
        for(Connection n : neighborhood) {
            if(n.getLineName().equals(line)
                    && (previous == null || !n.getStop().getName().equals(previous.getName()))
                    ) {
                directions.add(n);
            }
        }

        if(directions.size() == 0) {return;}

        Stop next_stop = null;
        if(directions.size() > 1) {
            int bifurcation = bifurcations.size() == 0 ? 0 : bifurcations.get(0);
            if(bifurcations.size() > 0) {bifurcations.remove(0);}
            for(Connection d : directions) {
                if(d.getBifurcation() == bifurcation) {
                    next_stop = d.getStop();
                    break;
                }
            }
            if(next_stop == null) {
                return;
            }
        }
        else {
            next_stop = directions.get(0).getStop();
            if(directions.get(0).getBifurcation() != 0) {
                if(bifurcations.size() > 0 && directions.get(0).getBifurcation() == bifurcations.get(0)){
                    bifurcations.remove(0);
                }
            }
        }

        for(Connection n : directions) {
            if(n.getStop() == next_stop) {
                n.addSchedule(time);
                time += n.getTime() + STOP_TIME;
                addScheduleRec(next_stop, current, line, bifurcations, time, stopsHashSet, connections, processed);
                return;
            }
        }
    }


    /**
     * Adds schedules to graph stops, parsed from a CSV line
     * @param input the current line we want to parse
     * @param stopsHashSet the map of stop names to their objects
     * @param connections the map of stops to their connections
     */
    private static void addSchedule(String[] input, HashMap<String, ArrayList<Stop>> stopsHashSet, HashMap<Stop, Set<Connection>> connections) {

        String line = input[0];

        ArrayList<Integer> bifurcations = new ArrayList<>();
        if(!input[1].equals("[]")) {
            String[] b = input[1].substring(1, input[1].length()-1).split(",");
            bifurcations = new ArrayList<>();
            for(String n : b){
                bifurcations.add(Integer.parseInt(n.trim()));
            }
        }

        String name = input[2];

        String[] timeString = input[3].split(":");
        int time = Integer.parseInt(timeString[0]) * 3600 + Integer.parseInt(timeString[1])*60;


        ArrayList<Stop> stops = stopsHashSet.get(name);
        if(stops == null) {return;}

        for(Stop stop : stops) {
            if(stop.getLines().contains(line)) {
                addScheduleRec(stop, null, line, bifurcations, time, stopsHashSet, connections, new HashSet<>());
            }
        }
    }

    /**
     * Parse CSV files to build the network graph
     */
    public void parseFiles(){
        IDFMNetworkExtractor.buildFiles();

        try {
            CSVTools.readCSVFromFile(TRACE_FILE_NAME,
                    (String[] line) -> addLine(line, nodes, tmp, connections));

            CSVTools.readCSVFromFile(HOURS_FILE_NAME,
                    (String[] line) -> addSchedule(line, tmp, connections));

            for(Set<Connection> set : connections.values()) {
                for(Connection c : set) {
                    c.sortSchedule();
                }
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while reading the line paths", e);
        }
    }

    /**
     * Returns network graph from parsed CSV files
     * @return the graphe of the network
     */
    public Graph createGraph() {
        return new Graph(nodes, connections);
    }

    public Finder createFinder(Graph graph) {
        return new Finder(graph);
    }

    public List<Path> getItinerary(Stop src, Stop dst, double startTime ){
        Graph graph = new Graph(nodes, connections);
        Finder finder = new Finder(graph);
        return finder.findPath(src, dst, startTime);
    }
}
