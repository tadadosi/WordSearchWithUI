package application.grid.config;

public class WordConfig {
	public static final int GRID_10 = 10;
	public static final int GRID_20 = 20;
	public static final int GRID_30 = 30;
	public static final int GRID_40 = 40;
	
	public static final int INFO_CELLS_0 = 0;
	public static final int INFO_CELLS_1 = 1;
	public static final int INFO_CELLS_2 = 2;
	public static final int INFO_CELLS_4 = 4;
	
	private int gridSizeX = GRID_10;
	
	private int gridSizeY = GRID_10;
	
	private int infoCellsNumber = 0;
	
	
    public WordConfig(int gridSizeX, int gridSizeY, int infoCellsNumber) {
        super();
        this.gridSizeX = gridSizeX;
        this.gridSizeY = gridSizeY;
        this.infoCellsNumber = infoCellsNumber;
    }
	public int getInfoCellsNumber() {
		return infoCellsNumber;
	}
	public void setInfoCellsNumber(int infoCellsNumber) {
		this.infoCellsNumber = infoCellsNumber;
	}
    public int getGridSizeX() {
        return gridSizeX;
    }
    public int getGridSizeY() {
        return gridSizeY;
    }
    public void setGridSizeX(int gridSizeX) {
        this.gridSizeX = gridSizeX;
    }
    public void setGridSizeY(int gridSizeY) {
        this.gridSizeY = gridSizeY;
    }
	
	
}
