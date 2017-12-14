package application.grid.wordsearch;

public class Cell {
	private int id;
	private char letter;
	private int areaId;
	private int nextDirection;
	private int lastDirection;
	
	public Cell() {
	}
	public Cell(Cell other) {
		this.id = other.id;
		this.letter = other.letter;
		this.areaId = other.areaId;
		this.nextDirection = other.nextDirection;
		this.lastDirection = other.lastDirection;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public char getLetter() {
		return letter;
	}
	
	public void setLetter(char letter) {
		this.letter = letter;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
    public int getNextDirection() {
        return nextDirection;
    }
    public int getLastDirection() {
        return lastDirection;
    }
    public void setNextDirection(int nextDirection) {
        this.nextDirection = nextDirection;
    }
    public void setLastDirection(int lastDirection) {
        this.lastDirection = lastDirection;
    }
	
}
