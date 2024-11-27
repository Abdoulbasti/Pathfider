package fr.u_paris.gla.project.utils;

import fr.u_paris.gla.project.idfm.IDFMNetworkExtractor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiUtils {
    /** The logger for information on the process */
    private static final Logger LOGGER = Logger
            .getLogger(IDFMNetworkExtractor.class.getName());

    /**
     * OpenStreetMap API URL
     */
    private static final String OSM_URL = "https://nominatim.openstreetmap.org/search";

    /**
     * This function returns the GPS location of a string, using OSM API.
     * The string can be anything, and adress, a street, a place.
     * @param term the term to search
     * @return the GPS location, (0,0) if not result
     */
    public static double[] getGPSLocation(String term) {
        try {
            String urlString = String.format("%s?q=%s&format=json", OSM_URL, URLEncoder.encode(term, StandardCharsets.UTF_8));
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();

            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            JSONArray jsonArray = new JSONArray(response.toString());

            if (!jsonArray.isEmpty()) {
                JSONObject firstResult = jsonArray.getJSONObject(0);

                double lat = firstResult.getDouble("lat");
                double lon = firstResult.getDouble("lon");
                return new double[]{lat, lon};
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, e,
                    () -> "Error accessing the API");
        }
        return new double[]{0, 0};
    }
}
