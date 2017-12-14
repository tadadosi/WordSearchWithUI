package application.grid.utils;

import java.util.List;

public class Validations {
	public static boolean validateWordsLength(List<String> wordList, String answer, int gridSizeX, int gridSizeY) {
		int totalLength  = 0;
		int totalGridSize = gridSizeX * gridSizeY;
		
		
		totalLength = totalLength + answer.length();
		
		for(String word : wordList) {
			totalLength = totalLength + word.length();
		}
		
		if (totalGridSize != totalLength)
			return false;
		
		return true;
	}
}
