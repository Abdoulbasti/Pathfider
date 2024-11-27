package fr.u_paris.gla.project.idfm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import fr.u_paris.gla.project.io.NetworkFormat;
import fr.u_paris.gla.project.utils.GPS;

public class CSVStreamProviderTest {

    //Test de hasNext, pour le cas ou il y'a un trace et cas ou il n'y en a pas
    @Test
    public void testHasNext() {
        // Scénario sans Trace
        CSVStreamProvider providerWithoutTrace = new CSVStreamProvider(Collections.emptyIterator(),null);
        assertFalse(providerWithoutTrace.hasNext(), "hasNext should return false when no traces are provided");

        // Scénario avec Trace
        StopEntry stop1 = new StopEntry("Stop1", 2.3522, 48.8566);
        StopEntry stop2 = new StopEntry("Stop2", 2.295, 48.8738);
        List<StopEntry> path = Arrays.asList(stop1, stop2);

        TraceEntry trace = new TraceEntry("Ligne1","IDFM:03434","Bus", "dummy_url");
        trace.addPath(path);

        CSVStreamProvider providerWithTrace = new CSVStreamProvider(Arrays.asList(trace).iterator(),null);
        assertTrue(providerWithTrace.hasNext(), "hasNext should return true when traces are provided");
    }



    //Test de la methode next()
    @Test
    public void testNext() {
        // Initialisation des données d'exemple directement dans le test
        StopEntry start = new StopEntry("Début", 2.3522, 48.8566); // Paris
        StopEntry end = new StopEntry("Fin", 2.295, 48.8738); // Proche de Paris
        
        TraceEntry traceEntry = new TraceEntry("Ligne1","IDFM:03434","Bus", "dummy_url");
        traceEntry.addPath(Arrays.asList(start, end)); // Ajout d'un chemin à la trace
        
        CSVStreamProvider provider = new CSVStreamProvider(Collections.singletonList(traceEntry).iterator(),null);

        assertTrue(provider.hasNext(), "Doit avoir un prochain élément");

        String[] result = provider.next();
        assertNotNull(result, "Le résultat ne doit pas être null");

        // Vérifications spécifiques sur le format des données de sortie
        assertEquals(start.lname, result[NetworkFormat.START_INDEX], "Le nom de l'arrêt de départ doit correspondre");
        assertEquals(end.lname, result[NetworkFormat.STOP_INDEX], "Le nom de l'arrêt d'arrivée doit correspondre");

        // Calcul et vérification de la distance attendue
        double expectedDistance = GPS.distance(start.latitude, start.longitude, end.latitude, end.longitude);
        String expectedDistanceFormatted = NumberFormat.getInstance(Locale.ENGLISH).format(expectedDistance);
        assertEquals(expectedDistanceFormatted, result[NetworkFormat.DISTANCE_INDEX], "La distance doit correspondre");
    }



    //Test de la methode private fillStation avec la réflexion
    @Test
    public void testFillStation() throws Exception {
        // Initialisation des données de test
        StopEntry stop = new StopEntry("StopName", 2.3522, 48.8566); // Exemple de coordonnées pour Paris
        String[] nextLine = new String[NetworkFormat.NUMBER_COLUMNS];

        // Accès à la méthode fillStation via la réflexion
        Method fillStationMethod = CSVStreamProvider.class.getDeclaredMethod("fillStation", StopEntry.class, String[].class, int.class);
        fillStationMethod.setAccessible(true);

        // Invocation de la méthode fillStation
        fillStationMethod.invoke(null, stop, nextLine, NetworkFormat.START_INDEX);

        // Format attendu pour la latitude et la longitude
        NumberFormat gpsFormatter = NetworkFormat.getGPSFormatter();
        String expectedLatitudeLongitude = MessageFormat.format("{0}, {1}",
                gpsFormatter.format(stop.latitude),
                gpsFormatter.format(stop.longitude));

        // Vérifications
        assertEquals(stop.lname, nextLine[NetworkFormat.START_INDEX], "Le nom de l'arrêt doit correspondre.");
        assertEquals(expectedLatitudeLongitude, nextLine[NetworkFormat.START_INDEX + 1], "Les coordonnées GPS doivent correspondre.");
    }




    //Test de la méthode static private distanceToTime()
    @Test
    public void testDistanceToTime() throws Exception {
        // Valeurs fictives pour TWO_ACCELERATION_DISTANCE et MAX_SPEED
        final double TWO_ACCELERATION_DISTANCE = 0.1;
        final double MAX_SPEED = 10.0;
        
        // Exemple de distance à tester
        double distanceExample = 1.0; // 1 km
        
        // Calcul attendu basé sur la formule fournie
        double expected = Math.max(0, distanceExample - TWO_ACCELERATION_DISTANCE) / MAX_SPEED
                          + (2 * Math.sqrt(Math.min(distanceExample, TWO_ACCELERATION_DISTANCE) * TWO_ACCELERATION_DISTANCE) / MAX_SPEED);
        
        // Accès à la méthode distanceToTime via la réflexion
        Method method = CSVStreamProvider.class.getDeclaredMethod("distanceToTime", double.class, String.class);
        method.setAccessible(true);
        
        // Invocation de la méthode distanceToTime et stockage du résultat
        double result = (Double) method.invoke(null, distanceExample, "Bus");
        
        // Assertion pour vérifier si le résultat est conforme à l'attendu
        assertEquals(expected, result, "Le calcul du temps à partir de la distance devrait être conforme à l'attendu.");
    }
}