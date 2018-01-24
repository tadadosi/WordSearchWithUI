package application.grid.wordsearch;

import java.util.ArrayList;
import java.util.List;

public class GridWithWord {
	
	private Grid grid;
	private String wordToWrite;
//	private List<int[]> currentCoord;
//	List<Integer> unusedGoodDirection;
	private List<CoordWithUnusedDir> cellsUnusedDirections;
	
	public GridWithWord(Grid grid, String wordToWrite, List<CoordWithUnusedDir> emptyCellsUnusedDirections) {
		this.grid = grid;
		this.wordToWrite = wordToWrite;
		
		this.cellsUnusedDirections = new ArrayList<CoordWithUnusedDir>();
		for (CoordWithUnusedDir cwud : emptyCellsUnusedDirections) {
		    this.cellsUnusedDirections.add(cwud);
		}
	}
	
	public GridWithWord(Grid grid, String wordToWrite, CoordWithUnusedDir emptyCellsUnusedDirection) {
        this.grid = grid;
        this.wordToWrite = wordToWrite;
        
        this.cellsUnusedDirections = new ArrayList<CoordWithUnusedDir>();
        this.cellsUnusedDirections.add(new CoordWithUnusedDir(emptyCellsUnusedDirection));
    }
	
	
	public GridWithWord(GridWithWord otherGrid) {
        this.grid = new Grid(otherGrid.getGrid());
        this.wordToWrite = otherGrid.getWordToWrite();
        
        this.cellsUnusedDirections = new ArrayList<CoordWithUnusedDir>();
        for (CoordWithUnusedDir cellWUnusedDirection : otherGrid.getCellsUnusedDirections()) {
            this.cellsUnusedDirections.add(new CoordWithUnusedDir(cellWUnusedDirection)); 
        }
    }
	
	
	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public String getWordToWrite() {
		return wordToWrite;
	}

	public void setWordToWrite(String wordToWrite) {
		this.wordToWrite = wordToWrite;
	}


    public List<CoordWithUnusedDir> getCellsUnusedDirections() {
        return cellsUnusedDirections;
    }


    public void setCellsUnusedDirections(List<CoordWithUnusedDir> cellsUnusedDirections) {
        this.cellsUnusedDirections = cellsUnusedDirections;
    }
    
    public List<Integer> getUnusedGoodDirection() {
        
        if (cellsUnusedDirections != null && cellsUnusedDirections.size() > 0) {
            return cellsUnusedDirections.get(0).getUnusedGoodDirection();
        } else {
            System.out.println(cellsUnusedDirections);
            return null;
        }
    }
    
    public int[] getCurrentCoord() {
        if (cellsUnusedDirections != null && cellsUnusedDirections.size() > 0) {
            return cellsUnusedDirections.get(0).getCurrentCoord();
        } else {
            System.out.println(cellsUnusedDirections);
            return null;
        }
    }
}
