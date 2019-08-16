package application.grid.wordsearch;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Cell {
	private int id;
	private char letter;
	private String letterStr;
	private int areaId;
	private int nextDirection;
	private int lastDirection;
	private boolean sameDirection;
	private int wordStartDirection;
	private int wordIndex;
	private int rowNumber;
	private int colNumber;
	
	
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
	
	@XmlAttribute
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	@XmlAttribute
	public char getLetter() {
		return letter;
	}
	
	public void setLetter(char letter) {
		this.letter = letter;
	}
	@XmlAttribute
	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	@XmlAttribute
    public int getNextDirection() {
        return nextDirection;
    }
    public void setNextDirection(int nextDirection) {
        this.nextDirection = nextDirection;
    }
    @XmlAttribute
    public boolean isSameDirection() {
        return sameDirection;
    }
    public void setSameDirection(boolean sameDirection) {
        this.sameDirection = sameDirection;
    }
    @XmlAttribute
    public int getWordStartDirection() {
        return wordStartDirection;
    }
    public void setWordStartDirection(int wordStartDirection) {
        this.wordStartDirection = wordStartDirection;
    }
    @XmlAttribute
    public int getWordIndex() {
        return wordIndex;
    }
    public void setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
    }
    @XmlAttribute
    public int getLastDirection() {
        return lastDirection;
    }
    public void setLastDirection(int lastDirection) {
        this.lastDirection = lastDirection;
    }
    @XmlAttribute
	public int getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}
	@XmlAttribute
	public int getColNumber() {
		return colNumber;
	}
	public void setColNumber(int colNumber) {
		this.colNumber = colNumber;
	}
	@XmlAttribute
	public String getLetterStr() {
		return letterStr;
	}
	
	public void setLetterStr(String letterStr) {
		this.letterStr = letterStr;
	}
    
}
