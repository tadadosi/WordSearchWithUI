package application;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.err.WordError;
import application.grid.config.WordConfig;
import application.grid.utils.Shuffle;
import application.grid.wordsearch.Cell;
import application.grid.wordsearch.Grid;
import application.grid.wordsearch.RunGenerator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class EventHandlingController {
    
    public final static String FILE_TO_SAVE_WORDS = "./words.txt";
    
    @FXML
    private Button generateButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button saveWordsButton;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private TextArea inputWordTextArea;
    @FXML
    private Label errorLabel;
    @FXML
    private Label errorAnswerLabel;
    @FXML
    private Label sizeLabel;
    @FXML
    private Label answerSizeLabel;
    @FXML
    private Slider verticalSlider;
    @FXML
    private Slider horizontalSlider;
    @FXML
    private TextField answerTextField;
    @FXML
    private Canvas answerCanvas;
    @FXML
    private Canvas viewCanvas;
    @FXML
    private CheckBox shuffleAnswerCheckBox;
    @FXML
    private CheckBox colorizeLettersCheckBox;
    @FXML
    private Canvas problemCanvas;
    
    private final static int TILE_SIZE = 30;
    
    private List<String> words;
    private boolean randomLetters;
    private boolean coloredAnswer;
    private int xSize;
    private int ySize;
    
    private int gridSize;
    private int insertedLettersSize;
    private String answer;
    private int descriptionTiles;
    int circleSize;
    int lineWidth;
    public static String newLine = System.getProperty("line.separator");
    /**
     * The constructor (is called before the initialize()-method).
     */
    public EventHandlingController() {
        xSize = 10;
        ySize = 10;
        gridSize = xSize * ySize;
        insertedLettersSize = 0;
        answer = "";
        randomLetters = false;
        coloredAnswer = false;
        descriptionTiles = 0;
        circleSize = 4;
        lineWidth = 1;
    }
    
    @FXML
    private void initialize()  {
        setErrorAnswerLabelValue();
        setAnswerLenLabel();
        stopButton.setDisable(true);
        generateButtonAction();
        getHorizontalSlidersParameter();
        getVerticalSlidersParameter();
        inputWordTextAreaAction();
        answerTextFieldAction();
        closeButtonAction();
        stopButtonAction();
        saveWordsButtonAction();
        shuffleAnswerCheckBoxAction();
        colorizeLettersCheckBoxAction();
        
        answerSizeLabel.setText(new Integer(answer.length()).toString());
        
    }
    
    
    private void generateButtonAction() {
        generateButton.setOnAction((event) -> {
            resetError();
            getWordsFromTextArea();
            handleWarn("Generuoja...");
            int totalLetters = answer.length() + insertedLettersSize;
            if (totalLetters != gridSize) {
                handleError("Ávestas neteisingas raidþiø skaièius!");
            } else {
                generateButton.setDisable(true);
                stopButton.setDisable(false);
//                gridThread = new Thread(() -> calculateGrid());
//                gridThread.start();
                calculateGrid();
                
//                System.out.println(gridThread.getName());
                
            }
        });
    }
    
    
    private void calculateGrid() {
        //xSize slider corresponds to - how many columns and ySize slider - how many rows
        WordConfig config = new WordConfig(ySize,xSize,descriptionTiles);
        RunGenerator gridGenerator = new RunGenerator(config, words, answer);
        Grid gridObj = null;
        try {
            gridObj = gridGenerator.run();
            drawGrid(gridObj);
            handleSuccess("Sugeneravo!");
        } catch (WordError e) {
            handleError(e.getMessage());
        }
        generateButton.setDisable(false);
        stopButton.setDisable(true);
    }
    
    
    private void drawGrid(Grid grid) {
        GraphicsContext gc = answerCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, answerCanvas.getWidth(), answerCanvas.getHeight());
        drawShapes(gc, grid, answerCanvas, true, false);
        
        GraphicsContext vc = viewCanvas.getGraphicsContext2D();
        vc.clearRect(0, 0, viewCanvas.getWidth(), viewCanvas.getHeight());
        drawShapes(vc, grid, viewCanvas, false, false);
        
        GraphicsContext pc = problemCanvas.getGraphicsContext2D();
        pc.clearRect(0, 0, problemCanvas.getWidth(), problemCanvas.getHeight());
        drawShapes(pc, grid, problemCanvas, true, true);
    }
    
    
    private void drawShapes(GraphicsContext gc,Grid gridObj, Canvas canvas, boolean writeText, boolean isProblemCanvas) {
        int cellSize = TILE_SIZE; // Default cell size
        int newYCellSize = cellSize;
        int newXCellSize = cellSize;
        
        if (ySize > 18) {
            newYCellSize = cellSize * 18 / ySize;
        } 
        if (xSize > 26) {
            newXCellSize = cellSize * 26 / xSize; 
        }
        cellSize = Math.min(newYCellSize, newXCellSize);
        
        
        
        Cell[][] grid = gridObj.getGrid();
        
        int colN = grid[0].length;
        int rowN = grid.length;
        
        int gridWidth = colN * cellSize;
        int gridHeight = rowN * cellSize;
        
        double leftPadding = (int) ((canvas.getWidth() - gridWidth)/2);
        int topPadding = (int) (canvas.getHeight() - gridHeight)/2;
        
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(lineWidth);
        
        gc.strokeRect(leftPadding, topPadding, gridWidth, gridHeight);
        
        int gapToLetter = cellSize/2;
        gc.setFont(new Font("Arial", cellSize - 5));
        
        //Draw grid lines
        for (int i=1; i< rowN; i++) {
            double xStart = leftPadding;
            double xEnd = leftPadding + gridWidth;
            
            double yStart = topPadding + cellSize * (i);
            double yEnd = yStart;
            gc.strokeLine(xStart, yStart, xEnd, yEnd);
        }
        
        for (int i=1; i< colN; i++) {
            double xStart = leftPadding + cellSize * (i);
            double xEnd = xStart;
            
            double yStart = topPadding;
            double yEnd = topPadding + gridHeight;
            gc.strokeLine(xStart, yStart, xEnd, yEnd);
            
        }
        
        //Write text to grid and draw lines over text
        String answerToWrite = randomLetters ? Shuffle.shuffle(answer) : answer;
        
        int answerCounter = 0;
        
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        
        for (int i=0; i< rowN; i++) {
            for (int j=0; j< colN; j++) {
                String letter =  Character.toString(grid[i][j].getLetter());
                //Bottom left corner coordinates of cell
                double xBottomLeftCorner = leftPadding + (j * cellSize);
                double yBottomLeftCorner = topPadding + ((i+1) * cellSize);
                if (grid[i][j].getLetter() == '\0' ) {
                    if(coloredAnswer && !isProblemCanvas) {
                        gc.setFill(Color.RED);
                    }
                    if (writeText) {
                        gc.fillText( Character.toString(answerToWrite.charAt(answerCounter)), xBottomLeftCorner + gapToLetter, yBottomLeftCorner - gapToLetter);
                    }
                    answerCounter++;
                    gc.setFill(Color.BLACK);
                } else if (grid[i][j].getLetter() == '#'){
                    gc.setFill(Color.WHITESMOKE);
                    gc.fillRect(xBottomLeftCorner + lineWidth,yBottomLeftCorner - cellSize + lineWidth,cellSize - 2*lineWidth,cellSize - 2*lineWidth);
                    gc.setFill(Color.BLACK);
                    if (grid[i][j].getWordStartDirection() != 0) {
                        switch (grid[i][j].getWordStartDirection()) {
                        case 1:
                            gc.fillOval(xBottomLeftCorner + cellSize - circleSize/2, yBottomLeftCorner - cellSize/2 - circleSize/2, 5, 5);
                            break;
                        case 2:
                            gc.fillOval(xBottomLeftCorner + cellSize/2 - circleSize/2, yBottomLeftCorner - circleSize/2, 5, 5);
                            break;
                        case -3:
                            gc.fillOval(xBottomLeftCorner - circleSize/2, yBottomLeftCorner - cellSize/2 - circleSize/2, 5, 5);
                            break;
                        case -4:
                            gc.fillOval(xBottomLeftCorner + cellSize/2 - circleSize/2, yBottomLeftCorner - cellSize - circleSize/2, 5, 5);
                            break;
                        default:
                            break;
                        }
                    }
                    eraseLines(gc, grid, i, j, xBottomLeftCorner, yBottomLeftCorner, cellSize);
                } else {
                    if (writeText)
                        gc.fillText( letter, leftPadding + (j * cellSize) + gapToLetter, topPadding + ((i+1) * cellSize) - gapToLetter);
                    //Draw line over text
                    if (!isProblemCanvas) {
                        int direction = grid[i][j].getNextDirection();
                        //Need to find middle of the cell to know where to draw line from
                        double xStartLine = xBottomLeftCorner + 0.5 * cellSize;
                        double yStartLine = yBottomLeftCorner - 0.5 * cellSize;
                        
                        double xEndLine = 0;
                        double yEndLine = 0;
                        
                        switch (direction) {
                        case 1: 
                            yEndLine = yStartLine;
                            xEndLine = xStartLine + cellSize;
                            break;
                        case 2:
                            yEndLine = yStartLine + cellSize;
                            xEndLine = xStartLine;
                            break;
                        case -3:
                            yEndLine = yStartLine;
                            xEndLine = xStartLine - cellSize;
                            break;
                        case -4:
                            yEndLine = yStartLine - cellSize;
                            xEndLine = xStartLine;
                            break;
                        default:    
                            yEndLine = yStartLine;
                            xEndLine = xStartLine;
                        }
                        gc.setLineWidth(5);
                        //Transparency
                        gc.setGlobalAlpha(0.2f);
                        gc.strokeLine(xStartLine, yStartLine, xEndLine, yEndLine);
                        gc.setLineWidth(1);
                        gc.setGlobalAlpha(1f);
                    }
                }
                
            }
        }
    }

    private void shuffleAnswerCheckBoxAction() {
        shuffleAnswerCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                Boolean old_val, Boolean new_val) {
                    randomLetters = new_val;
            }
        });
    }
    
    private void colorizeLettersCheckBoxAction() {
        colorizeLettersCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                Boolean old_val, Boolean new_val) {
                    coloredAnswer = new_val;
            }
        });
    }
    
    private void stopButtonAction() {
        stopButton.setOnAction((event) -> {
//            gridThread.interrupt();
            
            generateButton.setDisable(false);
            stopButton.setDisable(true);
        });
    }
    
    private void saveWordsButtonAction() {
        saveWordsButton.setOnAction((event) -> {
            getWordsFromTextArea();
            try {
                handleSuccess("Uþsaugota á words.txt failà");
                writeToFile(FILE_TO_SAVE_WORDS, words);
            } catch (IOException e) {
                handleError("Nepavyko áraðyti 5 failà!");
            }
            
            
        });
    }
    
    private void closeButtonAction() {
        closeButton.setOnAction((event) -> {
            System.out.println("uzdaroma");
            Platform.exit();
            System.exit(0);
            
        });
    }
    private void answerTextFieldAction() {
        answerTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                answer = answerTextField.getText().replaceAll("\\s","");
                setAnswerLenLabel();
                setErrorAnswerLabelValue();
            }
        }); 
        
    }
    
    private void inputWordTextAreaAction() {
        inputWordTextArea.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                String inputText = inputWordTextArea.getText().replaceAll("\\s+","");
                String cleanString = inputText.replaceAll("\r", "").replaceAll("\n\\s+", "");
                insertedLettersSize = cleanString.length();
                //Lets check if there are any numbers which means this will be description empty cells, which take as many cells as number itself. Means we add +1 if its 2 and +2 if 3...
                for (int i = 0; i < cleanString.length(); i++){
                    char c = cleanString.charAt(i);
                    switch (c) {
                    case '2':
                        insertedLettersSize++;
                        break;
                    case '3' :
                        insertedLettersSize += 2;
                        break;
                    case '4' :
                        insertedLettersSize += 3;
                        break;
                    default:
                        break;
                    }
                }
                
                setErrorAnswerLabelValue();
                    
            }
        }); 
    }
    
    
    private void getHorizontalSlidersParameter() {
        horizontalSlider.valueProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                
                    xSize = (int) horizontalSlider.getValue();
                    sizeLabel.setText(xSize + "X" + ySize);
                    gridSize = xSize * ySize;
                    setErrorAnswerLabelValue();
            }
        });
        
    }
    
    private void getVerticalSlidersParameter() {
        verticalSlider.valueProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                
                    ySize = (int) verticalSlider.getValue();
                    sizeLabel.setText(xSize + "X" + ySize);
                    gridSize = xSize * ySize;
                    setErrorAnswerLabelValue();
            }
        });
        
    }
    
    
    private void setErrorAnswerLabelValue() {
        int totalLetters = answer.length() + insertedLettersSize;
        errorAnswerLabel.setText((totalLetters) + "/" + gridSize);
        
        if (totalLetters != gridSize) {
            errorAnswerLabel.setTextFill(Color.RED);
        } else {
            errorAnswerLabel.setTextFill(Color.GREEN);
        }
    }
    
    private void getWordsFromTextArea() {
        if (inputWordTextArea.getText().equals("")) {
            handleError("Input field is empty");
        } else {
            words = getWords(inputWordTextArea.getText().split("\n"));
            System.out.println(words);
        }
    }
    
    private List<String> getWords(String[] wordsArr) {
        List<String> words = new ArrayList<>();
        
        if (wordsArr == null || wordsArr.length == 0) {
            return words;
        }
        
        for (String w : wordsArr) {
            words.add(w.trim().replaceAll("\\s+",""));
        }
        
        return words;
    }
    
    private void handleError(String err) {
        errorLabel.setTextFill(Color.RED);
        errorLabel.setText(err);
    }
    
    private void handleSuccess(String err) {
        errorLabel.setTextFill(Color.GREEN);
        errorLabel.setText(err);
    }
    
    private void handleWarn(String err) {
        errorLabel.setTextFill(Color.ORANGE);
        errorLabel.setText(err);
    }
    
    private void resetError() {
        errorLabel.setText("");
    }
    
    private void writeToFile(String file, List<String> arrData)
            throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write("");
        int size = arrData.size();
        for (int i=0;i<size;i++) {
            String str = arrData.get(i).toString();
            writer.write(str);
            if(i < size-1) //**This prevent creating a blank like at the end of the file**
                writer.write(newLine);
        }
        writer.close();
    }
    private void setAnswerLenLabel() {
        answerSizeLabel.setText(new Integer(answer.length()).toString());
        answerSizeLabel.setTextFill(Color.GREEN);
    }
    
    private void eraseLines(GraphicsContext gc, Cell[][] cells, int row, int col, double xBottomLeftCorner, double yBottomLeftCorner, double cellSize) {
        if (cells[row][col].getLetter() != '#')
            return;
        int wordIndex = cells[row][col].getWordIndex();
        boolean rightCellDescription = false;
        boolean bottomCellDescription = false;
        double xStart;
        double xEnd;
        double yStart;
        double yEnd;
        
        gc.setStroke(Color.WHITESMOKE);
        gc.setLineWidth(lineWidth*2);
        if (col + 1 < cells[0].length && cells[row][col+1].getLetter() == '#' && cells[row][col+1].getWordIndex() == wordIndex) {
            xStart = xBottomLeftCorner + cellSize + lineWidth/2;
            xEnd = xBottomLeftCorner + cellSize + lineWidth/2;
            yStart = yBottomLeftCorner - lineWidth*2;
            yEnd = yBottomLeftCorner - cellSize + lineWidth*2;
            gc.strokeLine(xStart, yStart, xEnd, yEnd);
            rightCellDescription = true;
        }
        if (row + 1 < cells.length && cells[row+1][col].getLetter() == '#' && cells[row+1][col].getWordIndex() == wordIndex) {
            xStart = xBottomLeftCorner + lineWidth*2;
            xEnd = xBottomLeftCorner + cellSize - lineWidth*2;
            yStart = yBottomLeftCorner;
            yEnd = yBottomLeftCorner;
            gc.strokeLine(xStart, yStart, xEnd, yEnd);
            bottomCellDescription = true;
        }
        if (rightCellDescription && bottomCellDescription) {
            xStart = xBottomLeftCorner + cellSize;
            xEnd = xBottomLeftCorner + cellSize;
            yStart = yBottomLeftCorner + lineWidth*2;
            yEnd = yBottomLeftCorner;
            gc.strokeLine(xStart, yStart, xEnd, yEnd);
        }
        gc.setLineWidth(lineWidth);
        gc.setStroke(Color.BLACK);
    }
}
