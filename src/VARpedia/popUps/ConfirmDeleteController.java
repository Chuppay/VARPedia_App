package VARpedia.popUps;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * This class is the controller for a pop up. This pop up prompts the user to either answer yes or no.
 */

public class ConfirmDeleteController {

	@FXML
	Label _text;

	/**
	 * Stores a true as the user input
	 */
	public void pressedYes(ActionEvent event) {
		PopUpWindow.setConfirm(true);
		PopUpWindow.getStage().close();
	}

	/**
	 * Stores a false as the user input
	 */
	public void pressedNo(ActionEvent event) {
		PopUpWindow.setConfirm(false);
		PopUpWindow.getStage().close();
	}

	/**
	 * Allows the message to be customizable
	 */
	public void setMessage(String msg) {
		_text.setText(msg);
	}
}
