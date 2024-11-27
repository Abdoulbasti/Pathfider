package fr.u_paris.gla.project.itinerary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A representation of a graph
 * for our path-finding algorithm
 */
public class Graph {
    private final Set<Stop> nodes;

    private final Map<Stop, Set<Connection>> connections;

    /**
     * @param nodes the set of graph nodes
     * @param connections the map of all nodes to their edges
     */
    public Graph(Set<Stop> nodes, Map<Stop, Set<Connection>> connections) {
        this.nodes = nodes;
        this.connections = connections;
    }

    /**
     * Returns the set of edges of a node
     * @param node the node from which we want to get the edges
     * @return the sets of the edges for the given node
     */
    public Set<Connection> getConnections(Stop node) {
        return connections.get(node);
    }

    /**
     * Returns the set of graph nodes
     * @return the set of nodes
     */
    public Set<Stop> getNodes() {
        return nodes;
    }

    /**
     * Returns the map of all nodes to their edges in the graph
     * @return the map of all nodes to their edges
     */
    public Map<Stop, Set<Connection>> getConnections() {
        return connections;
    }

    /**
     * Add a node to the graph
     * @param s the node to add
     */
    public void addNode(Stop s) {
        nodes.add(s);
    }

    /**
     * Add a connection to the graph
     * @param stop the node from which the connection starts
     * @param con the connection to add
     */
    public void addConnection(Stop stop, Connection con) {
        Set<Connection> currentConnections =  connections.get(stop);
        if (currentConnections == null) {
            HashSet<Connection> set = new HashSet<>();
            set.add(con);
            connections.put(stop, set);
        }
        else {
            currentConnections.add(con);
        }
    }

    /**
     * Remove a node from the graph.
     * This also removes all connections to and from this node.
     * @param s the node to be removed
     */
    public void removeNode(Stop s) {
        for(Stop stop : nodes) {
            if(getConnections(stop) == null) {
                continue;
            }
            ArrayList<Connection> toRemove = new ArrayList<>();
            for(Connection c : getConnections(stop)) {
                if(c.getStop() == s) {
                    toRemove.add(c);
                }
            }
            for(Connection c : toRemove) {
                getConnections(stop).remove(c);
            }
        }
        nodes.remove(s);
        connections.remove(s);
    }
}
