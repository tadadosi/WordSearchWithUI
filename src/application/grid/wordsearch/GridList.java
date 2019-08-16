package application.grid.wordsearch;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GridList {
	private List<Cell> gridList = new ArrayList<Cell>();
	private int colN;
	private int rowN;
	private double lineWidth;
	private double cellSize;
	
	public List<Cell> getGridList() {
		return gridList;
	}

	public void setGridList(List<Cell> gridList) {
		this.gridList = gridList;
	}
	
	public void addGridElement(Cell cell) {
		gridList.add(cell);
	}

	public int getColN() {
		return colN;
	}

	public void setColN(int colN) {
		this.colN = colN;
	}

	public int getRowN() {
		return rowN;
	}

	public void setRowN(int rowN) {
		this.rowN = rowN;
	}

	public double getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(double lineWidth) {
		this.lineWidth = lineWidth;
	}

	public double getCellSize() {
		return cellSize;
	}

	public void setCellSize(double cellSize) {
		this.cellSize = cellSize;
	}
	
}
