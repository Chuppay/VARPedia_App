package VARpedia.creationMenu;

import VARpedia.popUps.PopUpWindow;
import VARpedia.resources.Storage;
import VARpedia.workers.APIFlickrWorker;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import VARpedia.VarpediaApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.File;
import java.util.Scanner;

/**
 * This class is the controller for the chunk selection menu. This scene allows the user to customise the audio of the
 * creation and select the desired chunks of text they want played.
 */

public class ChunkSelectionMenuController {

    @FXML
    private ListView<String> _textList;

    @FXML
    private ComboBox<String> _synthesizer;

    @FXML
    private Button _add, _preview, _Back, _delete, _play, _createCreation;

    @FXML
    private ListView<AudioChunk> _existingAudioChunks;

    private final ObservableList<AudioChunk> _audioChunks= FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // sets up already existing audio chunks and the synthesizer combobox options
        _existingAudioChunks.setItems(_audioChunks);
        _synthesizer.getItems().addAll("eSpeak","Festival");
        _synthesizer.getSelectionModel().select(0);

        // Disables buttons until the desired input is given by the user
        _preview.disableProperty().bind(_textList.getSelectionModel().selectedItemProperty().isNull());
        _add.disableProperty().bind(_textList.getSelectionModel().selectedItemProperty().isNull());
        _delete.disableProperty().bind(_existingAudioChunks.getSelectionModel().selectedItemProperty().isNull());
        _play.disableProperty().bind(_existingAudioChunks.getSelectionModel().selectedItemProperty().isNull());
        _createCreation.disableProperty().bind(Bindings.isEmpty(_existingAudioChunks.getItems()));
    }

    /**
     * Adds the selected chunk of text to the list for creation
     */
    public void addPressed(ActionEvent event) {
        // checks if the all fields have correct inputs
        AudioChunk chunk = new AudioChunk(_textList.getSelectionModel().getSelectedItem(), _synthesizer.getValue());
        _audioChunks.add(chunk);

        Storage.getInstance().setAudioChunks(_audioChunks);
    }

    /**
     * Previews the highlighted text given the synthesizer
     */
    public void previewPressed(ActionEvent event) {
        // checks if all the fields have correct inputs
        String option = _synthesizer.getValue();
        String cmd = "";

        // Checks the synthesizer and determines the bash command to use
        if (option.equals("eSpeak")) {
            cmd = "espeak \"" + _textList.getSelectionModel().getSelectedItem() + "\"";
        } else if (option.equals("Festival")) {
            cmd = "echo " + _textList.getSelectionModel().getSelectedItem() +" | festival --tts";
        }
        VarpediaApp.executeBashCommand(cmd);
    }

    /**
     * Changes scene back to the search menu
     */
    public void pressedBack(ActionEvent event){
        try {
            // resets the temporary so that a new creation can be made without existing files interrupting
            String cmd = "rm -r Creations/"+ Storage.getInstance().getCreationName();
            VarpediaApp.executeBashCommand(cmd);
            Parent mainMenu = FXMLLoader.load(getClass().getResource("/VARpedia/mainMenu/MainMenu.fxml"));
            VarpediaApp.getStage().setScene(new Scene(mainMenu));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the existing chunk of audio from the list used for creation
     */
    public void pressedDelete(ActionEvent event){
        AudioChunk selected = _existingAudioChunks.getSelectionModel().getSelectedItem();
        _audioChunks.remove(selected);
    }

    /**
     * Plays the existing chunk of audio from the list used for creation
     */
    public void pressedPlay (ActionEvent event){
        AudioChunk selected = _existingAudioChunks.getSelectionModel().getSelectedItem();
        selected.playAudioChunk();
    }

    /**
     * Makes a creation out of the audio chucks selected
     */
    public void pressedCreate(ActionEvent event){
        if (_audioChunks.isEmpty()) {
            PopUpWindow.popUP("Please add an audio chunk", "Highlight some text, select a synthesiser and click 'add' to add audio");
        } else {
            try {
                // Changes scene to the image previewer
                Parent getImages = FXMLLoader.load(getClass().getResource("/VARpedia/creationMenu/MusicMenu.fxml"));
                VarpediaApp.getStage().setScene(new Scene(getImages));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Assigns the text area the given text
     */
    public void setTextArea(String[] text) {
        ObservableList<String> temp= FXCollections.observableArrayList();
        temp.addAll(text);
        _textList.setItems(temp);
    }

    /**
     * Loads the state of where the user left off. It recreates the directory that was previously created to store the
     * creation contents in. It calls the setOldChunks method to load up the content and audio that was once there.
     */
    public void loadOldState() {
        PopUpWindow.load();

        Runnable task = () -> {
            try {
                String term = Storage.getInstance().getQuery();
                String creationName = Storage.getInstance().getCreationName();

                // sets up a directory to store creation contents in
                String mkDirs = "mkdir Creations/" + Storage.getInstance().getCreationName();
                VarpediaApp.executeBashCommand(mkDirs);

                // Redownloads the images from Flickr
                APIFlickrWorker flickrWorker = new APIFlickrWorker(term, Storage.getInstance().getNumImages(), creationName);
                flickrWorker.start();

                // Query the search term
                String wikit = ("wikit " + term + " 1> ./Creations/" + creationName + "/wikiInfo.txt");
                VarpediaApp.executeBashCommand(wikit);
                setOldChunks();

                Platform.runLater(() -> {
                    PopUpWindow.getStage().close();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        new Thread(task).start();
    }

    /**
     * This method loads the query result onto the list view and an AudioChunks previously selected.
     */
    public void setOldChunks(){
        try {
            // loads the text from the a text file
            String text = "";
            Scanner scan = new Scanner(new File("./Creations/" + Storage.getInstance().getCreationName() +
                    "/wikiInfo.txt"));
            while (scan.hasNext()) {
                text = text + scan.nextLine().toString();
            }

            // set the previously loaded text onto the list view
            String[] listOfText = text.trim().split("(?<=[a-z])\\.\\s+");
            setTextArea(listOfText);

            // loads up old AudioChunks if there are any
            if (Storage.getInstance().getAudioChucks() != null) {
                _audioChunks.addAll(Storage.getInstance().getAudioChucks());
                _existingAudioChunks.setItems(_audioChunks);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
