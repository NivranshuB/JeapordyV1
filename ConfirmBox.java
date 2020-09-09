package jeopardy;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Class contains single static method which creates a new confirm box window and
 * returns true if the user selected yes or false if the user selected no.
 * 
 * @author Nivranshu Bose
 *
 */
public class ConfirmBox {

	static boolean answer;
	
	/**
	 * Given a title and a message, creates a new window with the message and a yes 
	 * button and a not button. If the user presses yes then returns true, else if the 
	 * user presses no then returns false.
	 */
	public static boolean displayConfirm(String title, String message) {
		
		Stage window = new Stage();
		
		//Window does not close until user selects presses either yes or no
		window.initModality(Modality.APPLICATION_MODAL);
		
		window.setTitle(title);
		window.setMinWidth(250);
		window.setMinHeight(250);
		
		Label label = new Label(message);
		label.setWrapText(true);
		label.setPadding(new Insets(20, 20, 20, 20));
		label.setStyle("-fx-font-size: 18;");
		
		//If window is closed then return false by default
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle (WindowEvent e) {
				window.close();
				answer = false;
			}
		});
		
		Button yes = new Button("Yes");
		yes.setText("Yes");
		yes.setStyle("-fx-border-color: #067CA0;-fx-border-width: 1;-fx-font-size: 18;");
		yes.setPrefSize(60, 30);
		yes.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (ActionEvent e) {
				window.close();
				answer = true;
			}
		});
		
		Button no = new Button("No");
		no.setText("No");
		no.setStyle("-fx-border-color: #067CA0;-fx-border-width: 1;-fx-font-size: 18;");
		no.setPrefSize(60, 30);
		no.setOnAction(new EventHandler<ActionEvent>() {
			public void handle (ActionEvent e) {
				answer = false;
				window.close();
			}
		});
		
		HBox bottomMenu = new HBox();
		bottomMenu.setPadding(new Insets(10, 50, 20, 170));
		bottomMenu.getChildren().addAll(yes, no);
		
		BorderPane layout = new BorderPane();
		layout.setPadding(new Insets(10, 10,10, 10));
		layout.setCenter(label);
		layout.setBottom(bottomMenu);
		
		Scene scene = new Scene(layout, 500, 250);
		window.setScene(scene);
		window.showAndWait();
		
		return answer;
	}
}
