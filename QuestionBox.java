package jeopardy;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Class contains single static method which creates a new question box window and
 * also reads out the question input using a BASH command process.
 * 
 * @author Nivranshu Bose
 *
 */
public class QuestionBox {

	static String answer = "";

	/**
	 * Given a title and a question, creates a new window with the question and 
	 * a test field for the user to write their answer. It also reads out the question
	 * using BASH espeak. This method will then return the answer as a string.
	 */
	public static String displayConfirm(String title, String question) {
		
		String cmd = "echo " + question + " | espeak";
		ProcessBuilder builder =  new ProcessBuilder("/bin/bash", "-c", cmd);
		
		try {
			Process process = builder.start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Stage window = new Stage();

		window.initModality(Modality.APPLICATION_MODAL);

		window.setTitle(title);
		window.setMinWidth(250);
		window.setMinHeight(250);

		Label questionLabel = new Label(question);
		questionLabel.setWrapText(true);
		questionLabel.setPadding(new Insets(20, 20, 20, 20));
		questionLabel.setStyle("-fx-font-size: 18;");
		
		GridPane answerLayout = new GridPane();
		answerLayout.setPadding(new Insets(10, 10,10, 10));
		answerLayout.setVgap(8);
		answerLayout.setHgap(10);

		TextField answerPrompt = new TextField();
		answerPrompt.setPromptText("Type your one word answer");
		answerPrompt.setPrefSize(80, 50);
		answerPrompt.setFocusTraversable(false);
		answerPrompt.setStyle("-fx-font-size: 18;");
		GridPane.setConstraints(answerPrompt, 0, 0);
		
		Button submit = new Button("Submit");
		submit.setStyle("-fx-border-color: #067CA0;-fx-border-width: 1;-fx-font-size: 18;");
		submit.setOnAction(new EventHandler<ActionEvent>() {
					public void handle (ActionEvent e) {
						answer = answerPrompt.getText();
						window.close();
					}
				});
		submit.setTextAlignment(TextAlignment.CENTER);

		StackPane bottomMenu = new StackPane();
		bottomMenu.getChildren().add(submit);  
		bottomMenu.setPadding(new Insets(0, 0, 20, 0));
        StackPane.setAlignment(submit, Pos.CENTER);
		
		BorderPane layout = new BorderPane();
		layout.setPadding(new Insets(10, 10,10, 10));
		layout.setTop(questionLabel);
		layout.setCenter(answerPrompt);
		layout.setBottom(bottomMenu);

		Scene scene = new Scene(layout, 500, 250);
		window.setScene(scene);
		window.showAndWait();
		
		return answer;
	}
}
