package VARpedia.creationMenu;

import VARpedia.VarpediaApp;
import VARpedia.popUps.PopUpWindow;
import VARpedia.resources.Storage;
import VARpedia.workers.APIFlickrWorker;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * This class is the controller for the search menu. It allows the user to specify a search term to query using the
 * wikit command. The creation content will be based on the search term. It also allows the user to name the creation
 * and select the number of images.
 */

public class SearchMenuController {

    @FXML
    private TextField _searchTermField;

    @FXML
    private TextField _creationNameField;

    @FXML
    private ComboBox<String> _numImages;

    @FXML
    private Button _createButton;

    private String[] _text;

    @FXML
    public void initialize(){
        // Sets up the combo box to display the numbers 1-10
        _numImages.getItems().addAll("1","2","3","4","5","6","7","8","9","10");
        _numImages.getSelectionModel().select(0);
        _createButton.setDisable(true);
    }

    /**
     * Changes scene back to the main menu
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
     * Takes the text from both the search term and creation name text fields and proceed to search the search term
     * using the wikit.
     */
    public void pressedCreateButton(ActionEvent event){
        _creationNameField.setText(_creationNameField.getText().replace(' ', '_'));
        String term = _searchTermField.getText();
        String name = _creationNameField.getText();

        //check directory of that name doesn't exist
        File f = new File("./Creations/" + name);
        if (f.exists()) {
            // tell users to pick a new name
            PopUpWindow.popUP("Error", "Creation already exists");
            _creationNameField.setText(name + "_1"); //suggest a new name
            return;
        } else if (Pattern.compile("[*<>\\\\|\"^/:]").matcher(name).find()) { // Checks to see if any invalid characters
            PopUpWindow.popUP("ERROR", "Please choose a name without any of the following characters:  " +
                    "\\ / : * \" < > | ");
            return;
        }

        // pops up a loading screen to let the user know that something is happening
        PopUpWindow.load();

        Runnable task = () -> {
            try{
                //create directory to store files
                String mkDirs = "if [ ! -e ./Creations/" + name + " ]; then\n" +
                        "			mkdir ./Creations/" + name + "\n" +
                        "	fi";
                VarpediaApp.executeBashCommand(mkDirs);

                APIFlickrWorker flickrWorker = new APIFlickrWorker(term, _numImages.getValue(), name);
                flickrWorker.start();

                //search for wikit term, store to text file and check it was valid
                String cmd = ("wikit " + term + " 1> ./Creations/" + name + "/wikiInfo.txt");

                VarpediaApp.executeBashCommand(cmd);

                //Check that the wikit error string is not present in text file
                Scanner scan;
                String text = "";
                scan = new Scanner(new File("./Creations/" + name + "/wikiInfo.txt"));
                while (scan.hasNext()) {
                    text = text + scan.nextLine().toString();
                }

                _text = text.trim().split("(?<=[a-z])\\.\\s+");

                if (text.contains("not found :^(")) {
                    Platform.runLater(() -> {
                        PopUpWindow.getStage().close();
                        PopUpWindow.popUP("Error", "Invalid search term");
                        String deleteDir = "rm -r Creations/"+name;
                        VarpediaApp.executeBashCommand(deleteDir);
                    });
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Platform.runLater(() ->{
                try {
                    PopUpWindow.getStage().close();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/VARpedia/creationMenu/ChunkSelectMenu.fxml"));
                    Parent chunkScene = loader.load();
                    ChunkSelectionMenuController controller = loader.getController();
                    controller.setTextArea(_text); //send the wiki text through to text editing scene
                    VarpediaApp.getStage().setScene(new Scene(chunkScene)); //move to next scene

                    // Sets the search term and creation name as static fields so that it can be accessed in the
                    // stages
                    Storage.getInstance().setNameOfCreation(_creationNameField.getText());
                    Storage.getInstance().setQuery(_searchTermField.getText());
                    Storage.getInstance().setNumImages(_numImages.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        };

        new Thread(task).start();
    }

    /**
     * This method acts as a binding. It enables the create button once the two text fields have inputs.
     */
    public void releaseCreateButton(){
        boolean areFieldsEmpty = (_searchTermField.getText().trim().isEmpty() || _creationNameField.getText().trim().isEmpty());
        _createButton.setDisable(areFieldsEmpty);
    }
}