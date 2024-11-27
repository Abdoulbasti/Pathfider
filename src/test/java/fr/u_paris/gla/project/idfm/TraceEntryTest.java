package fr.u_paris.gla.project.idfm;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

public class TraceEntryTest {
    
    //addTerminus
    @Test
    public void testAddTerminus() {
        TraceEntry traceEntry = new TraceEntry("Ligne 1","IDFM:03434","Bus", "dummy_url");
        String terminus1 = "Terminus A";
        String terminus2 = "Terminus B";

        //Ajouter des arrêt sur la ligne
        traceEntry.addTerminus(terminus1);
        traceEntry.addTerminus(terminus2);
        List<String> terminusList = traceEntry.getTerminus();
        
        assertEquals(2, terminusList.size(), "La liste des terminus doit contenir deux éléments.");
        assertTrue(terminusList.contains(terminus1), "La liste des terminus doit contenir le terminus A.");
        assertTrue(terminusList.contains(terminus2), "La liste des terminus doit contenir le terminus B.");
    }

    //addPath
    @Test
    public void testAddPath() {
        TraceEntry traceEntry = new TraceEntry("Ligne 1","IDFM:03434","Bus", "dummy_url");
        StopEntry stop1 = new StopEntry("Station 1", 2.300, 48.850);
        StopEntry stop2 = new StopEntry("Station 2", 2.310, 48.855);
        List<StopEntry> path = Arrays.asList(stop1, stop2);
        traceEntry.addPath(path);
        List<List<StopEntry>> paths = traceEntry.getPaths();
        
        assertEquals(1, paths.size(), "Il doit y avoir un chemin dans la liste des chemins.");
        assertEquals(2, paths.get(0).size(), "Le chemin ajouté doit contenir deux arrêts.");
        assertTrue(paths.get(0).containsAll(path), "Le chemin ajouté doit contenir les arrêts spécifiés.");
    }


    //Verfier si le nom de la ligne lname est correctement initialiser
    @Test
    public void testTraceEntryName() {
        TraceEntry traceEntry = new TraceEntry("Ligne 1","IDFM:03434","Bus", "dummy_url");
        assertEquals("Ligne 1", traceEntry.lname, "Le nom de la ligne doit être 'Ligne 1'.");
    }
}