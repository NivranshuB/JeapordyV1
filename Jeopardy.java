package jeopardy;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * The main class for the Jeopardy application. This class launches the game window
 * handles all the actions to the main menu and the question board, and also fires
 * up any alert box, confirm box or text box requirements for the game.
 * 
 * @author Nivranshu Bose | 05/09/2020
 */
public class Jeopardy extends Application {

	QuestionRetriever _questions;//all access to category and questions is through this
	Winnings _currentWinnings;//allows the retrieval of saved winnings data
	Stage _gameWindow;
	Scene _menuScene;//the default scene for the game (also the main menu)
	int _winnings;//the total winnings for the player

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Implemented start method that initialises all components of the main
	 * menu scene including any buttons that the user interacts with.
	 */
	public void start(Stage PrimaryStage) {
		_questions = new QuestionRetriever();//initialise categories and questions
		_currentWinnings = new Winnings();//initialise winnings
		_winnings = _currentWinnings.getValue();
		_gameWindow = PrimaryStage;

		PrimaryStage.setTitle("Jeopardy");

		//Welcome message centered at the top of main menu
		StackPane menuText = new StackPane();
		Text welcome = new Text("Welcome to Jeopardy");
		welcome.setStyle("-fx-font-size: 30;");
		welcome.setTextAlignment(TextAlignment.CENTER);
		menuText.getChildren().add(welcome);
		StackPane.setAlignment(welcome, Pos.CENTER);

		//Prompt to the user to engage with the main menu options
		StackPane menuInfo = new StackPane();
		Text info = new Text("Please select one of the following options: ");
		info.setStyle("-fx-font-size: 12;");
		info.setTextAlignment(TextAlignment.CENTER);
		menuInfo.getChildren().add(info);
		StackPane.setAlignment(info, Pos.CENTER);

		//Prints out the question board in a new scene
		Button printBoardButton = new Button();
		printBoardButton.setText("Print question board");
		printBoardButton.setPrefSize(460,60);
		printBoardButton.setStyle("-fx-border-color: #200459;-fx-border-width: 1;-fx-font-size: 16;");
		printBoardButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (ActionEvent e) {
				displayQuestionBoard();
			}
		});

		//Prints out the question board in a new scene
		Button askQuestionButton = new Button();
		askQuestionButton.setText("Ask a question");
		askQuestionButton.setPrefSize(460,60);
		askQuestionButton.setStyle("-fx-border-color: #070459;-fx-border-width: 1;-fx-font-size: 16;");
		askQuestionButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (ActionEvent e) {
				displayQuestionBoard();
			}
		});

		//Prints out the player's winnings in a new window
		Button viewWinningsButton = new Button();
		viewWinningsButton.setText("View the current winnings");
		viewWinningsButton.setPrefSize(460,60);
		viewWinningsButton.setStyle("-fx-border-color: #0B478D;-fx-border-width: 1;-fx-font-size: 16;");
		viewWinningsButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (ActionEvent e) {
				AlertBox.displayAlert("Winnings", "Your current winnings are $" + _winnings, "#0E9109");
			}
		});

		//Resets the game by reinitialising category and questions
		Button resetGameButton = new Button();
		resetGameButton.setText("Reset Game");
		resetGameButton.setPrefSize(460,60);
		resetGameButton.setStyle("-fx-border-color: #184FA0;-fx-border-width: 1;-fx-font-size: 16;");
		resetGameButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (ActionEvent e) {
				resetGame();
			}
		});

		//Saves the category and questions data of the game into a file before exiting the game
		Button exitButton = new Button();
		exitButton.setText("Exit Game");
		exitButton.setPrefSize(460,60);
		exitButton.setStyle("-fx-border-color: #067CA0;-fx-border-width: 1;-fx-font-size: 16;");
		exitButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (ActionEvent e) {
				boolean confirmation = ConfirmBox.displayConfirm("Exit confirmation", "Are you sure "
						+ "you want to exit? (Don't worry your progress will be saved)");
				if (confirmation) {
					exitGame();
				}
			}
		});

		//Saves the category and questions data for the player into a file before exiting the game
		_gameWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle (WindowEvent e) {
				boolean confirmation = ConfirmBox.displayConfirm("Exit confirmation", "Are you sure "
						+ "you want to exit? (Don't worry your progress will be saved)");
				if (confirmation) {
					exitGame();
				}
				e.consume();
			}
		});

		//Set up the layout for the contents of the main menu
		VBox menuLayout = new VBox();
		menuLayout.setSpacing(10);
		menuLayout.setPadding(new Insets(20, 20, 30, 20)); 
		menuLayout.getChildren().addAll(menuText, menuInfo, printBoardButton, askQuestionButton,
				viewWinningsButton, resetGameButton, exitButton);

		_menuScene = new Scene(menuLayout, 500, 500);
		PrimaryStage.setScene(_menuScene);
		PrimaryStage.show();
	}

	/**
	 * This method displays the question board for the user by using the data available on 
	 * the categories and questions, via the QuestionRetriever instance. This method is also
	 * responsible for handling the requests when a user selects a question to answer.
	 */
	private void displayQuestionBoard() {

		//initial layout for the graphical layout of the category names
		HBox topMenu = new HBox();
		topMenu.setPadding(new Insets(5, 5, 5, 5));
		topMenu.setSpacing(8);

		//initial setup for the graphical layout of the questions
		GridPane quesLayout = new GridPane();
		quesLayout.setPadding(new Insets(10, 10,10, 10));
		quesLayout.setVgap(8);
		quesLayout.setHgap(20);

		int i = 0;
		int j = 0;

		int categoriesDone = 0;//this variable enables the check to see if all questions
		//of all categories have been attempted or not

		//for each question from each category create a new button or label depending on
		//if the question has been attempted or not
		for (Category c : _questions.getCategoryList()) {

			int questionsDone = 0;//this variable enables the check to see if all questions
			//of a category have been attempted or not

			for (Question q : c.getQuestions()) {

				//if the question has been attempted already
				if (q.getValue() == 1 || q.getValue() == -1) {
					String doneColor = "#000000";

					//if the question was answered correctly the color of text 'Done' is 
					//green, else the color of text 'Done' is red
					if (q.getValue() == 1) {
						doneColor = "#0E9109";
					} else if (q.getValue() == -1) {
						doneColor = "#BC0808";
					}

					//'Done' label is for a question that has already been attempted
					Label label = new Label();
					label.setText("Done");
					label.setPrefSize(80, 40);
					label.setPadding(new Insets(0, 0, 0, 14));
					label.setStyle("-fx-font-size: 18;-fx-border-color: " + doneColor +
							";-fx-text-fill: " + doneColor);
					GridPane.setConstraints(label, i, j);
					quesLayout.getChildren().add(label);

					questionsDone++;

				} else {

					//Creation of button instance for a question
					Button button = new Button();
					button.setText(Integer.toString(q.getValue()));
					button.setPrefSize(80, 40);
					button.setStyle("-fx-border-color: #067CA0;-fx-border-width: 1;"
							+ "-fx-font-size: 18;");

					button.setOnAction(new EventHandler<ActionEvent>() {
						public void handle (ActionEvent e) {

							//Confirm with the user if they want the answer the question
							boolean confirm = ConfirmBox.displayConfirm("Question confirmation",
									"You picked category " + c.getCategoryName() + " for " + Integer.toString(q.getValue()) + ". \n"
											+ "Are you sure you want this question?");

							if (confirm) {
								//ask the user the question in a new QuestionBox window
								String answerInput = QuestionBox.displayConfirm("You picked category " + c.getCategoryName() +
										" for " + Integer.toString(q.getValue()), q.getQuestion());
								String cmd = "";

								//if the answer is correct 'echo correct' using BASH, send an alert box to the user and update winnings
								if (answerInput.trim().equalsIgnoreCase(q.getAnswer())) {
									cmd = "echo " + "Correct" + " | espeak";
									_winnings += q.getValue();
									questionFeedback(true, q);

									//if the answer is wrong 'echo' the correct answer using BASH, send	an alert box to the user and update winnings
								} else {
									cmd = "echo " + "Incorrect, the correct answer was " + q.getAnswer() + " | espeak";
									_winnings -= q.getValue();
									questionFeedback(false, q);
								}
								//Run a BASH process from java using ProcessBuilder
								ProcessBuilder builder =  new ProcessBuilder("/bin/bash", "-c", cmd);
								try {
									Process process = builder.start();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							} 

						}
					});
					GridPane.setConstraints(button, i, j);
					quesLayout.getChildren().add(button);
				}
				j++;
			}
			j = 0;
			i++;

			//Create a label for the category name
			Label categoryLabel = new Label();

			if (questionsDone == c.numberOfQuestions()) {
				categoryLabel.setText("Complete");
				categoryLabel.setPrefSize(100, 50);
				categoryLabel.setMaxSize(100, 50);
				categoryLabel.setPadding(new Insets(2, 2, 2, 2));
				categoryLabel.setStyle("-fx-font-size: 17;-fx-border-width: 1;-fx-background-color: #2A9600;-fx-text-fill: #ffffff");
				categoriesDone++;
			} else {
				categoryLabel.setText(c.getCategoryName());
				categoryLabel.setPrefSize(100, 50);
				categoryLabel.setPadding(new Insets(2, 2, 2, 2));
				categoryLabel.setStyle("-fx-font-size: 18;-fx-border-width: 1;-fx-background-color: #040662;-fx-text-fill: #ffffff");
			}

			topMenu.getChildren().add(categoryLabel);
		}

		//if all categories are complete then invoke gameFinished()
		if (categoriesDone == _questions.getCategoryList().size()) {
			gameFinished();
		}

		Button backButton = new Button();
		backButton.setText("Back");
		backButton.setPrefSize(80, 40);
		backButton.setStyle("-fx-border-color: #067CA0;-fx-border-width: 1;-fx-font-size: 18;");
		backButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (ActionEvent e) {
				_gameWindow.setScene(_menuScene);
			}
		});

		backButton.setTextAlignment(TextAlignment.CENTER);

		StackPane bottomMenu = new StackPane();
		bottomMenu.getChildren().add(backButton);  
		bottomMenu.setPadding(new Insets(0, 0, 20, 0));
		StackPane.setAlignment(backButton, Pos.CENTER);

		BorderPane layout = new BorderPane();
		layout.setTop(topMenu);
		layout.setCenter(quesLayout);
		layout.setBottom(bottomMenu);

		Scene quesScene = new Scene(layout, 500, 500);

		_gameWindow.setScene(quesScene);
	}

	/**
	 * This method creates a new window AlertBox to give the user feedback about 
	 * their answer to a question.
	 * @param outcome: true if answer was correct, false if answer was incorrect
	 * @param ques: the question that the user answered
	 */
	private void questionFeedback(boolean outcome, Question ques) {
		if (outcome) {
			AlertBox.displayAlert("Correct answer", "Congratulations!!! You just won $" + ques.getValue(), "#0E9109");
		} else {
			AlertBox.displayAlert("Incorrect answer", "Oops that was the wrong answer. You just lost $" + 
					ques.getValue() + ". The correct answer was '" + ques.getAnswer() + "'.", "#BC0808");
		}
		ques.questionAttempted(outcome);
		_gameWindow.setScene(_menuScene);
	}

	/**
	 * This method deletes all the save_data for the game category/questions and the
	 * winnings and then reinitialises the category/questions and the winnings.
	 */
	private void resetGame() {
		boolean confirmation = ConfirmBox.displayConfirm("Reset Game", "Are you sure you want to reset the game?");
		if (confirmation) {

			//if saved data exists then also need to delete this
			String save_loc = System.getProperty("user.dir") + System.getProperty("file.separator") + "save_data";
			File save_data = new File(save_loc);

			deleteDirectory(save_data);

			_questions = new QuestionRetriever();
			_currentWinnings = new Winnings();
			_winnings = _currentWinnings.getValue();
		}
	}

	/**
	 * This method deletes any previous save_data recursively and then rewrites 
	 * the question/category data and the winnings data into the save_data directory.
	 */
	private void exitGame() {

		String save_loc = System.getProperty("user.dir") + System.getProperty("file.separator") + "save_data";
		Path pathCategoryData = Paths.get(save_loc + System.getProperty("file.separator") + "categories");

		try {
			Files.createDirectories(pathCategoryData);
		} catch (IOException e) {
			System.err.println("Failed to create directory!" + e.getMessage());
		}

		try {
			FileWriter winningsWriter = new FileWriter(save_loc + "/winnings");
			winningsWriter.write(String.valueOf((_winnings)));
			winningsWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Write data for each question to its respective category file
		for (Category c : _questions.getCategoryList()) {
			try {
				FileWriter categoryWriter = new FileWriter(save_loc + "/categories/" + c.getCategoryName());

				for (Question q : c.getQuestions()) {
					categoryWriter.write(q.getValue() + "," + q.getQuestion() + "," + q.getAnswer() + "\n");
				}				
				categoryWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		_gameWindow.close();
	}

	/**
	 * This method deletes a specified directory and all its contents recursively.
	 * 
	 * @param directoryToBeDeleted 
	 * @return | true if File deleted successfully else return false
	 */
	private boolean deleteDirectory(File directoryToBeDeleted) {
		File[] contents = directoryToBeDeleted.listFiles();
		if (contents != null) {
			for (File file : contents) {
				deleteDirectory(file);
			}
		}
		return directoryToBeDeleted.delete();
	}

	/**
	 * This method displays a message with the total winnings earned by the user
	 * when the game is finished, i.e. when all the questions from all the categories
	 * have been attempted.
	 */
	private void gameFinished() {
		AlertBox.displayAlert("Game finished", "Congratulations!!! You earned $" + _winnings + ". Well done.", "#067CA0");

		String save_loc = System.getProperty("user.dir") + System.getProperty("file.separator") + "save_data";
		File save_data = new File(save_loc);

		deleteDirectory(save_data);

		_questions = new QuestionRetriever();
		_currentWinnings = new Winnings();
		_winnings = _currentWinnings.getValue();

		_gameWindow.close();
	}
}
