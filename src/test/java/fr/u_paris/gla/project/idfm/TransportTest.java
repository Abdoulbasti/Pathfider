package fr.u_paris.gla.project.idfm;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class TransportTest {

    /*@Test
    public void testRoadToLast() {
        Transport transport = new Transport("Test Line", "Bus");
        transport.addStop("A", "B", 1);
        transport.addStop("B", "C", 2);
        transport.addStop("C", "D", 3);
        TraceDescription td = new TraceDescription("A", "D", "A", "D");
        transport.descriptions.add(td);
    
        List<String> visited = new ArrayList<>();
        List<Integer> bifurcations = new ArrayList<>();
        SimpleEntry<Boolean, List<Integer>> result = transport.roadToLast("A", "D", visited, bifurcations);
        assertFalse(result.getKey());
        assertEquals(List.of(1, 2, 3), result.getValue());
    }*/

    /*@Test
    public void testRoadToLastOptimized() {
        SimpleEntry<Boolean, List<Integer>> result = transport.roadToLastOptimized("A", "D", new HashSet<>(), new ArrayList<>());
        assertTrue(result.getKey());
        assertEquals(List.of(1, 2, 3), result.getValue());
    }*/

    @Test
    public void testIsTerminus() {
        Transport transport = new Transport("Test Line", "Bus", "dummy_url");
        transport.addStop("A", "B", 1);
        transport.addStop("B", "C", 2);
        transport.addStop("C", "D", 3);
        TraceDescription td = new TraceDescription("A", "D", "A", "D");
        transport.descriptions.add(td);

        assertTrue(transport.isTerminus("A"));
        assertTrue(transport.isTerminus("D"));
        assertFalse(transport.isTerminus("B"));
    }

    @Test
    public void testAddStop() {
        Transport transport = new Transport("Test Line", "Bus", "dummy_url");
        transport.addStop("A", "B", 1);
        transport.addStop("B", "C", 2);
        transport.addStop("C", "D", 3);
        TraceDescription td = new TraceDescription("A", "D", "A", "D");
        transport.descriptions.add(td);    

        transport.addStop("D", "E", 4);
        assertTrue(transport.stopsMap.containsKey("E"));
        assertEquals("E", transport.stopsMap.get("E").name);
        assertTrue(transport.stopsMap.get("D").isStopConnected("E"));
    }
}