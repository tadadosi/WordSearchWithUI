package application.grid.wordsearch;

import java.util.ArrayList;
import java.util.List;

public class DescrDirections {
    List<Integer> directionsPossibility;
    
    public DescrDirections() {
    }
    
    public DescrDirections(Integer singleDescription) {
        directionsPossibility = new ArrayList<Integer>();
        directionsPossibility.add(singleDescription);
    }
    
    public DescrDirections(List<Integer> descriptionsDir) {
        this.directionsPossibility = descriptionsDir;
    }
    
    public List<Integer> getDirectionsPossibility() {
        return directionsPossibility;
    }

    public void setDirectionsPossibility(List<Integer> directionsPossibility) {
        this.directionsPossibility = directionsPossibility;
    }
    
    
}
