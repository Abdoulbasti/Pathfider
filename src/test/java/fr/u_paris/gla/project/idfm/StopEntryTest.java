package fr.u_paris.gla.project.idfm;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StopEntryTest {
    
    //Test de toString
    /*
    @Test
    public void testToString() {
        StopEntry stop = new StopEntry("Chatelet", 2.346, 48.853);
        // Mise à jour de la valeur attendue pour correspondre au formatage réel
        String expected = "Chatelet [2,346, 48,853]";
        assertEquals(expected, stop.toString());
    }
     */

    /*
    //Si le le test testToString du haut ne marche pas essayer celui du bas
    @Test
    public void testToString() {
        StopEntry stop = new StopEntry("Chatelet", 2.346, 48.853);
        // Mise à jour de la valeur attendue pour correspondre au formatage réel
        String expected = "Chatelet [2.346, 48.853]";
        assertEquals(expected, stop.toString());
    }
     */

    
    //Test de compareTo
    @Test
    public void testCompareTo() {
        StopEntry stop1 = new StopEntry("Chatelet", 2.3467, 48.8534);
        StopEntry stop2 = new StopEntry("Louvre", 2.3360, 48.8606);
        assertTrue(stop1.compareTo(stop2) < 0); //
        assertTrue(stop2.compareTo(stop1) > 0); //
        
        // Test avec la même latitude et longitude mais des noms différents
        StopEntry stop3 = new StopEntry("Chatelet", 2.3467, 48.8534);
        assertEquals(0, stop1.compareTo(stop3));
        
        // Test avec le même nom mais des emplacements différents
        StopEntry stop4 = new StopEntry("Chatelet", 2.3500, 48.8500);
        assertTrue(stop1.compareTo(stop4) > 0);
    }

    //Test de hashCode
    @Test
    public void testHashCode() {
        StopEntry stop1 = new StopEntry("Chatelet", 2.3467, 48.8534);
        StopEntry stop2 = new StopEntry("Chatelet", 2.3467, 48.8534);
        assertEquals(stop1.hashCode(), stop2.hashCode());
    }

    //Test de equals
    @Test
    public void testEquals() {
        StopEntry stop1 = new StopEntry("Chatelet", 2.3467, 48.8534);
        StopEntry stop2 = new StopEntry("Chatelet", 2.3467, 48.8534);
        StopEntry stop3 = new StopEntry("Louvre", 2.3360, 48.8606);
        
        assertEquals(stop1, stop2);
        assertNotEquals(stop1, stop3);
    }
}