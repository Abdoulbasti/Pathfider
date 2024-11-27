package fr.u_paris.gla.project.io;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleFormatTest {

    @Test
    public void getTripSequence() {
        String rpz = "4,5,19,21";
        List<Integer> test = Arrays.asList(4, 5, 19, 21);

        assertEquals(test, ScheduleFormat.getTripSequence(rpz));
    }

    @Test
    public void getTimeFormatter() {
        DateTimeFormatter formatter = ScheduleFormat.getTimeFormatter();
        LocalDateTime date =   LocalDateTime.now();
        String test = date.format(formatter);
        //format date: YYYY-MM-DDTHH:MM:SS.DECIMAL
        assertEquals(date.toString().substring(11, 16), test);


    }
}