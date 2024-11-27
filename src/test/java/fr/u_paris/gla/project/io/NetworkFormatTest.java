package fr.u_paris.gla.project.io;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class NetworkFormatTest {

    String t = "00:00";
    NumberFormat GPS_test = NetworkFormat.getGPSFormatter();

    @Test
    public void testParseDurationEqual() {

        assertEquals(Duration.ZERO,  NetworkFormat.parseDuration(t));
    }

    @Test
    public void testParseDurationTooBig() {
        String y = "119:00";
        assertThrows(DateTimeParseException.class, () ->  NetworkFormat.parseDuration(y));
    }

    @Test
    public void formatDuration() {
        assertEquals(t, NetworkFormat.formatDuration(Duration.ZERO));
    }

    @Test
    public void parseThenFormatDuration(){
        String t = "00:00";
        assertEquals(t, NetworkFormat.formatDuration(NetworkFormat.parseDuration(t)));
    }

    @Test
    public void getGPSFormatterPos() {
        double GPS_pos = 1.456489615649813;
        assertEquals(String.valueOf(GPS_pos), GPS_test.format(GPS_pos));


    }

    @Test
    public void getGPSFormatterNeg() {
        double GPS_neg = -1.456489615649813;
        assertEquals(String.valueOf(GPS_neg), GPS_test.format(GPS_neg));


    }
    @Test
    public void getGPSFormatterNul() {
        int GPS_nul = 0;
        assertEquals(String.valueOf(GPS_nul), GPS_test.format(GPS_nul));


    }

    @Test
    public void getGPSFormatterBig() {
        String string_int = "4565156498156489";
        String string_float = "5675747274674276474267479751262167";
        BigDecimal GPS_big = new BigDecimal(string_int + "."  + string_float);


        assertEquals(string_int + "." + string_float.substring(0, NetworkFormat.GPS_PRECISION),
                GPS_test.format(GPS_big).replace(",", "").replace(" ",""));


    }



}