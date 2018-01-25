package application.grid.wordsearch;

public class Cell {
	private int id;
	private char letter;
	private int areaId;
	private int nextDirection;
	private int lastDirection;
	private boolean sameDirection;
	private int wordStartDirection;
	private int wordIndex;
	
	
	public Cell() {
	    sameDirection = true;
	    wordStartDirection = 0;
	}
	public Cell(Cell other) {
		this.id = other.id;
		this.letter = other.letter;
		this.areaId = other.areaId;
		this.nextDirection = other.nextDirection;
		this.sameDirection = other.sameDirection;
		this.wordStartDirection = other.wordStartDirection;
		this.wordIndex = other.wordIndex;
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
    public void setNextDirection(int nextDirection) {
        this.nextDirection = nextDirection;
    }
    public boolean isSameDirection() {
        return sameDirection;
    }
    public void setSameDirection(boolean sameDirection) {
        this.sameDirection = sameDirection;
    }
    public int getWordStartDirection() {
        return wordStartDirection;
    }
    public void setWordStartDirection(int wordStartDirection) {
        this.wordStartDirection = wordStartDirection;
    }
    public int getWordIndex() {
        return wordIndex;
    }
    public void setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
    }
    public int getLastDirection() {
        return lastDirection;
    }
    public void setLastDirection(int lastDirection) {
        this.lastDirection = lastDirection;
    }
    
}
