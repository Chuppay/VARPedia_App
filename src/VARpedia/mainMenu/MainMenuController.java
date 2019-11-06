package VARpedia.mainMenu;

import VARpedia.VarpediaApp;
import VARpedia.creationMenu.ChunkSelectionMenuController;
import VARpedia.popUps.PopUpWindow;
import VARpedia.resources.Storage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;

/**
 * This class is the controller for the main menu. It takes the user to their desired scenes.
 */

public class MainMenuController {

    @FXML
    private Button menu;

    /**
     * Exits the application
     */
    public void pressedQuitButton(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Changes the scene to the view menu and updates the creation list to be display
     */
    public void pressedViewButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VARpedia/viewMenu/ViewMenu.fxml"));
            VarpediaApp.getStage().setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the scene to the creation-search menu if no existing creation process and chunk selection menu if there
     * is an incomplete creation process.
     */
    public void pressedCreateButton(ActionEvent event) {
        try {
            // Determines if there was an incomplete creation process
            if (Storage.getInstance().getCreationName() != null) {
                boolean confirmContinue = PopUpWindow.confirm("Load old state", "Would you like to continue where you left off?");
                if (confirmContinue) {
                    // Changes scene to chunk selection menu and loads the old progress
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/VARpedia/creationMenu/ChunkSelectMenu.fxml"));
                    Parent root = loader.load();
                    ChunkSelectionMenuController controller = loader.getController();
                    controller.loadOldState();
                    VarpediaApp.getStage().setScene(new Scene(root));
                } else {
                    Parent searchScene = FXMLLoader.load(getClass().getResource("/VARpedia/creationMenu/SearchMenu.fxml"));
                    VarpediaApp.getStage().setScene(new Scene(searchScene));
                }
            } else {
                Parent searchScene = FXMLLoader.load(getClass().getResource("/VARpedia/creationMenu/SearchMenu.fxml"));
                VarpediaApp.getStage().setScene(new Scene(searchScene));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the scene to the quiz menu.
     */
    public void pressedQuiz(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VARpedia/quiz/QuizMenu.fxml"));
            VarpediaApp.getStage().setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method opens up the user manual.
     */
    public void pressedHelp() {
        String cmd = "xdg-open UserManual.pdf";
        VarpediaApp.executeBashCommand(cmd);
    }
}
