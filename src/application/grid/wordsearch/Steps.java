
package application.grid.wordsearch;

import java.util.ArrayList;
import java.util.List;

import application.grid.config.WordConfig;

public class Steps {

	public static GridWithWord enterWordInGrid(Grid gridObj, int[] coord, String word, WordConfig config) {
		Grid grid = new Grid(gridObj);
		List<Integer> goodDirection = grid.determineDirection(coord);
		List<GridWithWord> tempGridList = new ArrayList<GridWithWord>();

		// First add currect situation
		GridWithWord tempGrid = new GridWithWord(grid, word, coord, goodDirection);
		tempGridList.add(tempGrid);
		GridWithWord nextGrid = null;

		for (int i = 0; i < word.length() + config.getInfoCellsNumber(); i++) {
			// if we removed all tempGridList that means there is no way to add word
			if (tempGridList.size() == 0)
				return null;

			boolean added = false;
			GridWithWord lastGrid = tempGridList.get(tempGridList.size() - 1);
			List<Integer> lastUnusedDirections = lastGrid.getUnusedGoodDirection();

			while (!added && lastUnusedDirections != null && lastUnusedDirections.size() > 0
					&& lastUnusedDirections.get(0) != 0) {

				// Lets construct new tree of possibilities
				nextGrid = moveOneStep(lastGrid, config, i);

				// If there are at least 1 good direction and it is not last letter
				if (nextGrid != null) {
					tempGridList.add(nextGrid);
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

	private static GridWithWord moveOneStep(GridWithWord lastStepGridObj,  WordConfig config, int letterIndex) {
		GridWithWord lastStepGrid = new GridWithWord(lastStepGridObj);
		int[] coord = lastStepGrid.getCurrentCoord();
		Grid grid = lastStepGrid.getGrid();
		System.out.println("Grid before enterng Letter");
		System.out.println("current coords: " + coord[0] + ":" + coord[1]);
		grid.printGrid();
		String word = lastStepGrid.getWordToWrite();
		
		//Before entering word add empty description cells. Lets set description true in those cells
		// if config.getInfoCellsNumber() == 0 we do not do anything
		if (letterIndex == 0) {
			if (config.getInfoCellsNumber() == 1) {
				
			} else if (config.getInfoCellsNumber() == 2) {

			} else if (config.getInfoCellsNumber() == 3) {

			} else if (config.getInfoCellsNumber() == 4) {

			}
		} else {
			
		}
		
		
		
		
		int whichDirection = (int) Math.round(Math.random() * (lastStepGrid.getUnusedGoodDirection().size() - 1));
		int goodDirection = lastStepGrid.getUnusedGoodDirection().get(whichDirection);

		if (goodDirection == 0) {
			return null; // There is no possibile solution from this point
		}

		// Since direction is not 0 lets remove it from previous step since it is
		// already used in our tree
		// Remove it from original object
		lastStepGridObj.getUnusedGoodDirection().remove(whichDirection);
		
		//Lets set on old coord where we went from there
		grid.setNextDirection(coord, goodDirection);
		
		coord[0] += (goodDirection + 1) % 2;
		coord[1] += goodDirection % 2;
		
//		grid.setLastDirection(coord, goodDirection);
		char letter = word.charAt(0);
		grid.setLetter(coord, letter);
		
		System.out.println("Grid after enterng Letter");
		grid.printGrid();
		System.out.println("current coords: " + coord[0] +":"+ coord[1]);
		System.out.println("good directions - " + grid.determineDirection(coord));
		
		//Reset mext direction for this cell in case it was written in the past
		grid.setNextDirection(coord, 0);
		
		
		// Now lets remove first letter from word since we already written it
		word = word.length() > 1 ? word.substring(1) : "";
//		grid.printGrid();
		List<Integer> nextUnusedDirections = grid.determineDirection(coord);
		return new GridWithWord(grid, word, coord, nextUnusedDirections);
		
	}

}
