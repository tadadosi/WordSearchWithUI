package application.grid.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {

	// read the file containing words for grid creation
	public static List<String> readFile(String filename) {
		List<String> listLines = new ArrayList<String>();
		try {
			Scanner scanner;
			scanner = new Scanner(new File(filename));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.length() > 0)
					listLines.add(line);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return listLines;
	}
}
