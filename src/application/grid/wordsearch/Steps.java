
package application.grid.wordsearch;

import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

public class Steps {
    
    Grid gridObj;
    String word;
    int[] coord;
    GridWithWord tempGrid;
    GridWithWord lastGrid;
    
    
	public Steps(Grid gridObj, String word, int[] coord) {
        this.gridObj = gridObj;
        this.word = word;
        this.coord = coord;
        
    }
    public GridWithWord enterWordInGrid() {
	    Grid grid = new Grid(gridObj);
		List<Integer> goodDirection = grid.determineDirection(coord);
		List<GridWithWord> tempGridList = new ArrayList<GridWithWord>();
		
		// First add current situation
		lastGrid = new GridWithWord(grid, word, new CoordWithUnusedDir(goodDirection, coord));
		tempGridList.add(lastGrid);
		
		for (int i = 0; i < word.length(); i++) {
			// if we removed all tempGridList that means there is no way to add word
			if (tempGridList.size() == 0)
				return null;

			boolean added = false;
			lastGrid = tempGridList.get(tempGridList.size() - 1);
			List<Integer> lastUnusedDirections = lastGrid.getUnusedGoodDirection();

			while (!added && lastUnusedDirections != null && lastUnusedDirections.size() > 0
					&& lastUnusedDirections.get(0) != 0) {

				// Lets construct new tree of possibilities
			    tempGrid = moveOneStep();
			    
				// If there are at least 1 good direction and it is not last letter
				if (tempGrid != null) {
					tempGridList.add(tempGrid);
					added = true;
				}
			}

			if (!added) {
				// Lets remove last grid because it is impossible to continue from this point
				// and lets get one step back
				tempGridList.remove(lastGrid);
				
				i = i - 2;
			}
			
		}
		
//		for (GridWithWord temp : tempGridList) {
//			temp.getGrid().printGrid();
//		}
		
		
		return tempGridList.get(tempGridList.size() - 1);
	}

	private GridWithWord moveOneStep() {
	    coord[0] = lastGrid.getCurrentCoord()[0];
	    coord[1] = lastGrid.getCurrentCoord()[1];
	    boolean firstLetter = false;
		Grid grid = new GridWithWord(lastGrid).getGrid();
		
//	    System.out.print("START STEP ---------------------");
//		grid.printGrid();
		
		String tempWord = lastGrid.getWordToWrite();
		
		int goodDirection = findGoodDirection();
		
		if (goodDirection == 0) {
			return null; // There is no possibile solution from this point
		}
		
		if (word.equals(tempWord)) {
		    grid.setSameDirection(coord, true);
		    firstLetter = true;
		} else {
		    if (lastGrid.getGrid().isSameDirection(coord, goodDirection)) {
	            if (tempWord == null || tempWord.length() <= 1) {
	                goodDirection = findGoodDirectionForLastLetter();
	                if (goodDirection == 0) {
	                    return null; // There is no possibile solution from this point
	                }
	                grid.setSameDirection(coord, false);
	            } else {
	                grid.setSameDirection(coord, true);
	            }
	        } else {
	            grid.setSameDirection(coord, false); 
	        } 
		}
		
		
		//Lets set on old coord where we went from there
		grid.setNextDirection(coord, goodDirection);
		
		if (!firstLetter)
		    getNewLetterCoords(goodDirection);
		
//		grid.setLastDirection(coord, goodDirection);
		char letter = tempWord.charAt(0);
		grid.setLetter(coord, letter);
		
		//Reset next direction for this cell in case it was written in the past
		if (tempWord.length() == 1) {
		    grid.setNextDirection(coord, 0);
		} else {
		    grid.setNextDirection(coord, goodDirection);
		}
		    
		
		
		// Now lets remove first letter from word since we already written it
		tempWord = tempWord.length() > 1 ? tempWord.substring(1) : "";
//		grid.printGrid();
		List<Integer> nextUnusedDirections = grid.determineDirection(coord);
		
		
//		 System.out.print("END STEP ---------------------");
//	     grid.printGrid();
		
		return new GridWithWord(grid, tempWord, new CoordWithUnusedDir(nextUnusedDirections, coord));
		
	}
	
	private int findGoodDirection() {
	    int whichDirection = (int) Math.round(Math.random() * (lastGrid.getUnusedGoodDirection().size() - 1));
        int goodDirection = lastGrid.getUnusedGoodDirection().get(whichDirection);
        // Since direction is not 0 lets remove it from previous step since it is
        // already used in our tree
        // Remove it from original object
        if (goodDirection != 0) {
          lastGrid.getUnusedGoodDirection().remove(whichDirection);
        }
        
        return goodDirection;
	}
	
	private int findGoodDirectionForLastLetter() {
	    int index = 0;
	    List<Integer> viableDirections = new ArrayList<>();
	    for (int direction : lastGrid.getUnusedGoodDirection()) {
	        if (!lastGrid.getGrid().isSameDirection(coord, direction) && direction != 0)
	            viableDirections.add(direction);
	        else {
	            lastGrid.getUnusedGoodDirection().remove(index);
	        }
	        index++;
	    }
	    if (viableDirections != null && viableDirections.size() > 0) {
	        int whichDirection = (int) Math.round(Math.random() * (viableDirections.size() - 1));
	       return viableDirections.get(whichDirection);
	    }
	    
        return 0;
    }
	
	private void getNewLetterCoords(int goodDirection) {
	    coord[0] += (goodDirection + 1) % 2;
        coord[1] += goodDirection % 2;
	}
	
	// private static GridWithWord addFirstLetter(GridWithWord lastStepGridObj) {
//  GridWithWord lastStepGrid = new GridWithWord(lastStepGridObj);
//    
//  int[] coord = lastStepGrid.getCurrentCoord();
//    Grid grid = lastStepGrid.getGrid();
//    String word = lastStepGrid.getWordToWrite();
//
//    char letter = word.charAt(0);
//    
//    switch (letter) {
//    case '1' : 
//        //enter 1 empty cell in grid
//        break;
//    case '2' :
//      //enter 2 empty cell in grid
//        break;
//    case '3' :
//      //enter 3 empty cell in grid
//        break;
//    case '4' :
//      //enter 4 empty cell in grid
//        break;
//    default: break;
//    }
//    List<Integer> nextUnusedDirections = grid.determineDirection(coord);
//    return new GridWithWord(grid, word, new CoordWithUnusedDir(nextUnusedDirections, coord));
//}
}
