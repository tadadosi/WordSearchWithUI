package application.grid.wordsearch;

import java.util.List;

import application.err.WordError;
import application.grid.config.WordConfig;

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



    public Grid run() throws WordError {
        int timesToTry = 100;
        int loopIndex = 0;
            while (loopIndex < timesToTry && !added) {
                loopIndex++;
                try {
                    StartingPoint startingPoint = new StartingPoint(words, answer, config);
                    startingPoint.constructGrid();
                    resultGrid = startingPoint.getFinalGrid();
                    added = true;
                    System.out.println("done in " + loopIndex + " tries");
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
