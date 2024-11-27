package fr.u_paris.gla.project.utils;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CSVToolsTest {




    @Test
    public void readCSVFromURL_invalid() {
        // TODO Fix the exception thrown
        /**
        assertThrows(IOException.class,() -> {
                    Consumer<String[]> test = s -> System.out.println(Arrays.toString(s));
                    CSVTools.readCSVFromURL("https://google.fr",
                            test);

                }
                );
        */
    }

    @Test
    public void testreadCSVFromURL_valid() {
        assertDoesNotThrow(() -> {
                    Consumer<String[]> test = s -> System.out.println(Arrays.toString(s));
                    CSVTools.readCSVFromURL("https://people.sc.fsu.edu/~jburkardt/data/csv/addresses.csv",
                            test);

                }
        );
    }

    String randomUniqueFilename() {
        String prefix = "file_";
        String randomString = UUID.randomUUID().toString().substring(0, 8);
        return prefix + randomString + ".csv";
    }

    @Test
    void writeCSVToFile() {
        String fileName = randomUniqueFilename();
        assertDoesNotThrow(() -> {
            String[] stuff = {"jsqdsqdsqsqffdfgzava", "pfezegrrbeebn", "dfbsduifzegbczi", "sdfsdfcy"};
            String[][] t = {stuff, stuff};
            Stream<String[]> test = Arrays.stream(t);
            CSVTools.writeCSVToFile(fileName, test);
            File tmp = new File(fileName);
            tmp.delete();
        });
    }

    @Test
    void writeCSVToFile_specialName() {
        String fileName = randomUniqueFilename();
        assertDoesNotThrow(() -> {
            String[] stuff = {"jsqdsqdsqsqffdfgzava", "pfezegrrbeebn", "dfbsduifzegbczi", "sdfsdfcy"};
            String[][] t = {stuff, stuff};
            Stream<String[]> test = Arrays.stream(t);
            CSVTools.writeCSVToFile(fileName, test);
            File tmp = new File(fileName);
            tmp.delete();
        });
    }

    @Test
    void writeCSVToFile_invalidName() {

        assertThrows( IOException.class ,() -> {
            String[] stuff = {"jsqdsqdsqsqffdfgzava", "pfezegrrbeebn", "dfbsduifzegbczi", "sdfsdfcy"};
            String[][] t = {stuff, stuff};
            Stream<String[]> test = Arrays.stream(t);
            CSVTools.writeCSVToFile(".", test);
        });
    }
}