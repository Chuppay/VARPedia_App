package VARpedia.popUps;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * This is the controller class for the simple pop up window. The user must dismiss this pop up before continuing to
 * interact with the main GUI
 */

public class SimplePopUpController {

    @FXML
    private Label _message;

    /**
     * Sets the message display so that it can be used to display different messages
     */
    public void setMessage(String msg){
        _message.setText(msg);
    }

    /**
     * Dismisses the pop up
     */
    public void closeStage(){
        PopUpWindow.getStage().close();
    }
}
