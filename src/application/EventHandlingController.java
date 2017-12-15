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
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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
    
    private final static int TILE_SIZE = 30;
    
    private Thread gridThread;
    
    private List<String> words;
    private boolean randomLetters;
    private boolean coloredAnswer;
    private int xSize;
    private int ySize;
    
    private int gridSize;
    private int insertedLettersSize;
    private String answer;
    
    private boolean shutdown;
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
        shutdown = false;
    }
    
    @FXML
    private void initialize()  {
        setErrorAnswerLabelValue();
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
    }
    
    
    private void generateButtonAction() {
        generateButton.setOnAction((event) -> {
            resetError();
            getWordsFromTextArea();
            
            int totalLetters = answer.length() + insertedLettersSize;
            if (totalLetters != gridSize) {
                handleError("Ávestas neteisingas raidþiø skaièius!");
            } else {
                shutdown = false;
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
        int descriptionTiles = 0;
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
        Group root = new Group();
        GraphicsContext gc = answerCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, answerCanvas.getWidth(), answerCanvas.getHeight());
        drawShapes(gc, grid, answerCanvas, true);
        
        GraphicsContext vc = viewCanvas.getGraphicsContext2D();
        vc.clearRect(0, 0, viewCanvas.getWidth(), viewCanvas.getHeight());
        drawShapes(vc, grid, viewCanvas, false);
    }
    
    
    private void drawShapes(GraphicsContext gc,Grid gridObj, Canvas canvas, boolean writeText) {
        int cellSize = TILE_SIZE; // Default cell size
        
        Cell[][] grid = gridObj.getGrid();
        
        int colN = grid[0].length;
        int rowN = grid.length;
        
        int gridWidth = colN * cellSize;
        int gridHeight = rowN * cellSize;
        
        double leftPadding = (canvas.getWidth() - gridWidth)/2;
        double topPadding = (canvas.getHeight() - gridHeight)/2;
        
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        
        gc.strokeRect(leftPadding, topPadding, gridWidth, gridHeight);
        
        int gapToLetter = 15;
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
        for (int i=0; i< rowN; i++) {
            for (int j=0; j< colN; j++) {
                String letter =  Character.toString(grid[i][j].getLetter());
                //Bottom left corner coordinates of cell
                double xBottomLeftCorner = leftPadding + (j * cellSize);
                double yBottomLeftCorner = topPadding + ((i+1) * cellSize);
                if (grid[i][j].getLetter() == '\0' ) {
                    if(coloredAnswer) {
                        gc.setFill(Color.RED);
                    }
                    if (writeText)
                        gc.fillText( Character.toString(answerToWrite.charAt(answerCounter)), xBottomLeftCorner + gapToLetter/2, yBottomLeftCorner - gapToLetter/2);
                    
                    answerCounter++;
                    gc.setFill(Color.BLACK);
                } else {
                    if (writeText)
                        gc.fillText( letter, leftPadding + (j * cellSize) + gapToLetter/2, topPadding + ((i+1) * cellSize) - gapToLetter/2);
                    //Draw line over text
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
            shutdown = true;
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
                answer = answerTextField.getText();
                setErrorAnswerLabelValue();
                    
            }
        }); 
        
    }
    
    private void inputWordTextAreaAction() {
        inputWordTextArea.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                
                
                
                String inputText = inputWordTextArea.getText();
                String cleanString = inputText.replaceAll("\r", "").replaceAll("\n", "");
                insertedLettersSize = cleanString.length();
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
            errorAnswerLabel.setTextFill(Color.web("#FF0000"));
        } else {
            errorAnswerLabel.setTextFill(Color.web("#000000"));
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
            words.add(w.trim());
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
    
}
