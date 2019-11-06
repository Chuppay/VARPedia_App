package VARpedia.quiz;

import java.util.List;
import VARpedia.VarpediaApp;
import VARpedia.popUps.PopUpWindow;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

/**
 * This class is the controller class for the the quiz menu scene. This scene allows the user to select the creations
 * that they want to be tested on.
 */

public class QuizMenuController {

	@FXML
	ListView<String> _creationsList, _addedList;

	@FXML
	Button _add, _delete,_back, _startQuiz;

	@FXML
	public void initialize() {
		updateList();

		// Disables buttons until creations are selected on their respective list views
		_delete.disableProperty().bind(_addedList.getSelectionModel().selectedItemProperty().isNull());
		_add.disableProperty().bind(_creationsList.getSelectionModel().selectedItemProperty().isNull());
	}

	/**
	 * Changes back to the main menu
	 */
	public void backPressed() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/VARpedia/mainMenu/MainMenu.fxml"));
			VarpediaApp.getStage().setScene(new Scene(loader.load()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the selected creation onto the list of creations to be quizzed on
	 */
	public void addPressed() {
		String selected = _creationsList.getSelectionModel().getSelectedItem();

		if(_addedList.getItems().contains(selected)) {
			//display some already added error to user
			PopUpWindow.popUP("Error","Oh no! You already picked this creation. Please pick something else.");
		} else {
			_addedList.getItems().add(selected);
		}
	}

	/**
	 * Deletes the selected creation from the list of creations to be quizzed on
	 */
	public void deletePressed() {
		String selected = _addedList.getSelectionModel().getSelectedItem();
		_addedList.getItems().remove(selected);
	}

	/**
	 * Changes to the quiz scene and sets up quiz
	 */
	public void startQuizPressed() {
		if (_addedList.getItems().size() <= 1 ) {
			PopUpWindow.popUP("", "Please add at least two creations");
		} else {
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/VARpedia/quiz/Quiz.fxml"));
				Parent playScene = loader.load();

				QuizController controller = loader.getController();
				VarpediaApp.getStage().setScene(new Scene(playScene));

				// Sets up the quiz
				controller.setImages(_addedList.getItems());
				controller.newQuestion();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method scans for creations then updates the creations list
	 */
	public void updateList() {
		_creationsList.getItems().clear();
		List<String> creations = VarpediaApp.scanForCreations();
		_creationsList.getItems().addAll(creations);
	}
}
