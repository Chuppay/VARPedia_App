package VARpedia.creationMenu;

import VARpedia.VarpediaApp;
import VARpedia.popUps.PopUpWindow;
import VARpedia.resources.Storage;
import VARpedia.workers.CreationWorker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

/**
 * This class is the controller for the image selection scene. It allows the user to delete any images that the do not
 * wish to include in their creation.
 */

public class ImageSelectionMenuController {

    @FXML
    private ListView<String> _listView;

    @FXML
    private ImageView _imageView;

    private final ObservableList<String> _imageList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Scans through the creation directory for images
        File dir = new File("./Creations/" + Storage.getInstance().getCreationName());
        File[] files = dir.listFiles((dir1, name) -> {
            if (name.endsWith(".jpg")) {
                return (true);
            }
            return (false);
        });

        // Displays the images found
        for (File file : files) {
            _imageList.add(file.getName());
        }
        _listView.setItems(_imageList);
        _imageView.setImage(new Image("file:./Creations/"+Storage.getInstance().getCreationName()+"/" + _imageList.get(0)));

        // Enables the image view to display the selected image from the list view with a simple click
        _listView.setOnMouseClicked(e -> {
            Image selectedImage = new Image("file:./Creations/"+Storage.getInstance().getCreationName()+"/" + _listView.getSelectionModel().getSelectedItem());
            _imageView.setImage(selectedImage);
        });
    }

    /**
     * This method deleted the selected image from the list view.
     */
    public void pressedDelete(ActionEvent event) {
        // Ensures that there will be at least one image remaining
        if (_imageList.size() == 1) {
            return;
        }

        // Sets the image above the deleted image onto the image view
        String selected = _listView.getSelectionModel().getSelectedItem();
        int index = _imageList.indexOf(selected);
        if (index == 0){
            _imageView.setImage(new Image("file:./Creations/" +Storage.getInstance().getCreationName() + "/" + _imageList.get(1)));
        } else {
            _imageView.setImage(new Image("file:./Creations/" + Storage.getInstance().getCreationName() + "/" + _imageList.get(index - 1)));
        }

        // deletes the image file
        _imageList.remove(selected);
        File file = new File("./Creations/"+Storage.getInstance().getCreationName() + "/"  + selected);
        file.delete();
    }

    /**
     * This method triggers the creation to finally be created
     */
    public void pressedContinue(ActionEvent event) {
        try {
            // Changes back to the main scene
            Parent mainScene = FXMLLoader.load(getClass().getResource("/VARpedia/mainMenu/MainMenu.fxml"));
            VarpediaApp.getStage().setScene(new Scene(mainScene));

            PopUpWindow.load();

            // Makes the creation .mp4 file
            CreationWorker creationWorker = new CreationWorker(Storage.getInstance().getCreationName(),
                    Storage.getInstance().getQuery(), Storage.getInstance().getAudioChucks(), Storage.getInstance().getMusic());
            creationWorker.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
