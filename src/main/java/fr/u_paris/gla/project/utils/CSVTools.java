/**
 *
 */
package fr.u_paris.gla.project.utils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVParser;
import com.opencsv.ICSVWriter;
import com.opencsv.exceptions.CsvValidationException;

/** A CSV tool class.
 *
 * @author Emmanuel Bigeon */
public final class CSVTools {

    /** Hidden constructor of tool class */
    private CSVTools() {
        // Tool class
    }

    /** get a CSV file from a URL, download and parse it, and keep values in memory
     * @param is the address of the CSV file
     * @param contentLineConsumer the variable used to store the data
     * @throws IOException if it's impossible to download the file
     */
    public static void readCSVFromInputStream(InputStream is, Consumer<String[]> contentLineConsumer)
            throws IOException {
        ICSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        try (Reader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            CSVReaderBuilder csvBuilder = new CSVReaderBuilder(reader)
                    .withCSVParser(parser);
            try (CSVReader csv = csvBuilder.build()) {
                String[] line = csv.readNextSilently(); // Eliminate header
                while (csv.peek() != null) {
                    line = csv.readNext();
                    contentLineConsumer.accept(line);
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("Invalid csv file", e); //$NON-NLS-1$
        }
    }


    /** get a CSV file from a file and parse it, keeping values in memory
     * @param filename the saved file's name and path
     * @param contentLineConsumer the variable used to store the data
     * @throws IOException if it's impossible to read the file
     */
    public static void readCSVFromFile(String filename, Consumer<String[]> contentLineConsumer)
            throws IOException {
        File file = new File(filename);
        readCSVFromInputStream(new FileInputStream(file), contentLineConsumer);
    }

    /** get a CSV file from a URL, download and parse it, and keep values in memory
     * @param url the address of the CSV file
     * @param contentLineConsumer the variable used to store the data
     * @throws IOException if it's impossible to download the file
     */
    public static void readCSVFromURL(String url, Consumer<String[]> contentLineConsumer)
            throws IOException {
        readCSVFromInputStream(new URL(url).openStream(), contentLineConsumer);
    }

    /** Save our current CSV variable's data into an actual file
     * @param filename saved file's name and path
     * @param contentLinesConsumer our data variable
     * @throws IOException if we can't write the data into the file
     */
    public static void writeCSVToFile(String filename,
            Stream<String[]> contentLinesConsumer) throws IOException {
        try (FileWriter writer = new FileWriter(filename, StandardCharsets.UTF_8)) {
            CSVWriterBuilder wBuilder = new CSVWriterBuilder(writer).withSeparator(';');
            try (ICSVWriter csv = wBuilder.build()) {
                contentLinesConsumer.forEachOrdered(csv::writeNext);
            }
        }
    }
}
