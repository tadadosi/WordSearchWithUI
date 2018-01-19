package application.grid.wordsearch;

import java.util.ArrayList;
import java.util.List;

public class Grid{
	Cell[][] grid;
	
	
	public Grid(Grid otherGridObj) {
		Cell[][] otherGrid = otherGridObj.getGrid();
		int rowNum = otherGrid.length;
		int colNum = otherGrid[0].length;
		grid = new Cell[rowNum][colNum];
		for (int row = 0; row < otherGrid.length; row++) {
			for (int col = 0; col < otherGrid[0].length; col++) {
				grid[row][col] = new Cell(otherGrid[row][col]);
			}
		}
	}
	
	public Grid(int gridSizeX, int girdSizeY) {
		grid = new Cell[gridSizeX][girdSizeY];
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				grid[row][col] = new Cell();
				grid[row][col].setId(row * grid[0].length + col);
			}
		}
	}

	public Grid() {
	}
	
	public void printGrid() {
		System.out.println("Grid");
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				if (grid[row][col].getLetter() != '\0') {
					System.out.print(grid[row][col].getLetter() + " ");
				} else {
					System.out.print("_" + " ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	// return number of empty cell for each area 
	public int[] getAreaIdArray() {
		int maxAreaId = 0;
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				if (maxAreaId < grid[row][col].getAreaId())
					maxAreaId = grid[row][col].getAreaId();
			}
		}
		
		int[] areaIds = new int[maxAreaId + 1];
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				if (grid[row][col].getAreaId() >= 0)
					areaIds[grid[row][col].getAreaId()]++;
			}
		}
		return areaIds;
	}

	// identify separate areas on the grid
	public int initiateAreas() {
		List<Integer> cellIdList = new ArrayList<Integer>();
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				if (grid[row][col].getLetter() != '\0') {
					grid[row][col].setAreaId(-1);
				} else {
					cellIdList.add(grid[row][col].getId());
				}
			}
		}

		int areaId = 0;
		while (cellIdList.size() > 0) {
			grid[(int) (cellIdList.get(0) / grid[0].length)][cellIdList.get(0) % grid[0].length].setAreaId(areaId);
			List<Integer> root = new ArrayList<Integer>();
			root.add(cellIdList.get(0));
			cellIdList.remove(0);
			for (int i = 0; i < root.size(); i++) {
				for (int j = 0; j < cellIdList.size(); j++) {
					if (root.get(i) + 1 == cellIdList.get(j) && (root.get(i) + 1) % grid[0].length != 0 || root.get(i) - 1 == cellIdList.get(j) && root.get(i) % grid[0].length != 0
							|| root.get(i) + grid[0].length == cellIdList.get(j) || root.get(i) - grid[0].length == cellIdList.get(j)) {
						grid[(int) (cellIdList.get(j) / grid[0].length)][cellIdList.get(j) % grid[0].length].setAreaId(areaId);
						root.add(cellIdList.get(j));
						cellIdList.remove(cellIdList.get(j));
					}
				}
			}
			areaId++;
		}
		return areaId;
	}

	public void printId() {
		System.out.println("Id");
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				if (grid[row][col].getId() < 10)
					System.out.print("  " + grid[row][col].getId());
				else
					System.out.print(" " + grid[row][col].getId());
			}
			System.out.println();
		}
		System.out.println();
	}

	public void printAreaId() {
		System.out.println("Area Id");
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				if (grid[row][col].getAreaId() < 0)
					System.out.print(" " + grid[row][col].getAreaId());
				else if (grid[row][col].getAreaId() < 10)
					System.out.print("  " + grid[row][col].getAreaId());
				else
					System.out.print(" " + grid[row][col].getAreaId());
			}
			System.out.println();
		}
		System.out.println();
	}

	public void setLetter(int[] coord, char letter) {
		grid[coord[0]][coord[1]].setLetter(letter);
	}
	
	public void setNextDirection(int[] coord, int direction) {
        grid[coord[0]][coord[1]].setNextDirection(direction);
    }
	public void setWordStartDirection(int[] coord, int direction) {
        grid[coord[0]][coord[1]].setWordStartDirection(direction);;
    }
	
	public boolean isSameDirection(int[] coord, int direction) {
        if (grid[coord[0]][coord[1]].getNextDirection() == direction && grid[coord[0]][coord[1]].isSameDirection()) {
            return true;
        }
        return false;
    }
	
    public void setSameDirection(int[] coord, boolean isSameDirection) {
        grid[coord[0]][coord[1]].setSameDirection(isSameDirection);
    }
	
	
	// check which direction you can snake
	public List<Integer> determineDirection(int[] coord) {
		List<Integer> direction = new ArrayList<Integer>();
		
		if (coord[1] + 1 < grid[0].length && grid[coord[0]][coord[1] + 1].getLetter() == '\0')
			direction.add(1);
		if (coord[0] + 1 < grid.length && grid[coord[0] + 1][coord[1]].getLetter() == '\0')
			direction.add(2);
		if (coord[1] - 1 >= 0 && grid[coord[0]][coord[1] - 1].getLetter() == '\0')
			direction.add(-3);
		if (coord[0] - 1 >= 0 && grid[coord[0] - 1][coord[1]].getLetter() == '\0')
			direction.add(-4);
		if (direction.size() == 0)
			direction.add(0);
		return direction;
	}
	
	// check which direction you can snake
    public List<Integer> determineDirectionWithoutZero(int[] coord) {
        List<Integer> direction = new ArrayList<Integer>();
        
        if (coord[1] + 1 < grid[0].length && grid[coord[0]][coord[1] + 1].getLetter() == '\0')
            direction.add(1);
        if (coord[0] + 1 < grid.length && grid[coord[0] + 1][coord[1]].getLetter() == '\0')
            direction.add(2);
        if (coord[1] - 1 >= 0 && grid[coord[0]][coord[1] - 1].getLetter() == '\0')
            direction.add(-3);
        if (coord[0] - 1 >= 0 && grid[coord[0] - 1][coord[1]].getLetter() == '\0')
            direction.add(-4);
        return direction;
    }
	
	
	// return coordinates for specific areaId
	public List<int[]> coordByAreaId(int areaId) {
		List<int[]> coordList = new ArrayList<int[]>();
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				if(grid[row][col].getAreaId() == areaId && grid[row][col].getLetter() == '\0') {
					int[] coord = {row, col};
					coordList.add(coord);
				}
			}
		}
		return coordList;
	}
	
	public List<int[]> allCoords() {
	    List<int[]> coordList = new ArrayList<int[]>();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if( grid[row][col].getLetter() == '\0') {
                    int[] coord = {row, col};
                    coordList.add(coord);
                }
            }
        }
        return coordList;
    }
	
	public List<int[]> coordByEmpty() {
		List<int[]> coordList = new ArrayList<int[]>();
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				if(grid[row][col].getLetter() == '\0') {
					int[] coord = {row, col};
					coordList.add(coord);
				}
			}
		}
		return coordList;
	}

	public Cell[][] getGrid() {
		return grid;
	}

	public void setGrid(Cell[][] grid) {
		this.grid = grid;
	}
	
	public boolean isLURTreeValid(int[] coord) {
	    if (isLeftValid(coord) && isUpValid(coord) && isUpleftValid(coord))
	        return true;
	    
	    return false;
	}
	
	public boolean isLDRTreeValid(int[] coord) {
	    if (isLeftValid(coord) && isDownValid(coord) && isDownLeftValid(coord))
            return true;
        
        return false;
    }
	
	public boolean isRULTreeValid(int[] coord) {
	    if (isRightValid(coord) && isUpValid(coord) && isUpRightValid(coord))
            return true;
        
        return false;
    }
	
    public boolean isRDLTreeValid(int[] coord) {
        if (isRightValid(coord) && isDownValid(coord) && isDownRightValid(coord))
            return true;

        return false;
    }
	
    public boolean isUpValid(int[] coord) {
	    //UP
        if (coord[0] - 1 < 0 ||  grid[coord[0]-1][coord[1]].getLetter() != '\0')
            return false;
        
        return true;
	}
	
	public boolean isDownValid(int[] coord) {
        //DOWN
	    if (coord[0] + 1 >= 0 ||  grid[coord[0]+1][coord[1]].getLetter() != '\0')
            return false;
        
        return true;
    }
	
	public boolean isLeftValid(int[] coord) {
        //LEFT
	    if (coord[1] - 1 < 0 || grid[coord[0]][coord[1] - 1].getLetter() != '\0')
            return false;

        return true;
	}
	
	public boolean isRightValid(int[] coord) {
        //RIGHT
	    if (coord[1] + 1 >= grid[0].length || grid[coord[0]][coord[1]+1].getLetter() != '\0')
            return false;

        return true;
    }
	
	public boolean isUpRightValid(int[] coord) {
	    //UP-RIGHT DIAGONAL
        if (coord[0] - 1 < 0  || coord[1] + 1 >= grid[0].length || grid[coord[0]-1][coord[1]+1].getLetter() != '\0')
            return false;
        
        return true;
	}
	
	public boolean isUpleftValid(int[] coord) {
        // UP-LEFT DIAGONAL
        if (coord[0] - 1 < 0 || coord[1] - 1 < 0 || grid[coord[0] - 1][coord[1] - 1].getLetter() != '\0')
            return false;

        return true;
    }
    
    public boolean isDownLeftValid(int[] coord) {
        // DOWN-LEFT DIAGONAL
        if (coord[0] + 1 >= grid.length || coord[1] - 1 < 0 || grid[coord[0] + 1][coord[1] - 1].getLetter() != '\0')
            return false;

        return true;
    }
	
    public boolean isDownRightValid(int[] coord) {
        // DOWN-LEFT DIAGONAL
        if (coord[0] + 1 >= grid.length || coord[1] + 1 >= grid[0].length || grid[coord[0] + 1][coord[1] + 1].getLetter() != '\0')
            return false;

        return true;
    }
    
    public boolean isLeftLeftValid(int[] coord) {
        if (!isLeftValid(coord))
            return false;
        
        int goodDirection = -3;
        int[] tempCoord = new int[2];
        tempCoord[0] = coord[0] + (goodDirection + 1) % 2;
        tempCoord[1] = coord[1] + goodDirection % 2;
        
        if (!isLeftValid(tempCoord))
            return false;
        
        return true;
    }
    
    public boolean isRightRightValid(int[] coord) {
        if (!isLeftValid(coord))
            return false;
        
        int goodDirection = 1;
        int[] tempCoord = new int[2];
        tempCoord[0] = coord[0] + (goodDirection + 1) % 2;
        tempCoord[1] = coord[1] + goodDirection % 2;
        
        if (!isLeftValid(tempCoord))
            return false;
        
        return true;
    }
}
