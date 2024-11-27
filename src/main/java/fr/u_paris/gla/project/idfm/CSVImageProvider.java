/**
 *
 */
package fr.u_paris.gla.project.idfm;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Logger;

import fr.u_paris.gla.project.io.ImageFormat;
import fr.u_paris.gla.project.utils.CSVTools;

public final class CSVImageProvider {

    /**
     * The logger for information on the process
     */
    private static final Logger LOGGER = Logger
            .getLogger(IDFMNetworkExtractor.class.getName());

    private static final NumberFormat MINUTES_SECOND_FORMATTER = NumberFormat
        .getInstance(Locale.ENGLISH);

    static {
        MINUTES_SECOND_FORMATTER.setMinimumIntegerDigits(2);
    }

    private final String[] line = new String[ImageFormat.NUMBER_COLUMNS];

    private final Iterator<Transport> current;

    private static ArrayList<ImagePair> lineImageMap;

    public static final String FILE_NAME = IDFMNetworkExtractor.IMAGES_FILE_NAME;


    /** Create the stream provider */
    public CSVImageProvider(Iterator<Transport> traces) {
        this.current = traces;
    }

    /** Check if next exists */
    public boolean hasNext() {
        return this.current.hasNext();
    }

    /** Get Next element */
    public String[] next() {
        if (!this.hasNext()) {
            return null;
        }

        Transport element = this.current.next();
        this.line[ImageFormat.LINE_INDEX] = element.name;
        this.line[ImageFormat.LINE_DETAIL_INDEX] = element.type;
        this.line[ImageFormat.IMAGE_URL_INDEX] = element.image_url;

        return Arrays.copyOf(this.line, this.line.length);
    }

    /**
     * This function returns a list of ImagePair, which represents the name of the line and the link to the
     * image of the line details.
     * The list is created once and then store in a static variable.
     * @return an ArrayList of ImagePair
     */
    public static ArrayList<ImagePair> getLineImageMap() {
        if (lineImageMap != null)
            return lineImageMap;
        lineImageMap = new ArrayList<>();
        try {
            CSVTools.readCSVFromFile(FILE_NAME,
                    (String[] line) ->
                    {
                        String label = line[ImageFormat.LINE_INDEX];
                        String detail = line[ImageFormat.LINE_DETAIL_INDEX];
                        String imageUrl = line[ImageFormat.IMAGE_URL_INDEX];

                        lineImageMap.add(new ImagePair(label, detail, imageUrl));
                    });
        }
        catch(IOException e){
            LOGGER.severe("File is not generated yet");
        }
        lineImageMap.sort(Comparator.comparing(ImagePair::getLabel));
        return lineImageMap;
    }
}
