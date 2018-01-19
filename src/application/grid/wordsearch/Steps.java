
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
    
    public static final char DESCR_SYMBOL = '#';
    
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
		
		char firstLetter = word.charAt(0);
		
		if (firstLetter == '1' || firstLetter == '2' || firstLetter == '3' || firstLetter == '4') {
		    lastGrid = addAllDescriptionCells(Character.getNumericValue(firstLetter));
		    word = word.length() > 1 ? word.substring(1) : "";
		}
		
		if (lastGrid == null)
		    return null;
		
		tempGridList.add(lastGrid);
		
		for (CoordWithUnusedDir possibleStart : lastGrid.getCellsUnusedDirections()) {
		    
		}
		
		
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
		
		
		//Do not allow word to go straight line. If word is straight in last letter and there is no other option we return null to roll back
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
	
    private GridWithWord addAllDescriptionCells(int numberOfCells) {
        coord[0] = lastGrid.getCurrentCoord()[0];
        coord[1] = lastGrid.getCurrentCoord()[1];
        GridWithWord resultGrid;
     // enter 1 empty cell in grid
        if ("1".equals(numberOfCells)) {
            resultGrid = addOneDescriptionCell(lastGrid.getGrid());
         // enter 2 empty cell in grid  
        } else {
            resultGrid = addDescriptionXCells(lastGrid.getGrid(), numberOfCells);
        }
        return resultGrid;
    }
    
    private GridWithWord addOneDescriptionCell(Grid grid) {
        List<CoordWithUnusedDir> nextUnusedCoords = new ArrayList<>();
        Grid tempGrid = new Grid(lastGrid.getGrid());
        tempGrid.setLetter(coord, '#');
        List<Integer> unusedDirections = tempGrid.determineDirectionWithoutZero(coord);
        
        if (unusedDirections.size() == 0)
            return null;
                    
        nextUnusedCoords.add(new CoordWithUnusedDir(unusedDirections, coord));
        return new GridWithWord(tempGrid, word, nextUnusedCoords);
    }
    
    //Cell number possible: 2, 3 , 4
    private GridWithWord addDescriptionXCells(Grid grid, int cellNumber) {
        List<CoordWithUnusedDir> nextUnusedCoords = new ArrayList<>();
        Grid tempGrid = new Grid(lastGrid.getGrid());
        List<DescrDirections> posibilitiesTree = new ArrayList<>();
        
        switch (cellNumber) {
            case 2:
                posibilitiesTree = findViableRoutes2Cells(tempGrid, coord);
                break;
            case 3:
                posibilitiesTree = findViableRoutes3Cells(tempGrid, coord);
                break;
            case 4:
                posibilitiesTree = findViableRoutes4Cells(tempGrid, coord);
                break;    
            default:
                break;
        }
        
        
        if (posibilitiesTree.size() == 0)
            return null;
        
        for (DescrDirections treeBranch : posibilitiesTree) {
            List<Integer> directions = treeBranch.getDirectionsPossibility();
            Grid tempGrid1 = new Grid(tempGrid);
            List<CoordWithUnusedDir> tempUnusedDir = new ArrayList<>();
            
            int[] tempCoord = new int[2];
            tempCoord[0] = coord[0];
            tempCoord[1] = coord[1];
            
            
            //First we need to add description cell symbol '#' to root cell and every cell of directions
            
            //Add to root cell
            tempGrid1.setLetter(tempCoord, '#');
            //Now add to all directions also
            for (Integer d : directions) {
                tempCoord[0] += (d + 1) % 2;
                tempCoord[1] += d % 2;
                tempGrid1.setLetter(tempCoord, '#');
            }
            
            //Now lets reset coords and Calculate unused directions and see if there are any possibilities
            tempCoord[0] = coord[0];
            tempCoord[1] = coord[1];
            addUnusedDirIfAvailable(tempGrid1, tempCoord, tempUnusedDir);
            
            for (Integer d : directions) {
                tempCoord[0] += (d + 1) % 2;
                tempCoord[1] += d % 2;
                addUnusedDirIfAvailable(tempGrid1, tempCoord, tempUnusedDir);
            }
            
            if (!tempUnusedDir.isEmpty()) {
                tempGrid = tempGrid1;
                nextUnusedCoords = tempUnusedDir;
                break;
            }
            
        }
        
        if (nextUnusedCoords.isEmpty())
            return null;
                    
        return new GridWithWord(tempGrid, word, nextUnusedCoords);
    }
    
   private void  addUnusedDirIfAvailable(Grid tempGrid, int[] tempCoord, List<CoordWithUnusedDir> tempUnusedDir) {
       List<Integer> unusedDirectionsTemp;
       unusedDirectionsTemp = tempGrid.determineDirectionWithoutZero(tempCoord);
       if (!unusedDirectionsTemp.isEmpty()) {
           tempUnusedDir.add(new CoordWithUnusedDir(unusedDirectionsTemp, tempCoord));
       }
   }
    
    
    private List<DescrDirections> findViableRoutes2Cells(Grid grid, int[] coords) {
        List<DescrDirections> resulTreeList = new ArrayList<>();

        // L direction
        if (grid.isLeftLeftValid(coords)) {
            List<Integer> lTree = new ArrayList<>();
            lTree.add(-3);
            resulTreeList.add(new DescrDirections(lTree));
        }
        // R direction
        if (grid.isLeftLeftValid(coords)) {
            List<Integer> rTree = new ArrayList<>();
            rTree.add(1);
            resulTreeList.add(new DescrDirections(rTree));
        }
        return resulTreeList;
    }
    
    private List<DescrDirections> findViableRoutes3Cells(Grid grid, int[] coords) {
        // There are 2 possible scenarios for 3 description cells:
        // L - left, R - right, U - up, D - down then possibilities is
        // LL, RR . Lets check each of these seperatly without
        // counting unused direction (will do later)
        // Direction wise it would look like : (-3, -3) , (1, 1)
        List<DescrDirections> resulTreeList = new ArrayList<>();
        
        // LL direction
        if (grid.isLeftLeftValid(coords)) {
            List<Integer> llTree = new ArrayList<>();
            llTree.add(-3);
            llTree.add(-3);
            resulTreeList.add(new DescrDirections(llTree));
        }

        // RR direction
        if (grid.isRightRightValid(coords)) {
            List<Integer> rrTree = new ArrayList<>();
            rrTree.add(1);
            rrTree.add(1);
            resulTreeList.add(new DescrDirections(rrTree));
        }
        
        return resulTreeList;
    }
    
    private List<DescrDirections> findViableRoutes4Cells(Grid grid, int[] coords) {
        //There are 4 possible scenarios for 4 description cells:
        // L - left, R - right, U - up, D - down then possibilities is
        // LUR, LDR, RUL, RDL . Lets check each of these seperatly without counting unused direction (will do later)
        // Direction wise it would look like : (-3, -4, 1) , (-3, 2, 1), (1, 2, -3), (1, -4, -3)
        List<DescrDirections> resulTreeList = new ArrayList<>();
        
        //LUR direction
        if (grid.isLURTreeValid(coords)) {
            List<Integer> lurTree = new ArrayList<>();
            lurTree.add(-3);
            lurTree.add(-4);
            lurTree.add(1);
            resulTreeList.add(new DescrDirections(lurTree));
        }
        
        // LDR direction
        if (grid.isLDRTreeValid(coords)) {
            List<Integer> ldrTree = new ArrayList<>();
            ldrTree.add(-3);
            ldrTree.add(2);
            ldrTree.add(1);
            resulTreeList.add(new DescrDirections(ldrTree));
        }

        // RUL direction
        if (grid.isLDRTreeValid(coords)) {
            List<Integer> rulTree = new ArrayList<>();
            rulTree.add(1);
            rulTree.add(2);
            rulTree.add(-3);
            resulTreeList.add(new DescrDirections(rulTree));
        }
        
     // RDL direction
        if (grid.isLDRTreeValid(coords)) {
            List<Integer> rdlTree = new ArrayList<>();
            rdlTree.add(1);
            rdlTree.add(-4);
            rdlTree.add(-3);
            resulTreeList.add(new DescrDirections(rdlTree));
        }
        return resulTreeList;
    }
    
}
