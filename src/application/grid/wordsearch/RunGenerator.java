package application.grid.wordsearch;

import java.util.List;

import application.err.WordError;
import application.grid.config.WordConfig;
import javafx.concurrent.Task;

public class RunGenerator {
    boolean added = false;
    Grid resultGrid;
    String errorMessage;
    WordConfig config;
    List<String> words;
    String answer;
    

    public RunGenerator(WordConfig config, List<String> words, String answer) {
        super();
        this.config = config;
        this.words = words;
        this.answer = answer;
    }



    public Grid run(Task<Void> task) throws WordError {
        int timesToTry = 1000;
        int loopIndex = 0;
            while (!added && !task.isCancelled()) {
                loopIndex++;
                try {
                    StartingPoint startingPoint = new StartingPoint(words, answer, config, task);
                    startingPoint.constructGrid();
                    resultGrid = startingPoint.getFinalGrid();
                    if (!task.isCancelled()) {
                    	added = true;
                    	System.out.println("done in " + loopIndex + " tries");
                    } else {
                    	System.out.println("Stopped at itteration: " + loopIndex);
                    }
                    
                } catch (WordError e) {
                    errorMessage = e.getMessage();
                    System.out.println("failed in " + loopIndex + " tries. Error - " + e.getMessage());
                }
            }
        if (added) {
            return resultGrid;
        } else {
            throw new WordError("Failed to generate grid. Last iteration error: " + errorMessage);
        }
          
    }
}
