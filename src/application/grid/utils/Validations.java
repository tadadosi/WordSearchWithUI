package application.grid.utils;

import java.util.List;

public class Validations {
	public static boolean validateWordsLength(List<String> wordList, String answer, int gridSizeX, int gridSizeY) {
		int totalLength  = 0;
		int totalGridSize = gridSizeX * gridSizeY;
		
		
		totalLength = totalLength + answer.length();
		
		for(String word : wordList) {
			totalLength = totalLength + word.length();
			char firstLetter = word.charAt(0);
			switch (firstLetter) {
            case '2':
                totalLength++;
                break;
            case '3' :
                totalLength += 2;
                break;
            case '4' :
                totalLength += 3;
                break;
            default:
                break;
            }
		}
		
		if (totalGridSize != totalLength)
			return false;
		
		return true;
	}
}
