package fr.u_paris.gla.project.idfm;

/**
 * This class is made specific to store a Pair of Name/Link to be used in a Swing ComboBox
 * These getters ables a ComboBox to show the label returned by toString, and get a specific value when the object is returned
 */
public class ImagePair {
    /**
     * The name of the line
     */
    private final String line;

    /**
     * The label that will be shown in the ComboBox
     */
    private final String label;

    /**
     * The link of the line details
     */
    private final String value;

    public ImagePair(String label, String label_detail, String value){
        this.line = label;
        this.label = label + " - " + label_detail;
        this.value = value;
    }

    public String getLabel(){
        return this.label;
    }

    public String getLine(){
        return this.line;
    }

    public String getValue(){
        return this.value;
    }

    @Override
    public String toString(){
        return label;
    }
}
