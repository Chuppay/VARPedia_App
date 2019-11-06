package VARpedia.viewMenu;

import VARpedia.VarpediaApp;
import VARpedia.popUps.PopUpWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class is the controller vlass for the view menu. This scene allows the user to either view or delete existing
 * creations.
 */

public class ViewMenuController {

    @FXML
    private ComboBox<String> _creationList = new ComboBox<String>();

    @FXML
    private Button _delete,_play;

    @FXML
    private void initialize(){
        // Disables buttons until a creation is selected in the combo box
        _delete.disableProperty().bind(_creationList.getSelectionModel().selectedItemProperty().isNull());
        _play.disableProperty().bind(_creationList.getSelectionModel().selectedItemProperty().isNull());

        refreshList();
    }

    /**
     * Changes scene back to main menu
     */
    public void pressedBackButton(ActionEvent event){
        try {
            Parent mainScene = FXMLLoader.load(getClass().getResource("/VARpedia/mainMenu/MainMenu.fxml"));
            VarpediaApp.getStage().setScene(new Scene(mainScene));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the selected creation from the combo box and then plays it
     */
    public void pressedPlayButton(ActionEvent event){
        try {

            //get correct creation file
            String selected = _creationList.getSelectionModel().getSelectedItem();
            if (selected == null ){
                PopUpWindow.popUP("Error","Please select a creations");
                return;
            }
            File fileUrl = new File("./Creations/"+selected+ "/" + selected +".mp4");

            //Change scene and play file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/VARpedia/viewMenu/PlayVideo.fxml"));
            Parent playScene = loader.load();

            PlayVideoController controller = loader.getController();
            VarpediaApp.getStage().setScene(new Scene(playScene));
            controller.play(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the selected creation to refreshes the list that so it does not show up in list after update.
     */
    public void pressedDeleteButton(ActionEvent event){
        String selected = _creationList.getSelectionModel().getSelectedItem();

        Boolean confirmed = PopUpWindow.confirm("Confirm","Are you sure you want to delete \"" + selected + "\"?");
        if (confirmed){
            String cmd = "rm -r Creations/\""+selected+"\"";
            VarpediaApp.executeBashCommand(cmd);
            refreshList();
        }
    }

    /**
     * Refreshes the list of creations
     */
    public void refreshList() {
        _creationList.getItems().clear();

        List<String> creations = VarpediaApp.scanForCreations();
        _creationList.getItems().addAll(creations);

        if (!_creationList.getItems().isEmpty()) {
            _creationList.getSelectionModel().select(0);
        }
    }
}
