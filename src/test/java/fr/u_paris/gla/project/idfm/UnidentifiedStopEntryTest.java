package fr.u_paris.gla.project.idfm;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


public class UnidentifiedStopEntryTest {


    // Test de la méthode resolve de la classe UnidentifiedStopEntry
    @Test
    public void testResolve() {
        // Création d'un UnidentifiedStopEntry avec des coordonnées arbitraires (0,0)
        UnidentifiedStopEntry unidentifiedStopEntry = new UnidentifiedStopEntry(0, 0);

        // Test lorsque la liste des candidats est vide
        assertNull(unidentifiedStopEntry.resolve());

        // Test lorsque la liste des candidats contient un seul StopEntry
        StopEntry stopEntry1 = new StopEntry("Stop1", 10.0, 20.0);
        unidentifiedStopEntry.addCandidate(stopEntry1);
        assertEquals(stopEntry1, unidentifiedStopEntry.resolve());

        // Test lorsque la liste des candidats contient plusieurs StopEntries
        StopEntry stopEntry2 = new StopEntry("Stop2", 30.0, 40.0);
        unidentifiedStopEntry.addCandidate(stopEntry2);
        // En supposant que la méthode GPS.distance fonctionne correctement, stopEntry1 devrait être plus proche
        assertEquals(stopEntry1, unidentifiedStopEntry.resolve());

        // Test lorsque la liste des candidats contient plusieurs StopEntries et que le plus proche change
        UnidentifiedStopEntry unidentifiedStopEntry2 = new UnidentifiedStopEntry(35.0, 45.0);
        unidentifiedStopEntry2.addCandidate(stopEntry1);
        unidentifiedStopEntry2.addCandidate(stopEntry2);
        // Maintenant, stopEntry1 devrait être plus proche
        assertEquals(stopEntry2, unidentifiedStopEntry2.resolve());
    }


    // Test de la méthode addCandidate de la classe UnidentifiedStopEntry
    @Test
    public void testAddCandidate() {
        // Création d'un UnidentifiedStopEntry avec des coordonnées arbitraires (0,0)
        UnidentifiedStopEntry unidentifiedStopEntry = new UnidentifiedStopEntry(0, 0);

        // Test lorsque nous ajoutons un StopEntry à la liste des candidats
        StopEntry stopEntry1 = new StopEntry("Stop1", 10.0, 20.0);
        unidentifiedStopEntry.addCandidate(stopEntry1);
        assertEquals(stopEntry1, unidentifiedStopEntry.resolve());

        // Test lorsque nous ajoutons un autre StopEntry à la liste des candidats
        StopEntry stopEntry2 = new StopEntry("Stop2", 30.0, 40.0);
        unidentifiedStopEntry.addCandidate(stopEntry2);
        // En supposant que la méthode GPS.distance fonctionne correctement, stopEntry1 devrait être plus proche
        assertEquals(stopEntry1, unidentifiedStopEntry.resolve());
    }


    // Test de la méthode equals de la classe UnidentifiedStopEntry
    @Test
    public void testEquals() {
        // Création de deux UnidentifiedStopEntry avec les mêmes coordonnées
        UnidentifiedStopEntry unidentifiedStopEntry1 = new UnidentifiedStopEntry(0, 0);
        UnidentifiedStopEntry unidentifiedStopEntry2 = new UnidentifiedStopEntry(0, 0);

        // Test lorsque nous comparons un UnidentifiedStopEntry avec lui-même
        assertTrue(unidentifiedStopEntry1.equals(unidentifiedStopEntry1));

        // Test lorsque nous comparons deux UnidentifiedStopEntry qui n'ont pas de candidats
        assertTrue(unidentifiedStopEntry1.equals(unidentifiedStopEntry2));

        // Test lorsque nous ajoutons le même StopEntry aux deux UnidentifiedStopEntry
        StopEntry stopEntry = new StopEntry("Stop1", 10.0, 20.0);
        unidentifiedStopEntry1.addCandidate(stopEntry);
        unidentifiedStopEntry2.addCandidate(stopEntry);
        assertTrue(unidentifiedStopEntry1.equals(unidentifiedStopEntry2));

        // Test lorsque nous ajoutons un autre StopEntry à l'un des UnidentifiedStopEntry
        StopEntry stopEntry2 = new StopEntry("Stop2", 30.0, 40.0);
        unidentifiedStopEntry1.addCandidate(stopEntry2);
        assertFalse(unidentifiedStopEntry1.equals(unidentifiedStopEntry2));
    }
}