package fr.u_paris.gla.project.idfm;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


class IDFMNetworkExtractorTest {

    //Test de clenLine de ma classe IDFMNetworkExtractor
    @Test
    public void testCleanLine() throws Exception {
        // Création d'un arrêt non identifié avec des coordonnées spécifiques
        UnidentifiedStopEntry unidentifiedStop = new UnidentifiedStopEntry(2.3522, 48.8566); // Coordonnées pour Paris
        
        // Création d'un arrêt candidat proche de l'arrêt non identifié
        StopEntry closeCandidate = new StopEntry("Proche Candidat", 2.3523, 48.8567); // Coordonnées proches de Paris
        
        // Ajout du candidat à l'arrêt non identifié
        unidentifiedStop.addCandidate(closeCandidate);
        
        // Liste des chemins contenant l'arrêt non identifié
        List<List<StopEntry>> paths = new ArrayList<>(Arrays.asList(Arrays.asList(unidentifiedStop)));
        
        // Accès à la méthode cleanLine via la réflexion
        Method cleanLineMethod = IDFMNetworkExtractor.class.getDeclaredMethod("cleanLine", List.class);
        cleanLineMethod.setAccessible(true);
        
        // Invocation de la méthode cleanLine
        boolean result = (Boolean) cleanLineMethod.invoke(null, paths);
        
        // Vérifications
        assertTrue(result, "La méthode cleanLine devrait retourner true si le nettoyage a réussi.");
        assertNotEquals("Unidentified", paths.get(0).get(0).lname, "L'arrêt non identifié devrait avoir été résolu.");
        assertEquals(closeCandidate.lname, paths.get(0).get(0).lname, "L'arrêt devrait être résolu au candidat le plus proche.");
    }



    @Test
    public void testAddCandidate() throws Exception {
        UnidentifiedStopEntry unidentifiedStop = new UnidentifiedStopEntry(2.3522, 48.8566); // Coordonnées pour Paris
        List<StopEntry> path = new ArrayList<>(Arrays.asList(unidentifiedStop));
        TraceEntry trace = new TraceEntry("Ligne1","IDFM:03434","Bus", "dummy_url");
        trace.addPath(path);

        StopEntry candidate = new StopEntry("Proche Candidat", 2.3523, 48.8567); // Coordonnées proches

        Method method = IDFMNetworkExtractor.class.getDeclaredMethod("addCandidate", TraceEntry.class, StopEntry.class);
        method.setAccessible(true);

        method.invoke(null, trace, candidate);

        //L'appel c'est derouler correctement
        assertTrue(true, "L'appel de addCandidate s'est déroulé sans erreur.");
    }
}