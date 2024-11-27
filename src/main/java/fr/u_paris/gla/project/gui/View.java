package fr.u_paris.gla.project.gui;

import fr.u_paris.gla.project.idfm.CSVImageProvider;
import fr.u_paris.gla.project.idfm.IDFMNetworkExtractor;
import fr.u_paris.gla.project.idfm.ImagePair;
import fr.u_paris.gla.project.itinerary.*;
import fr.u_paris.gla.project.utils.ApiUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

public class View extends JFrame {
    private static final Logger LOGGER = Logger
            .getLogger(IDFMNetworkExtractor.class.getName());

    private JPanel CardPanel;
    private JMenuItem Home;
    private JMenuItem Network;
    private JPanel NetworkPanel;

    private JTextField TextLocation;
    private JButton ButtonLocation;
    private JPanel HomePanel;
    private JPanel MainPanel;
    private DefaultTableModel modelStops;

    private JTable tableStops;

    private JTable tableItinerary;

    private DefaultTableModel modelItinerary;

    private JScrollPane paneStops;

    private JScrollPane paneItinerary;
    private JPanel ItineraryPanel;
    private JMenuItem Itinerary;
    private JPanel stationsPanel;
    private JLabel departText;
    private JLabel arrText;

    private JMenuItem Lines;
    private JPanel LinesPanel;
    private JLabel LineLabel;
    private JComboBox LinesComboBox;
    private JButton ShowLineButton;

    private JMenuItem Stops;
    private JPanel StopsPanel;
    private JLabel StopsLabel;
    private JComboBox StopsComboBox;
    private JComboBox StopsLinesComboBox;
    private JButton SeeStopButton;

    private JTextField TextCoord;
    private JButton ButtonCoord;
    private JPanel SearchCoordPanel;
    private JPanel SearchLocationPanel;
    private JLabel NetworkLabel;
    private JLabel FavLabel;
    private JMenuBar ButtonBar;

    private ArrayList<Stop> StopList;

    private String departureCur;

    private String arrivalCur;

    private String searchLocation;

    private String searchCoord;

    private ArrayList<Stop> searchRes;

    private ArrayList<Path> searchResPath;

    private int count = 0;

    private  Finder finder;


    public View(Graph graph, Finder finder, ArrayList<Stop> s) throws HeadlessException {
       this.finder = finder;
        setSize(800, 600);
        MainPanel = new JPanel();
        GridLayout MainLayout = new GridLayout(1, 2, 50, 0);
        MainPanel.setLayout(MainLayout);
        CardLayout CardLay = new CardLayout();
        CardPanel = new JPanel(CardLay);

        HomePanel = new JPanel();
        CardPanel.add(HomePanel);
        SearchCoordPanel = new JPanel();
        SearchLocationPanel = new JPanel();
        GridLayout SearchLayout = new GridLayout(1, 2);
        HomePanel.setLayout(SearchLayout);
        HomePanel.add(SearchCoordPanel);
        HomePanel.add(SearchLocationPanel);
        GridLayout SearchCoordLayout = new GridLayout(2, 1);
        GridLayout SearchLocationLayout = new GridLayout(2, 1);
        SearchCoordPanel.setLayout(SearchCoordLayout);
        SearchLocationPanel.setLayout(SearchLocationLayout);
        TextCoord = new JTextField();
        ButtonCoord = new JButton("Look for coords");
        SearchCoordPanel.add(TextCoord);
        SearchCoordPanel.add(ButtonCoord);
        TextLocation = new JTextField();
        ButtonLocation = new JButton("Look for string");
        SearchLocationPanel.add(TextLocation);
        SearchLocationPanel.add(ButtonLocation);

        NetworkPanel = new JPanel();
        CardPanel.add(NetworkPanel);
        GridLayout NetworkLayout = new GridLayout(3, 1);
        NetworkPanel.setLayout(NetworkLayout);
        NetworkLabel = new JLabel("Network");
        NetworkPanel.add(NetworkLabel);
        stationsPanel = new JPanel();
        NetworkPanel.add(stationsPanel);
        GridLayout StationsLayout = new GridLayout(2, 1);
        departText = new JLabel("Départ: ");
        arrText = new JLabel("Arivée: ");
        stationsPanel.setLayout(StationsLayout);
        stationsPanel.add(departText);
        stationsPanel.add(arrText);
        paneStops = new JScrollPane();
        tableStops = new JTable();
        paneStops.add(tableStops);
        NetworkPanel.add(paneStops);

        ItineraryPanel = new JPanel();
        CardPanel.add(ItineraryPanel);
        GridLayout ItineraryLayout = new GridLayout(2, 1);
        ItineraryPanel.setLayout(ItineraryLayout);
        paneItinerary = new JScrollPane();
        tableItinerary = new JTable();
        paneItinerary.add(tableItinerary);
        ItineraryPanel.add(paneItinerary);

        LinesComboBox = new JComboBox();
        LinesComboBox.setMaximumSize(new Dimension(100, LinesComboBox.getPreferredSize().height));
        LinesComboBox.setPreferredSize(LinesComboBox.getPreferredSize());
        LineLabel = new JLabel("Show line");
        LineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ShowLineButton = new JButton("Open");
        ShowLineButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        LinesPanel = new JPanel();
        LinesPanel.setBackground(new Color(214,173,153));
        LinesPanel.setLayout(new BoxLayout(LinesPanel, BoxLayout.Y_AXIS));
        LinesPanel.add(Box.createHorizontalGlue());
        LinesPanel.add(Box.createHorizontalStrut(2));
        LinesPanel.add(LineLabel);
        LinesPanel.add(Box.createHorizontalStrut(10));
        LinesPanel.add(LinesComboBox);
        LinesPanel.add(Box.createHorizontalStrut(10));
        LinesPanel.add(ShowLineButton);
        LinesPanel.add(Box.createHorizontalStrut(2));
        LinesPanel.add(Box.createHorizontalGlue());

        StopsComboBox = new JComboBox();
        StopsComboBox.setMaximumSize(new Dimension(200, StopsComboBox.getPreferredSize().height));
        StopsComboBox.setPreferredSize(StopsComboBox.getPreferredSize());
        StopsLinesComboBox = new JComboBox();
        StopsLinesComboBox.setMaximumSize(new Dimension(200, StopsLinesComboBox.getPreferredSize().height));
        StopsLinesComboBox.setPreferredSize(StopsComboBox.getPreferredSize());
        StopsLabel = new JLabel("See stop schedules");
        StopsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        SeeStopButton = new JButton("See Schedule");
        SeeStopButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        StopsPanel = new JPanel();
        StopsPanel.setBackground(new Color(171,197,105));
        StopsPanel.setLayout(new BoxLayout(StopsPanel, BoxLayout.Y_AXIS));
        StopsPanel.add(Box.createHorizontalGlue());
        StopsPanel.add(Box.createHorizontalStrut(2));
        StopsPanel.add(StopsLabel);
        StopsPanel.add(Box.createHorizontalStrut(10));
        StopsPanel.add(StopsComboBox);
        StopsPanel.add(Box.createHorizontalStrut(10));
        StopsPanel.add(StopsLinesComboBox);
        StopsPanel.add(Box.createHorizontalStrut(10));
        StopsPanel.add(SeeStopButton);
        StopsPanel.add(Box.createHorizontalStrut(2));
        StopsPanel.add(Box.createHorizontalGlue());

        JPanel buttonBarPanel = new JPanel(new BorderLayout());
        ButtonBar = new JMenuBar();
        GridLayout ButtonLayout = new GridLayout(5, 1);
        ButtonBar.setLayout(ButtonLayout);
        Home = new JMenuItem("Home");
        ButtonBar.add(Home);
        Network = new JMenuItem("Network");
        ButtonBar.add(Network);
        Itinerary = new JMenuItem("Itinerary");
        ButtonBar.add(Itinerary);
        Lines = new JMenuItem("Lines");
        ButtonBar.add(Lines);
        Stops = new JMenuItem("Stops");
        ButtonBar.add(Stops);
        buttonBarPanel.add(ButtonBar, BorderLayout.CENTER);
        buttonBarPanel.setVisible(true);

        ButtonBar.setPreferredSize(new Dimension(50, MainPanel.getHeight()));
        MainPanel.add(ButtonBar);
        MainPanel.add(CardPanel);

        modelStops = (DefaultTableModel) tableStops.getModel();
        modelStops.setColumnCount(2);
        modelStops.setColumnIdentifiers(new Object[]{"Line", "Stop"});

        modelItinerary = (DefaultTableModel) tableItinerary.getModel();
        modelItinerary.setColumnCount(3);
        modelItinerary.setColumnIdentifiers(new Object[]{"Line", "Stop", "Time"});
        this.StopList = s;

        setContentPane(MainPanel);
        setTitle("Pathfinder");
        //setUndecorated(true);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Home.addActionListener(e -> {
            CardPanel.removeAll();
            CardPanel.add(HomePanel);
            CardPanel.repaint();
            CardPanel.revalidate();
        });

        Network.addActionListener(e -> {
            LoadSearchResult(s, modelStops);
            CardPanel.removeAll();
            CardPanel.add(NetworkPanel);

            CardPanel.repaint();
            CardPanel.revalidate();
        });

        Itinerary.addActionListener(e -> {
            LoadSearchResultItinerary(searchResPath, modelItinerary);
            CardPanel.removeAll();
            CardPanel.add(ItineraryPanel);

            CardPanel.repaint();
            CardPanel.revalidate();
        });

        Lines.addActionListener(e -> {
            CardPanel.removeAll();
            CardPanel.add(LinesPanel);
            CardPanel.repaint();
            CardPanel.revalidate();
        });

        Stops.addActionListener(e -> {
            CardPanel.removeAll();
            CardPanel.add(StopsPanel);
            CardPanel.repaint();
            CardPanel.revalidate();
        });

        CSVImageProvider.getLineImageMap().forEach(p -> LinesComboBox.addItem(p));
        ShowLineButton.addActionListener(f -> {
            ImagePair item = (ImagePair) LinesComboBox.getSelectedItem();
            openWebpage(item.getValue());
        });

        Set<Stop> nodes = graph.getNodes();
        List<Stop> nodesList = nodes.stream().sorted(Comparator.comparing(Stop::getName)).toList();
        nodesList.forEach(stop -> StopsComboBox.addItem(stop));
        StopsComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Stop stop = (Stop) StopsComboBox.getSelectedItem();
                StopsLinesComboBox.removeAllItems();
                graph.getConnections(stop).forEach(c ->  {
                    if (!c.toString().equals("WALK"))
                        StopsLinesComboBox.addItem(c);
                });
            }
        });

        SeeStopButton.addActionListener(f -> {
            Connection c;
            if ((c = (Connection) StopsLinesComboBox.getSelectedItem()) != null) {
                createHourWindow(c.getSchedules());
            }
        });

        TextLocation.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    String cur = TextLocation.getText();
                    LoadStringStops(cur);
                    LoadSearchResultItinerary(searchResPath, modelItinerary);
                    System.out.println("Enter key released with text " + cur);
                    CardPanel.removeAll();
                    CardPanel.add(ItineraryPanel);

                    CardPanel.repaint();
                    CardPanel.revalidate();
                }
            }
        });

        ButtonLocation.addActionListener(e -> {
            String cur = TextLocation.getText();
            if (!cur.isEmpty()) {
                CardPanel.removeAll();
                LoadStringStops(cur);
                LoadSearchResultItinerary(searchResPath, modelItinerary);
                CardPanel.add(ItineraryPanel);
            }
            CardPanel.repaint();
            CardPanel.revalidate();
        });

        ButtonCoord.addActionListener(e -> {
            String cur = TextCoord.getText();
            if (!cur.isEmpty()) {
                CardPanel.removeAll();
                LoadStringCoords(cur);
                LoadSearchResultItinerary(searchResPath, modelItinerary);
                CardPanel.add(ItineraryPanel);
            }
            CardPanel.repaint();
            CardPanel.revalidate();
        });

        tableStops.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println("MouseClick: " + e.getX() + ";" + e.getY());
                showOptionsDialog(tableStops, e.getX(), e.getY());
            }
        });

        paneStops.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
    }

    /**
     * @param schedules
     */
    private void createHourWindow(ArrayList<Integer> schedules) {
        JFrame frame = new JFrame("Schedule");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(panel);
        frame.getContentPane().add(scrollPane);

        for (int time : schedules) {
            int hours = time / 3600;
            int minutes = (time % 3600) / 60;
            JLabel label = new JLabel(String.format("%dh%d", hours, minutes));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(label);
        }
        if (schedules.isEmpty()) {
            panel.add(new JLabel("No time available"));
        }

        scrollPane.repaint();
        frame.repaint();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(200, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    /** Used to select a departure/arrival stop in the "Network" section, UNUSED
     * @param table the table that displays the stops
     * @param x x coordinate of mouse click
     * @param y y coordinate of mouse click
     */
    private void showOptionsDialog(JTable table, int x, int y) {
        int selectedRow = table.rowAtPoint(new Point(x, y));
        if (selectedRow != -1) { // If a row is selected

            String stationSel = (String) table.getValueAt(selectedRow, 1);

            // Options to set Departure, Arrival, or Cancel
            Object[] options = {"Departure", "Arrival", "Cancel"};
            int choice = JOptionPane.showOptionDialog(null, "What action would you like to perform for " + stationSel + "?", "Action Selection",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);

            // Handling the choice
            if (choice == 0) {
                this.departureCur = stationSel;
                this.departText.setText("Departure: " + stationSel);
            } else if (choice == 1) {
                this.arrivalCur = stationSel;
                this.arrText.setText("Arrival: " + stationSel);
            } else {
                System.out.println("rien");
            }
            System.out.println("Départ: " + this.departureCur + "; Arrivée: " + this.arrivalCur);
        }
    }

    /** Load all stops related to coordinates
     * @param stops a String in format (x1,y1);(x2,y2)
     */
    public void LoadStringCoords(String stops){
        stops = stops.replaceAll("[()]", "").replaceAll(";", ",");
        String[] stops_array = stops.split(",");
        double[] coords = new double[4];
        for (int i = 0; i < 4; i++){
            coords[i] = Double.parseDouble(stops_array[i]);
        }
        searchResPath = (ArrayList<Path>) finder.findPath(coords[0], coords[1], coords[2], coords[3], LocalDateTime.now().toLocalTime().toSecondOfDay());
    }

    /** Load all stops related to locations
     * @param stops a String in format name1;name2
     */
    public void LoadStringStops(String stops){
        String[] stops_array = stops.split(";");
        double[] coords = new double[4];
        int j = 0;
        for (String stop: stops_array) {
            double[] cur = ApiUtils.getGPSLocation(stop);
            for (int i = 0; i < 2;i++){
                coords[j] = cur[i];
                ++j;
            }
        }
        searchResPath = (ArrayList<Path>) finder.findPath(coords[0], coords[1], coords[2], coords[3], LocalDateTime.now().toLocalTime().toSecondOfDay());
    }


    /** Load a list of stops to display (used for selecting a departure and arrival stop, W.I.P)
     * @param stops the stops list
     * @param model the JTable model that will store them
     */
    public void LoadSearchResult(ArrayList<Stop> stops, DefaultTableModel model) {
        // Clear existing rows from the table
        int cols = model.getColumnCount();
        model.setRowCount(0);
        model.setColumnCount(cols);

        // Add new rows based on the search results
        count = 0;
        for (Stop stop : stops) {
            // Add a row to the table with Stop's line in the first column and Stop's name in the second column
            model.addRow(new Object[]{String.join(",", stop.getLines()), stop.getName()});
            ++count;
        }

        tableStops.revalidate();
        tableStops.repaint();
        paneStops.setViewportView(tableStops);
        paneStops.revalidate();
        paneStops.repaint();
        NetworkPanel.revalidate();
        NetworkPanel.repaint();
    }

    /**
     * Function that takes a list of paths and displays it in a JTabke
     * @param paths the list of paths (from one stop to another)
     * @param model the TableModel that stores the Table's data
     */
    public void LoadSearchResultItinerary(ArrayList<Path> paths, DefaultTableModel model){
        // Clear existing rows from the table
        int cols = model.getColumnCount();
        model.setRowCount(0);
        model.setColumnCount(cols);

        // Add new rows based on the search results
        count = 0;
        Path last = null;
        if (paths != null) {
            for (Path path : paths) {
                // Add a row to the table with Stop's line in the first column and Stop's name in the second column
                double time = path.getStartTime();
                int hours = (int) (time / 3600);
                int minutes = (int) ((time % 3600) / 60);

                model.addRow(new Object[]{path.getLine(), path.getCurrentStop(), String.format("%02d:%02d", hours, minutes)});

                ++count;
                last = path;
            }
        }
        if (last != null)
            model.addRow(new Object[]{last.getLine(), last.getNextStop()});

        tableItinerary.revalidate();
        tableItinerary.repaint();
        paneItinerary.setViewportView(tableItinerary);
        paneItinerary.revalidate();
        paneItinerary.repaint();
        ItineraryPanel.revalidate();
        ItineraryPanel.repaint();
    }

    /** Takes a table's data as argument and displays it
     * @param mod  the table's data
     */
    public void displayTableValues(TableModel mod) {
        for (int row = 0; row < mod.getRowCount(); row++) {
            for (int column = 0; column < mod.getColumnCount(); column++) {
                if (mod.getValueAt(row, column) != null) System.out.print(mod.getValueAt(row, column).toString() + " ");
            }
            System.out.print(";");
        }
        System.out.println();
    }

    /** open a URL in browser
     * @param uri
     */
    private void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                LOGGER.severe("Error opening browser");
            }
        }
    }

    /** open a URL (taken from a String) in browser
     * @param url the url String
     */
    private void openWebpage(String url) {
        try {
            openWebpage(new URL(url).toURI());
        } catch (URISyntaxException|MalformedURLException e) {
            LOGGER.severe("Default desktop browser not set");
        }
    }
}
