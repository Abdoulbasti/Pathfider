package fr.u_paris.gla.project.itinerary;

import fr.u_paris.gla.project.utils.GPS;

import java.util.*;

/**
 * Path finder algorithm.
 * The algorithm is based on an A* algorithm,
 * adapted to our case of path finding in a public transport network.
 */
public class Finder  {
    private Graph graph;

    public Finder(Graph graph) {
        this.graph = graph;
    }

    /**
     *
     * @param from_x the latitude of the starting point in decimal degrees (DD)
     * @param from_y the longitude of the starting point
     * @param to_x the latitude of the arrival point
     * @param to_y the longitude of the arrival point
     * @param startTime the departure time
     * @return the optimal path found by the algorithm
     */
    public List<Path> findPath(double from_x, double from_y, double to_x, double to_y, double startTime) {
        Stop fromNode = new Stop("", "tmp_from", from_x, from_y);
        Stop toNode = new Stop("", "tmp_to", to_x, to_y);

        for (Stop node : graph.getNodes()) {
            double from_dst = GPS.distance(from_x, from_y, node.getLatitude(), node.getLongitude());
            double to_dst = GPS.distance(to_x, to_y, node.getLatitude(), node.getLongitude());
            Connection from_c = new Connection(node, "", from_dst, (int) ((from_dst * 1000) / Parse.WALK_SPEED));
            Connection to_c = new Connection(toNode, "", to_dst, (int) ((to_dst * 1000) / Parse.WALK_SPEED));
            graph.addConnection(fromNode, from_c);
            graph.addConnection(node, to_c);
        }
        graph.addNode(fromNode);
        graph.addNode(toNode);

        List<Path> result = findPath(fromNode, toNode, startTime);

        graph.removeNode(fromNode);
        graph.removeNode(toNode);

        return result;
    }

    /**
     * return a path from startNode to goalNode using A* algorithm
     * @param startNode
     * @param goalNode
     */
    public List<Path> findPath(Stop startNode, Stop goalNode, double startTime) {
        PriorityQueue<Stop> openSet = new PriorityQueue<>(Comparator.comparingDouble(Stop::getF));
        HashSet<Stop> closedSet = new HashSet<>();
        HashMap<Stop, Path> cameFrom = new HashMap<>();
        HashMap<Stop, Double> gScore = new HashMap<>();
        HashMap<Stop, Double> fScore = new HashMap<>();

        // Initialize scores for all nodes to infinity
        for (Stop node : graph.getNodes()) {
            gScore.put(node, Double.POSITIVE_INFINITY);
            fScore.put(node, Double.POSITIVE_INFINITY);
        }

        // The cost of going from start to start is the start time
        gScore.put(startNode, startTime);
        // For the first node, fScore = gScore + heuristic
        fScore.put(startNode, startNode.getHeuristicCost(goalNode));
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Stop current = openSet.poll();
            double currentTime = gScore.get(current);

            if (current.equals(goalNode)) {
                return reconstructPath(cameFrom, current);
            }

            closedSet.add(current);

            if(graph.getConnections(current) == null) {
                continue;
            }

            for (Connection connection : graph.getConnections(current)) {
                Stop neighbor = connection.getStop();
                if (closedSet.contains(neighbor)) {
                    continue; // Ignore the neighbor which is already evaluated.
                }

                double tentativeGScore = currentTime + connection.getCost(currentTime);

                if (tentativeGScore >= gScore.get(neighbor)) {
                    continue; // This is not a better path.
                }

                // This path is the best until now. Record it!
                cameFrom.put(neighbor, new Path(current, connection, currentTime));
                gScore.put(neighbor, tentativeGScore);
                fScore.put(neighbor, tentativeGScore + neighbor.getHeuristicCost(goalNode));

                if (!openSet.contains(neighbor)) {
                    neighbor.setF(fScore.get(neighbor));
                    openSet.add(neighbor);
                }
                else {
                    updatePriority(openSet, neighbor, fScore.get(neighbor));
                }
            }
        }

        // If we reach here, it means there's no path from start to goal
        return null;
    }

    /**
     * Once we found the destination we reconstruct the path
     * @param cameFrom
     * @param current
     * @return path
     */
    private List<Path> reconstructPath(HashMap<Stop, Path> cameFrom, Stop current) {
        List<Path> totalPath = new ArrayList<>();
        totalPath.add(cameFrom.get(current));

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current).getCurrentStop();
            if(cameFrom.get(current) != null) {
                totalPath.add(0, cameFrom.get(current)); // Add to the beginning of the list to maintain order
            }
        }

        return totalPath;
    }

    /**
     * Update the priority queue
     * @param openSet
     * @param node
     * @param newF
     */
    public void updatePriority(PriorityQueue<Stop> openSet, Stop node, double newF) {
        openSet.remove(node);
        node.setF(newF);
        openSet.add(node);
    }
}

