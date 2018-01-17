package application.grid.wordsearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import application.err.WordError;
import application.grid.config.WordConfig;
import application.grid.utils.BinPacking;
import application.grid.utils.Validations;


public class StartingPoint {
		
	private String errorMessage;
	private List<String> words;
	private String answer;
	private Grid finalGrid = new Grid();
	WordConfig config;
	private boolean isFinished;
	//Kiek maximaliai ejimu gali paeit atgal
	private final static int MAX_STEPBACKS = 25;
	
	//Kiek kartu bandyt paeit atgal kiekviena zingsni. Pvz paeini x kart 1 zingsni atgal, tuomet einu 2 zingsnius atgal jei vis dar nepavyksta ir tt.
	private final static int DEFAULT_ITERATION_COUNT = 7;
	private int zingsniai;
	
	public long binTime = 0;
	public long enterWordTime = 0;
	
	public StartingPoint(List<String> words, String answer, WordConfig config) {
        this.words = words;
        this.answer = answer;
        this.config = config;
    }


    public void constructGrid() throws WordError {
		//Add config
		
		sortWordList(words);
		this.finalGrid = new Grid(config.getGridSizeX(), config.getGridSizeY());
		
		
		boolean isValid = Validations.validateWordsLength(words, answer, config.getGridSizeX(), config.getGridSizeY());
		
		if (!isValid) {
			throw new WordError("Invalid grid size");
		}
		
		List<Integer> weights = calculateWeights(words);
		List<String> wordsToAdd = new ArrayList<String>();
		List<String> wordsAdded = new ArrayList<String>();
		List<GridWithWord> gridAdded = new ArrayList<GridWithWord>();
		
		
		
		long[] iterations = new long[MAX_STEPBACKS];
		iterations[0] = 0;
		int skaitiklis = 0;
		
		for (this.zingsniai = 0; zingsniai< words.size(); zingsniai++) {
		    String myWord = words.get(this.zingsniai);
			// select starting position for the word
		    
//		    System.out.println("bin laikas: " + binTime);
//		    System.out.println("enterWord laikas: " + enterWordTime);
		    
		    
		    skaitiklis++;
		    if (skaitiklis > 1000000) {
		        throw new WordError("too many tries");
		    } 
		    if (!wordsAdded.isEmpty()) {
		        int ilgis = wordsAdded.size();
		        wordsToAdd = words.subList(wordsAdded.size(), words.size()); 
		    }
		    else
		        wordsToAdd = new ArrayList<String>(words);
		    
		    weights = calculateWeights(wordsToAdd);
		    
		    finalGrid.initiateAreas();
			
			
			
			int[] areaList = finalGrid.getAreaIdArray();
			boolean isAreaValid = validateArea(areaList,answer.length(),words.get(words.size()-1).length());
			int areaForWord = -1;
			
			if (isAreaValid) {
//			    areaForWord = BinPacking.fit(weights, finalGrid.getAreaIdArray());
			    areaForWord = BinPacking.fitDecreasing(weights, finalGrid.getAreaIdArray());
			} 
			
			
			GridWithWord withWordEntered = null;
			
            if (areaForWord != -1) {
                int loopIndex = 0;
                boolean added = false;

                
                //Try to add word
                while (loopIndex < 100 && !added) {
                    loopIndex++;
                    int[] coord = startingPosition(finalGrid, areaForWord, loopIndex);
                    
                    Steps steps = new Steps(finalGrid, myWord, coord);
                    
                    long enterWordt1 = System.currentTimeMillis();
                    withWordEntered = steps.enterWordInGrid();
                    long enterWordt2 = System.currentTimeMillis();
                    enterWordTime = enterWordTime + enterWordt2 - enterWordt1;
                    
                    if (withWordEntered != null) {
                        gridAdded.add(withWordEntered);
                        wordsAdded.add(myWord);
                        finalGrid = withWordEntered.getGrid();
                        added = true;
                    }
                }
                
                //Move step back if could not add word
                if (!added) {
                    moveStepBack(iterations, wordsAdded, gridAdded);
                }
            //Also move back if impossible to add    
            } else {
                moveStepBack(iterations, wordsAdded, gridAdded);
            }
			
		}
	}

	
	private void moveStepBack(long[] iterations, List<String> wordsAdded, List<GridWithWord> gridAdded) throws WordError {
	    iterations[0]++;
        int stepsBack = determineStepBacks(iterations);
        if (stepsBack == 0) {
            throw new WordError("moveStepBack - reached limit of moving back");
        }
//        System.out.println("atgal per zingsius: " + stepsBack);
        stepBack(wordsAdded, gridAdded, stepsBack);
	}
	
	public static int[] startingPosition(Grid grid, int areaToWriteTo, int loopIndex) {
		List<int[]> coord = grid.coordByAreaId(areaToWriteTo);
		List<int[]> coordOther = new ArrayList<>();
		List<int[]> coordWith2Empty = new ArrayList<>();
		List<int[]> coordWith1Empty = new ArrayList<>();
		
		
		if (loopIndex < 5) {
		//Bandom pradeti nuo tu langeliu kur geru koordinaciu yra 1, jei tokiu nera tai kur 2 bet kampinis
		for (int[] c : coord) {
		    List<Integer> directions = grid.determineDirection(c);
		    
		    if (directions.size() ==2 ) {
		        int sum = 0;
		        //Patikrinam kad butu kampinis
		        for (Integer c2 : directions) {
		            sum = sum + c2.intValue();
		        }
		        if (sum % 2 != 0)
		            coordWith2Empty.add(c);
            } else if (directions.size() ==1 ) {
		        coordWith1Empty.add(c);
            } else {
                coordOther.add(c);
            }
		}
		if (coordWith1Empty.size() > 0) {
		    int index = (int) Math.round(Math.random() * (coordWith1Empty.size() - 1));
	        return coordWith1Empty.get(index);
		} else if (coordWith2Empty.size() > 0) {
            int index = (int) Math.round(Math.random() * (coordWith2Empty.size() - 1));
            return coordWith2Empty.get(index);
        } else {
            int index = (int) Math.round(Math.random() * (coordOther.size() - 1));
            return coordOther.get(index);
        }
		} else {
		      int index = (int) Math.round(Math.random() * (coord.size() - 1));
		      return coord.get(index);
		}

	}

	// determine size of the grid based on word length
	public static int determineGridSize(List<String> words) {
		int gridSize = 0;
		for (int listElement = 0; listElement < words.size(); listElement++) {
			gridSize += words.get(listElement).length();
		}
		return (int) Math.ceil(Math.sqrt(gridSize));
	}

	// calculate weights for bin packing algorithm
	public static List<Integer> calculateWeights(List<String> words) {

		List<Integer> weights = new ArrayList<Integer>();
		for (String word : words) {
			weights.add(word.length());
		}
		return weights;
	}
	
	private void sortWordList(List<String> words) {
	    Comparator<String> x = new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                if (o2.length() > o1.length())
                    return 1;
                if (o1.length() > o2.length())
                    return -1;
                return 0;
            }
	    };
	    Collections.sort(words,x);
	}
	
	private void stepBack(List<String> wordsAdded, List<GridWithWord> gridAdded, int n) throws WordError {
	    if (n + 1 > wordsAdded.size()) {
	        throw new WordError("stepBack - Trying to do too many step backs");
	    }
	    
	    for (int j = 0; j< n; j++) {
	        wordsAdded.remove(wordsAdded.get(wordsAdded.size()-1));
	        gridAdded.remove(gridAdded.get(gridAdded.size()-1)); 
	        this.zingsniai--;
	    }
	    this.zingsniai--;
	    finalGrid = gridAdded.get(gridAdded.size()-1).getGrid();
	}
	
    private int determineStepBacks(long[] iterations) {
        int stepBacks = 1;
        for (int i = iterations.length - 1; i >= 0; i--) {
            if (iterations[i] >= DEFAULT_ITERATION_COUNT) {
                stepBacks = i + 1;
                
                for (int j = i; j >= 0; j--) {
                    iterations[j] = 0;
                
                
                if (i < iterations.length - 1) {
                    iterations[i + 1] += 1;
                } else {
                    return 0;
                }
               }
            }
        }
   
	    return stepBacks;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Grid getFinalGrid() {
		return finalGrid;
	}

	public void setFinalGrid(Grid finalGrid) {
		this.finalGrid = finalGrid;
	}
	private boolean validateArea(int[] areaList, int answerLength, int shortestAnswer) {
	    
	    int count = 0;
	    for (int areaSize : areaList) {
	      if (areaSize < shortestAnswer) {
	          count +=areaSize;
	      }
	      if(count > answerLength)
	          return false;
	    }
	    return true;
	}


    public boolean isFinished() {
        return isFinished;
    }


    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }
	
}
