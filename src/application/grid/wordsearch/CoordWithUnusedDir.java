package application.grid.wordsearch;

import java.util.ArrayList;
import java.util.List;

public class CoordWithUnusedDir {
    private List<Integer> unusedGoodDirection;
    private int[] currentCoord;
    
    public CoordWithUnusedDir(CoordWithUnusedDir otherCoord) {
        currentCoord =  new int[otherCoord.getCurrentCoord().length];
        System.arraycopy(otherCoord.getCurrentCoord(), 0, this.currentCoord, 0, otherCoord.getCurrentCoord().length);
        
        this.unusedGoodDirection = new ArrayList<Integer>();
        for (Integer unusedDirection : otherCoord.getUnusedGoodDirection() ) {
            this.unusedGoodDirection.add(unusedDirection); 
        }
    }
    
    public CoordWithUnusedDir(List<Integer> unusedGoodDirections, int[] currentCoord) {
        super();
        this.unusedGoodDirection = unusedGoodDirections;
        this.currentCoord = new int[2];
        this.currentCoord[0] = currentCoord[0];
        this.currentCoord[1] = currentCoord[1];
    }
    
    public CoordWithUnusedDir(Integer unusedGoodDirection, int[] currentCoord) {
        
        
        this.unusedGoodDirection = new ArrayList<Integer>();
        this.unusedGoodDirection.add(unusedGoodDirection);
        this.currentCoord = new int[2];
        this.currentCoord[0] = currentCoord[0];
        this.currentCoord[1] = currentCoord[1];
    }

    public List<Integer> getUnusedGoodDirection() {
        return unusedGoodDirection;
    }

    public int[] getCurrentCoord() {
        return currentCoord;
    }

    public void setUnusedGoodDirection(List<Integer> unusedGoodDirection) {
        this.unusedGoodDirection = unusedGoodDirection;
    }

    public void setCurrentCoord(int[] currentCoord) {
        this.currentCoord = currentCoord;
    }
}
