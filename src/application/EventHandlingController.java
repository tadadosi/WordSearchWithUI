package application;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.err.WordError;
import application.grid.config.WordConfig;
import application.grid.utils.Shuffle;
import application.grid.wordsearch.Cell;
import application.grid.wordsearch.Grid;
import application.grid.wordsearch.RunGenerator;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    private Button checkWordsButton;
    @FXML
    private Button helpButton;
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
    private Label warnWordsLabel;
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
    @FXML
    private ChoiceBox<String> descrCellsCB;
    @FXML
    private Label digitalClock;
   
    
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
    
    Task<Void> generateGridTask;
    private TimerClock timerClock;
    
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
        words = new ArrayList<>();
    }
    
    @FXML
    private void initialize()  {
    	addCBChildren();
        setErrorAnswerLabelValue();
        setAnswerLenLabel();
        generateButtonAction();
        getHorizontalSlidersParameter();
        getVerticalSlidersParameter();
        inputWordTextAreaAction();
        answerTextFieldAction();
        closeButtonAction();
        saveWordsButtonAction();
        shuffleAnswerCheckBoxAction();
        colorizeLettersCheckBoxAction();
        checkWordsButtonAction();
        stopButtonAction();
        descrCellsAction(); 
//        helpButtonAction();
        answerSizeLabel.setText(new Integer(answer.length()).toString());
        
    }
    
    
    private void generateButtonAction() {
        generateButton.setOnAction((event) -> {
        	resetError();
            getWordsFromTextArea();
            handleWarn("Generuoja...");
            int totalLetters = answer.length() + insertedLettersSize;
            if (totalLetters != gridSize) {
                handleError("Įvestas neteisingas raidžių skaicius!");
            } else if (!wordsValid(words)) {
            	handleError("Yra per trumpų žodžių!");
            } else {
//                timeStart = System.currentTimeMillis();
                timerClock = new TimerClock(errorLabel);
            	timerClock.start();
                generateButton.setDisable(true);
                stopButton.setDisable(false);
                disableAllFields(true);
                clearCanvas();
                createGenerateTask();
                new Thread(generateGridTask).start();
            }
        	
        	
        });
    }
    
//    private void helpButtonAction() {
//        helpButton.setOnAction((event) -> {
//            final Stage dialog = new Stage();
//            dialog.initModality(Modality.APPLICATION_MODAL);
////            dialog.initOwner(primaryStage);
//            VBox dialogVbox = new VBox(20);
//            Text helpText = new Text(
//                    "1. Žodius atskirkite nauja eilute" + newLine +
//                    "2. Žodžiai privalo būti ilgesni nei dvi raidės" + newLine +
//                    "3. Žodžiuose negali būti simbolio #" + newLine +
//                    "4. Algoritmas geriau skaičiuoja jeigu yra trumpi žodžiai, ilgas raktinis žodis, maža matrica, mažai aprašymo laukelių" + newLine
//                    );
//            helpText.setFont(new Font());
//            
//            
//            dialogVbox.getChildren().add(helpText);
//            Scene dialogScene = new Scene(dialogVbox, 300, 200);
//            dialog.setScene(dialogScene);
//            dialog.show();
//        });
//    }
    
    
    private void calculateGrid() throws WordError {
        //xSize slider corresponds to - how many columns and ySize slider - how many rows
        WordConfig config = new WordConfig(ySize,xSize,descriptionTiles);
        RunGenerator gridGenerator = new RunGenerator(config, words, answer);
        Grid gridObj = null;
            gridObj = gridGenerator.run(generateGridTask);
            drawGrid(gridObj);
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
                    resetError();    
                    randomLetters = new_val;
            }
        });
    }
    
    private void colorizeLettersCheckBoxAction() {
        colorizeLettersCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                Boolean old_val, Boolean new_val) {
                    resetError();
                    coloredAnswer = new_val;
            }
        });
    }
    
    private void stopButtonAction() {
        stopButton.setOnAction((event) -> {
        	generateGridTask.cancel();
        	timerClock.stop();
        	handleWarn("Sustabdyta");
            generateButton.setDisable(false);
            stopButton.setDisable(true);
            disableAllFields(false);
        });
    }
    
    private void saveWordsButtonAction() {
        saveWordsButton.setOnAction((event) -> {
            getWordsFromTextArea();
            try {
                handleSuccess("Užsaugota į words.txt failą");
                writeToFile(FILE_TO_SAVE_WORDS, words);
            } catch (IOException e) {
                handleError("Nepavyko įrašyti į failą!");
            }
            
            
        });
    }
    
    private void checkWordsButtonAction() {
        checkWordsButton.setOnAction((event) -> {
//            resetError();
            Set<String> wordsReversable = new HashSet<>();
            Set<String> wordsIdentical = new HashSet<>();
            Set<String> wordsShort = new HashSet<>();
            
            getWordsFromTextArea();
            
            for (String w: words) {
                String lowerWord = wordLowerWithoutNumber(w);
                String reversedWord =  new StringBuilder(lowerWord).reverse().toString();
                if (lowerWord.equals(reversedWord)) {
                    wordsReversable.add(wordWithoutNumber(w));
                }
                if (lowerWord.length() < 5) {
                	wordsShort.add(wordWithoutNumber(w));
                }
            }
           
            for (int i = 0; i < words.size()-1; i++) {
                for (int j = i+1; j< words.size(); j++) {
                    if (wordLowerWithoutNumber(words.get(i)).equals(wordLowerWithoutNumber(words.get(j)))) {
                        wordsIdentical.add(wordWithoutNumber(words.get(i)));
                    }
                }
            }    
            
            handleWanrWords(wordsReversable, wordsIdentical, wordsShort);
        });
    }
    
    private String wordLowerWithoutNumber(String word) {
        if (!word.isEmpty())
            return wordWithoutNumber(word).toLowerCase();
        
        return word;
    }
    
    private String wordWithoutNumber(String word) {
        String wordToCheck = word;
        if (!wordToCheck.isEmpty()) {
            char firstLetter = wordToCheck.charAt(0);
            if (firstLetter == '1' || firstLetter == '2' || firstLetter == '3' || firstLetter == '4') {
                wordToCheck = wordToCheck.substring(1, wordToCheck.length());
            }
            return wordToCheck;
        }
        return word;
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
                resetError();
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
                resetError();
                insertedLettersSize = 0;
                String inputText = inputWordTextArea.getText();
                
//                insertedLettersSize = cleanString.length();
                String[] w = inputText.split("\n");
                //Lets check if there are any numbers which means this will be description empty cells, which take as many cells as number itself. Means we add +1 if its 2 and +2 if 3...
                for (String word : w) {
                    if (!word.isEmpty()) {
                    	word = word.trim().replaceAll("\\s+","");
                        insertedLettersSize += word.length();
                        char c = word.charAt(0);
                        changeDescCellNumber(descrCellsCB.getValue(), c);
                        switch (c) {
                        case '2':
                            insertedLettersSize++;
                            break;
                        case '3':
                            insertedLettersSize += 2;
                            break;
                        case '4':
                            insertedLettersSize += 3;
                            break;
                        default:
                            break;
                        }
                    }
                }
                setErrorAnswerLabelValue();
            }
        }); 
    }
    private boolean descrCellsEqualsSelected(String selectedValue, char letter) {
    	char selectedChar = selectedValue.charAt(0);
    	if (selectedChar == '1' || selectedChar == '2' || selectedChar == '3' || selectedChar == '4') {
    		return selectedValue.charAt(0) == letter;
    	}
    	if (selectedChar == '0') {
    		return letter != '1' && letter != '2' && letter != '3' && letter != '4';
    	}
    	return true;
    	
    }
    
    private void changeDescCellNumber(String selectedValue, char letter) {
    	if (!descrCellsEqualsSelected(selectedValue, letter))
    		descrCellsCB.getSelectionModel().selectFirst();
    }
    
    private void descrCellsAction() {
    	descrCellsCB.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
    	      @Override
    	      public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
    	        if ("1".equals(newValue) || "2".equals(newValue) || "3".equals(newValue) || "4".equals(newValue)) {
    	        	addNumbersToWords(newValue);
    	        } else if ("0".equals(newValue)) {
    	        	removeNumbersToWords();
    	        }
    	      }
    	    });
    }
    
    private void getHorizontalSlidersParameter() {
        horizontalSlider.valueProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    resetError();
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
                    resetError();
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
            handleError("Nerasta nei vieno žodžio!");
        } else {
            words = getWords(inputWordTextArea.getText().split("\n"));
        }
    }
    
    private List<String> getWords(String[] wordsArr) {
        List<String> words = new ArrayList<>();
        
        if (wordsArr == null || wordsArr.length == 0) {
            return words;
        }
        
        for (String w : wordsArr) {
            if (!w.isEmpty())
                words.add(w.trim().replaceAll("\\s+",""));
        }
        
        return words;
    }
    
    private void addNumbersToWords(String newValue) {
    	List<String> newWords = new ArrayList<>();
    	
    	if (inputWordTextArea.getText().equals("")) {
    		handleError("Nerasta nei vieno žodžio!");
        } else {
        	String[] wordsArr = inputWordTextArea.getText().split("\n");
        	for (String w : wordsArr) {
                if (!w.isEmpty()) {
               	 String firstLetter = w.substring(0, 1);
               	 if ("1".equals(firstLetter) || "2".equals(firstLetter) || "3".equals(firstLetter) || "4".equals(firstLetter)) {
               		newWords.add(changeFirstLetter(newValue, w));
               	 } else {
               		newWords.add(addFirstLetter(newValue, w));
               	 }
                }
            }
        	setWordsToArea(newWords);
        }
    }
    
    private void setWordsToArea(List<String> wordList) {
    	String newText = "";
    	for (String word: wordList) {
    		newText += word + newLine;
    	}
    	inputWordTextArea.setText(newText);
    }
    
    private void removeNumbersToWords() {
    	List<String> newWords = new ArrayList<>();

    	if (inputWordTextArea.getText().equals("")) {
    		handleError("Nerasta nei vieno žodžio!");
        } else {
        	String[] wordsArr = inputWordTextArea.getText().split("\n");
        	for (String w : wordsArr) {
                if (!w.isEmpty()) {
               	 String firstLetter = w.substring(0, 1);
               	 if ("1".equals(firstLetter) || "2".equals(firstLetter) || "3".equals(firstLetter) || "4".equals(firstLetter)) {
               		newWords.add(removeFirstLetter(w));
               	 } else {
               	     newWords.add(w);
               	 }
                }
            }
        	setWordsToArea(newWords);
        }
    }
    
    private String removeFirstLetter(String word) {
    	String newWord = word.substring(1, word.length());
    	return newWord;
    }
    
    private String changeFirstLetter(String newLetter, String word) {
    	String newWord = newLetter + word.substring(1, word.length());
    	return newWord;
    }
    private String addFirstLetter(String newLetter, String word) {
    	String newWord = newLetter + word;
    	return newWord;
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
        errorLabel.setTextFill(Color.DARKORANGE);
        errorLabel.setText(err);
    }
    
    private void handleWanrWords(Set<String> reversableWords, Set<String> identicalWords, Set<String> shortWords) {
        String msg = "";
        
        msg += "Simetriški žodžiai: " + newLine;
        
        if (reversableWords != null && !reversableWords.isEmpty()) {
            for (String rw : reversableWords) {
                msg += rw + ", ";
            }
            msg = msg.substring(0, msg.length() - 2);
            msg += newLine;
            msg += newLine;
        } else {
        	msg += "-" + newLine + newLine;
        }
        
        
        msg += "Besikartojantys žodžiai: " + newLine;
        
        if (identicalWords != null && !identicalWords.isEmpty()) {
            for (String iw : identicalWords) {
                msg += iw + ", ";
            }
            msg = msg.substring(0, msg.length() - 2);
            msg += newLine;
            msg += newLine;
        } else {
        	msg += "-" + newLine + newLine;
        }
        
        msg += "Trumpi žodžiai: " + newLine;
        
        if (shortWords != null && !shortWords.isEmpty()) {
            for (String sw : shortWords) {
                msg += sw + ", ";
            }
            msg = msg.substring(0, msg.length() - 2);
        } else {
        	msg += "-" + newLine + newLine;
        }
        
        warnWordsLabel.setTextFill(Color.DARKORANGE);
        warnWordsLabel.setText(msg);
        
//        TextFlow flow = new TextFlow();
//        Text t1 = new Text();
//        t1.setStyle("-fx-fill: #4F8A10;-fx-font-weight:bold;");
//        t1.setText("Vienas");
    }
    
    
    private void resetError() {
        errorLabel.setText("");
        warnWordsLabel.setText("");
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
    
    private void createGenerateTask() {
    	generateGridTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
            	calculateGrid();
            	timerClock.stop();
				return null;
            }
        };
        generateGridTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t)
            {
              timerClock.successMessage(errorLabel);
              generateButton.setDisable(false);
              stopButton.setDisable(true);
              disableAllFields(false);
            }
        });
        
        generateGridTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t)
            {
              handleError("Nepavyko sugeneruoti. Bandykite dar.");
              generateButton.setDisable(false);
              stopButton.setDisable(true);
              disableAllFields(false);
             
            }
        });
    }
    
    private void clearCanvas() {
    	GraphicsContext gc = answerCanvas.getGraphicsContext2D();
        GraphicsContext pc = problemCanvas.getGraphicsContext2D();
        GraphicsContext vc = viewCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, answerCanvas.getWidth(), answerCanvas.getHeight());
        vc.clearRect(0, 0, viewCanvas.getWidth(), viewCanvas.getHeight());
        pc.clearRect(0, 0, problemCanvas.getWidth(), problemCanvas.getHeight());
    }
    
    private void disableAllFields(boolean disable) {
    	 inputWordTextArea.setDisable(disable);
         horizontalSlider.setDisable(disable);
         verticalSlider.setDisable(disable);
         answerTextField.setDisable(disable);
         shuffleAnswerCheckBox.setDisable(disable);
         colorizeLettersCheckBox.setDisable(disable);
         descrCellsCB.setDisable(disable);
    }
    
    private boolean wordsValid(List<String> allWords) {
    	for (String w: allWords) {
    		if (wordWithoutNumber(w) != null && wordWithoutNumber(w).length() < 3) 
    			return false;
    	}
    	return true;
    }
    private void addCBChildren() {
    	descrCellsCB.getItems().add("-");
    	descrCellsCB.getItems().add("0");
    	descrCellsCB.getItems().add("1");
    	descrCellsCB.getItems().add("2");
    	descrCellsCB.getItems().add("3");
    	descrCellsCB.getItems().add("4");
    	descrCellsCB.getSelectionModel().selectFirst();
    }
}
