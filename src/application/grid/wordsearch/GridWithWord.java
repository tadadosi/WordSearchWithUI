package application.grid.wordsearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GridWithWord {
	
	private Grid grid;
	private String wordToWrite;
	private int[] currentCoord;
	List<Integer> unusedGoodDirection;
	
	public GridWithWord(Grid grid, String wordToWrite, int[] currentCoord, List<Integer> goodDirection) {
		this.grid = grid;
		this.wordToWrite = wordToWrite;
		this.currentCoord = currentCoord;
		this.unusedGoodDirection = goodDirection;
	}
	
	public GridWithWord(GridWithWord otherGrid) {
		this.grid = new Grid(otherGrid.getGrid());
		this.wordToWrite = otherGrid.getWordToWrite();
		this.currentCoord = new int[otherGrid.getCurrentCoord().length];
		System.arraycopy(otherGrid.getCurrentCoord(), 0, this.currentCoord, 0 , otherGrid.getCurrentCoord().length);
		this.unusedGoodDirection = new ArrayList<Integer>();
		for (Integer unusedDirection : otherGrid.getUnusedGoodDirection() ) {
		    unusedGoodDirection.add(unusedDirection); 
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

	public int[] getCurrentCoord() {
		return currentCoord;
	}

	public void setCurrentCoord(int[] currentCoord) {
		this.currentCoord = currentCoord;
	}

	public List<Integer> getUnusedGoodDirection() {
		return unusedGoodDirection;
	}

	public void setUnusedGoodDirection(List<Integer> unusedGoodDirection) {
		this.unusedGoodDirection = unusedGoodDirection;
	}
	
	

	
	
}
