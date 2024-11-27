package fr.u_paris.gla.project;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.*;

import fr.u_paris.gla.project.gui.View;
import fr.u_paris.gla.project.itinerary.*;

/** Simple application model.
 *
 * @author Emmanuel Bigeon */
public class App {
    /**
     * 
     */
    private static final String UNSPECIFIED = "Unspecified";         //$NON-NLS-1$
    /** The logo image name. */
    private static final String LOGO_NAME   = "uparis_logo_rvb.png"; //$NON-NLS-1$
    /** Image height. */
    private static final int    HEIGHT      = 256;
    /** Image width. */
    private static final int    WIDTH       = HEIGHT;

    /** Resizes an image.
     *
     * @param src source image
     * @param w width
     * @param h height
     * @return the resized image */
    private static Image getScaledImage(Image src, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(src, 0, 0, w, h, null);
        g2d.dispose();
        return resizedImg;
    }

    /** Application entry point.
     *
     * @param args launching arguments */
    public static void main(String[] args) {
        if (args.length > 0) {
            for (String string : args) {
                if ("--info".equals(string)) { //$NON-NLS-1$
                    printAppInfos(System.out);
                    return;
                }
                if ("--gui".equals(string)) { //$NON-NLS-1$
                    showLogo();
                }
            }
        }
        else {
            run();
        }
    }

    public static void run() {
        Parse parse = new Parse();
        parse.parseFiles();

        Graph graph = parse.createGraph();
        Finder finder = parse.createFinder(graph);

        ArrayList<Stop> s = new ArrayList<>();
        s.add(new Stop("M8", "Balard", 1.0315897, 3.0265513));
        s.add(new Stop("M14", "Gare de Lyon", 2.4658452681, 3.0265513));

        SwingUtilities.invokeLater(() -> new View(graph, finder, s));
    }

    /** @param out the output stream */
    public static void printAppInfos(PrintStream out) {
        Properties props = readApplicationProperties();

        out.println("Application: " + props.getProperty("app.name", UNSPECIFIED)); //$NON-NLS-1$ //$NON-NLS-2$
        out.println("Version: " + props.getProperty("app.version", UNSPECIFIED)); //$NON-NLS-1$ //$NON-NLS-2$
        out.println("By: " + props.getProperty("app.team", UNSPECIFIED)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static Properties readApplicationProperties() {
        Properties props = new Properties();
        try (InputStream is = App.class.getResourceAsStream("application.properties")) { //$NON-NLS-1$
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read application informations", e); //$NON-NLS-1$
        }
        return props;
    }

    /** Shows the logo in an image. */
    public static void showLogo() {
        Properties props = readApplicationProperties();

        JFrame frame = new JFrame(props.getProperty("app.name")); //$NON-NLS-1$
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel container = new JLabel();

        try (InputStream is = App.class.getResourceAsStream(LOGO_NAME)) {
            if (is == null) {
                container.setText("Image Not Found");
            } else {
                BufferedImage img = ImageIO.read(is);
                ImageIcon icon = new ImageIcon(img);
                ImageIcon resized = new ImageIcon(
                        getScaledImage(icon.getImage(), WIDTH, HEIGHT));

                container.setIcon(resized);
            }
        } catch (IOException e) {
            container.setText("Image Not Read: " + e.getLocalizedMessage());
        }

        frame.getContentPane().add(container);

        frame.pack();
        frame.setVisible(true);
    }
}
