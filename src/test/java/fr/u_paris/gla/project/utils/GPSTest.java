package fr.u_paris.gla.project.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GPSTest {


    @Test
    public void testDistance_SameLat(){
        assertDoesNotThrow(
                () -> {
                    GPS.distance(5, 3, 5, 11);
                }
        );
    }

    @Test
    public void distance_SameLon(){
        assertDoesNotThrow(
                () -> {
                    GPS.distance(5, 3, 7, 3);
                }
        );
    }

    @Test
    public void distance_SamePoint() {
        assertEquals(0.0, GPS.distance(5, 3, 5, 3) );
    }

    @Test
    public void distance_NegativePoint(){
        assertNotEquals(0.0, GPS.distance(-5, 7, -13, 4));
    }


}