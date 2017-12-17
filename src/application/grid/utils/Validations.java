package application.grid.utils;

import java.util.List;

import application.grid.config.WordConfig;

public class Validations {
	public static boolean validateWordsLength(List<String> wordList, String answer,  WordConfig config) {
		int totalLength  = 0;
		int totalGridSize = config.getGridSizeX() * config.getGridSizeY();
		totalLength = totalLength + answer.length();
		
		for(String word : wordList) {
			totalLength = totalLength + word.length() + config.getInfoCellsNumber();
		}
		
		if (totalGridSize != totalLength)
			return false;
		
		return true;
	}
}
