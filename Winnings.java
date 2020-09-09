package jeopardy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This class initialises and retrieves the winnings data for the application.
 * 
 * @author Nivranshu Bose
 *
 */
public class Winnings {
	
	int _currentWinnings;
	
	/**
	 * Initialise winnings by either reading in saved winnings data if available or setting
	 * winnings to the default 0.
	 */
	public Winnings() {
		String save_loc = System.getProperty("user.dir") + System.getProperty("file.separator") + "save_data";

		File save_data = new File(save_loc);
		
		if (Files.exists(save_data.toPath())) {
			String winningsFile = save_loc + System.getProperty("file.separator") + "winnings";
			try {
				BufferedReader fileReader = new BufferedReader(new FileReader(winningsFile));
				_currentWinnings = Integer.parseInt(fileReader.readLine());
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			_currentWinnings = 0;
		}
	}
	
	/**
	 * Returns the current winnings of the user.
	 */
	public int getValue() {
		return _currentWinnings;
	}
}

