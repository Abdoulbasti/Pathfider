/**
 *
 */
package fr.u_paris.gla.project.idfm;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a transport line
 *
 * @author Emmanuel Bigeon
 */
public final class TraceEntry {
    public final String lname;
    public final String id;
    public final String type;
    public final String url;
    


    private List<String> terminus = new ArrayList<>();
    private List<List<StopEntry>> paths = new ArrayList<>();
    List <TraceDescription> descriptions = new ArrayList<>();
    

    /**
     * Create a transport line.
     *
     * @param lname the name of the line
     */
    public TraceEntry(String lname,String ident,String t_type, String img_url) {
        super();
        this.lname = lname;
        this.id = ident;
        this.type = t_type;
        this.url = img_url;
    }

    // FIXME list of lists are bad practice in direct access...
    /** @return the list of paths */
    public List<List<StopEntry>> getPaths() {
        // TODO Ne pas retourner directement la liste
        return paths;
    }

    /** @return the list of terminus */
    public List<String> getTerminus() {
        return terminus;
    }

    public void addPath(List<StopEntry> path) {
        paths.add(new ArrayList<>(path));
    }

    public void addTerminus(String term) {
        terminus.add(term);
    }

    public boolean isDescriptionEmpty(){
        return descriptions.isEmpty();
    }

    /**
     * Add all the description to the current one
     * @param desctipt
     */
    public void addDescriptions(List<TraceDescription> desctipt){
        descriptions.addAll(desctipt);
    }
}
