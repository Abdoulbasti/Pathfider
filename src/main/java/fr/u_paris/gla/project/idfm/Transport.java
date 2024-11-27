package fr.u_paris.gla.project.idfm;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.AbstractMap.SimpleEntry;

/**
 * Representation of a line with its description and stops
 */
public class Transport {
    //All the stops of the line
    Map<String, Stop> stopsMap = new HashMap<>();
    public String name;
    public String type;
    public String image_url;

    //All the line descriptions (directions and schedules)
    List <TraceDescription> descriptions = new ArrayList<>();


    public Transport(String name,String type, String url){
        this.name = name;
        this.type = type;
        this.image_url = url;
    }


    /**
     * Build the bifurcation for all the descriptions
     */
    public void buildBifurcation(){
        // int found = 0;
        for(TraceDescription d : descriptions){
            // System.out.println("Debut est "+d.first);

            Stop debut = stopsMap.get(d.from);
            Stop fin = stopsMap.get(d.to);
            if (debut != null && fin != null) {
                SimpleEntry<Boolean, List<Integer>> sol = roadToLast(debut.name, fin.name, new ArrayList<String>(), new ArrayList<Integer>());
                if (sol.getKey()) {
                    // found++;
                    d.bifurcation = sol.getValue();
                }
            }
        }
        // System.out.println("J'en ai trouvé "+found);
    }


    /**
     * Build the bifurcation for all the descriptions but optimized 
     */
    public void buildBifurcationOptimized() {
        // int found = 0;
        for (TraceDescription d : descriptions) {
            Stop debut = stopsMap.get(d.from);
            Stop fin = stopsMap.get(d.to);
            if (debut != null && fin != null) {
                Set<String> alreadyVisited = new HashSet<>();
                SimpleEntry<Boolean, List<Integer>> sol = roadToLastOptimized(debut.name, fin.name, alreadyVisited, new ArrayList<Integer>());
                if (sol.getKey()) {
                    // found++;
                    d.bifurcation = sol.getValue();
                }
            }
        }
        // System.out.println("J'en ai trouvé " + found);
    }


    /**
     * Check if the stop is a terminus 
     * @param stop the name of a Stop
     * @return True if the stop is a terminus
     */
    public boolean isTerminus(String stop){
        for(TraceDescription t: descriptions){
            if(stop.equals(t.first) || stop.equals(t.last))
                return true;
        }
        return false;
    }


    /**
     * Find the road from the currentStop to the last stop
     * @param currentStop the current stop we are visiting
     * @param last The last stop we are trying to go to
     * @param alreadyVisited All the stop we already have visisted
     * @param bifurcation All the bifurcation encountered from the first stop to the current
     * one
     * @return True and the bifurcation if we found our road to the last stop and 
     * false if we didn't
     */
    public SimpleEntry<Boolean,List<Integer> > roadToLast(String currentStop, String last, List<String> alreadyVisited, List<Integer> bifurcation){

        if(currentStop.equals(last)){
            return new SimpleEntry<>(true,bifurcation);
        }

        //Checker if the current stop is the bad terminus
        if(isTerminus(currentStop)){
            return new SimpleEntry<>(false,null);
        }
        List<String> visitedCopy = new ArrayList<>(alreadyVisited);
        visitedCopy.add(currentStop);

        Stop current = stopsMap.get(currentStop);
        List <SimpleEntry<Boolean,List<Integer>> > solutions = new ArrayList<>();
        for(BifStop b: current.connected.values()){
            if(!visitedCopy.contains(b.stop.name)){
                List<Integer> bifCopy = new ArrayList<>(bifurcation);
                if(b.bifurc!= 0)
                    bifCopy.add(b.bifurc);
                solutions.add(roadToLast(b.stop.name, last, visitedCopy, bifCopy));
            }
        }
        //TODo: Send a list on list of integer in case there is a lot of path for the same direction
        List<Integer> bifSol = new ArrayList<>();
        boolean trouve = false;
        for(SimpleEntry<Boolean,List<Integer>> se: solutions){
            if(se.getKey()){
                trouve = true;
                bifSol = se.getValue();
            }
        }
        return new SimpleEntry<>(trouve,bifSol) ;
    }


    /**
     * Find the road from the currentStop to the last stop
     * @param currentStop the current stop we are visiting
     * @param last The last stop we are trying to go to
     * @param alreadyVisited All the stop we already have visisted
     * @param bifurcation All the bifurcation encountered from the first stop to the current
     * one
     * @return True and the bifurcation if we found our road to the last stop and 
     * false if we didn't
     */
    public SimpleEntry<Boolean, List<Integer>> roadToLastOptimized(String currentStop, String last, Set<String> alreadyVisited, List<Integer> bifurcation) {
        if (currentStop.equals(last)) {
            return new SimpleEntry<>(true, bifurcation);
        }

        // Checker if the current stop is the bad terminus
        if (isTerminus(currentStop)) {
            return new SimpleEntry<>(false, null);
        }

        alreadyVisited.add(currentStop);

        Stop current = stopsMap.get(currentStop);
        List<SimpleEntry<Boolean, List<Integer>>> solutions = new ArrayList<>();
        for (BifStop b : current.connected.values()) {
            if (!alreadyVisited.contains(b.stop.name)) {
                List<Integer> bifCopy = new ArrayList<>(bifurcation);
                if (b.bifurc != 0) {
                    bifCopy.add(b.bifurc);
                }
                solutions.add(roadToLastOptimized(b.stop.name, last, alreadyVisited, bifCopy));
            }
        }

        // Todo: Send a list on list of integer in case there is a lot of path for the same direction
        List<Integer> bifSol = new ArrayList<>();
        boolean trouve = false;
        for (SimpleEntry<Boolean, List<Integer>> se : solutions) {
            if (se.getKey()) {
                trouve = true;
                bifSol = se.getValue();
                break; // Exit loop if a solution is found
            }
        }

        alreadyVisited.remove(currentStop); // Remove current stop from visited after processing

        return new SimpleEntry<>(trouve, bifSol);
    }


    /**
     * Connect 2 stops (start, end) and add them in stopMap if they are not already in 
     * @param start The name of a stop
     * @param end The name of the stop connected to the start
     * @param bifurcation The bifurcation taken to go from start stop to end stop
     */
    public void addStop(String start, String end, int bifurcation){
        Stop startStop = stopsMap.computeIfAbsent(start, Stop::new);
        Stop endStop = stopsMap.computeIfAbsent(end, Stop::new);

        BifStop connectedStop = new BifStop(bifurcation, endStop);
        startStop.addConnectedStop(connectedStop);
    }

    /**
     * Print every stops of the line and its connections
     */
    public void printAllConnectionStops() {
        System.out.println("Affichage des couples (stop, next du stop):");
        for (Map.Entry<String, Stop> entry : stopsMap.entrySet()) {
            Stop stop = entry.getValue();
            System.out.println("Stop: " + stop.name);
            System.out.println("Next:");
            for (BifStop nextEntry : stop.connected.values()) {
                System.out.println(nextEntry.bifurc + ": " + nextEntry.stop.name);
            }
        }
    }


    /**
     * Add all the description to the current one
     * @param desctipt
     */
    public void addDescriptions(List<TraceDescription> desctipt){
        descriptions.addAll(desctipt);
    }


}
