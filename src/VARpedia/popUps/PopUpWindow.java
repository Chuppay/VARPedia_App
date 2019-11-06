package VARpedia.popUps;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class manages all the pop up windows. These include the simple pop up containing that can be dismissed, the pop
 * up to get the user to input yes or not, and a loading screen pop up.
 */

public class PopUpWindow {

    private static Stage _stage;
    private static Boolean _confirm;

    /**
     * Opens a pop up window and forces the user to dismiss before continuing.
     */
    public static void popUP(String name, String msg){
        _stage = new Stage();
        _stage.initModality(Modality.APPLICATION_MODAL);
        _stage.setTitle(name);

        try {
            FXMLLoader loader = new FXMLLoader(PopUpWindow.class.getResource("/VARpedia/popUps/SimplePopUp.fxml"));
            Parent root = loader.load();
            SimplePopUpController controller = loader.getController();
            controller.setMessage(msg);
            _stage.setScene(new Scene(root));
            _stage.showAndWait();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Opens a pop up window and forces the user to answer either yes or no.
     */
    public static Boolean confirm(String name, String msg){
        _stage = new Stage();
        _stage.initModality(Modality.APPLICATION_MODAL);
        _stage.initStyle(StageStyle.UNDECORATED);
        _stage.setTitle(name);

        try {
            FXMLLoader loader = new FXMLLoader(PopUpWindow.class.getResource("/VARpedia/popUps/ConfirmDelete.fxml"));
            Parent root = loader.load();
            ConfirmDeleteController controller = loader.getController();
            controller.setMessage(msg);
            _stage.setScene(new Scene(root));
            _stage.showAndWait();
        } catch (Exception e){
            e.printStackTrace();
        }
        return _confirm;
    }

    /**
     * Opens a pop up window with an animation to let the user know that a long process is happening. This pop up will
     * need to be dismissed via code and not by the user.
     */
    public static void load(){
        _stage = new Stage();
        _stage.initModality(Modality.APPLICATION_MODAL);
        _stage.initStyle(StageStyle.UNDECORATED);
        try{
            Font.loadFont(PopUpWindow.class.getResourceAsStream("/VARpedia/resources/DKCoolCrayon.ttf"),48);
            Parent root = FXMLLoader.load(PopUpWindow.class.getResource("/VARpedia/popUps/LoadingScreen.fxml"));
            _stage.setScene(new Scene(root));
            _stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Returns the pop up window stage so that it can be dismissed.
     */
    public static Stage getStage(){
        return _stage;
    }

    /**
     * Sets the user input as a field so that it can be accessible.
     */
    public static void setConfirm(boolean option){
        _confirm = option;
    }
}