package fr.u_paris.gla.project.idfm;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StopTest {


    @Test
    public void testIsStopConnected() {

        Stop stop = new Stop("Stop1");
        BifStop bifStop1 = new BifStop(1, new Stop("Stop2"));
        // Initially, no stops are connected
        assertFalse(stop.isStopConnected("Stop2"));

        // Add a connected stop
        stop.addConnectedStop(bifStop1);

        // Now, Stop2 should be connected
        assertTrue(stop.isStopConnected("Stop2"));
    }


    @Test
    public void testGetConnectedStop() {

        Stop stop = new Stop("Stop1");
        BifStop bifStop1 = new BifStop(1, new Stop("Stop2"));
        BifStop bifStop2 = new BifStop(2, new Stop("Stop3"));

        // Add two connected stops
        stop.addConnectedStop(bifStop1);
        stop.addConnectedStop(bifStop2);

        // Retrieve the connected stops
        BifStop retrievedStop1 = stop.getConnectedStop("Stop2");
        BifStop retrievedStop2 = stop.getConnectedStop("Stop3");

        // Check if the correct stops were retrieved
        assertEquals(bifStop1, retrievedStop1);
        assertEquals(bifStop2, retrievedStop2);
    }

    @Test
    public void testAddConnectedStop() {
        Stop stop = new Stop("Stop1");
        BifStop bifStop1 = new BifStop(1, new Stop("Stop2"));

        // Add a connected stop
        stop.addConnectedStop(bifStop1);

        // Check if the stop was added
        assertTrue(stop.isStopConnected("Stop2"));
    }


    @Test
    public void testSHJH(){
        Stop stop = new Stop("Stop2323");
        BifStop bifStop1 = new BifStop(1, new Stop("Stop2323"));

        // Add a connected stop
        stop.addConnectedStop(bifStop1);

        // Check if the stop was added
        assertTrue(stop.isStopConnected("Stop2323"));
    }
}