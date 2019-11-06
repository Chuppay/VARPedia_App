package VARpedia.creationMenu;


import java.io.File;
import java.io.IOException;

import VARpedia.VarpediaApp;
import VARpedia.resources.Storage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;

/**
 * This class is the controller for the music menu. It gives the user the option to include music in their creation.
 */

public class MusicMenuController {

	@FXML
	ComboBox<String> _musicBox;

	@FXML
	Button _play;
	
	@FXML
	public void initialize() {
		refreshList();
		_musicBox.getItems().add("No Music");
		_musicBox.getSelectionModel().select("No Music");
	}
	
	/**
	 * This method takes the user back to the chunk selection scene
	 */
	public void backPressed(ActionEvent event){
        try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/VARpedia/creationMenu/ChunkSelectMenu.fxml"));
			Parent root = loader.load();
			ChunkSelectionMenuController controller = loader.getController();
			controller.setOldChunks();
			VarpediaApp.getStage().setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * This method takes the user to the image selection menu.
	 */
	public void continuePressed(ActionEvent event){
		String selected = _musicBox.getSelectionModel().getSelectedItem();

		// Stores the music to be used later in a different class
		Storage storage = Storage.getInstance();
		storage.setMusic(selected);

        try {
            // Changes scene to the image previewer
            Parent getImages = FXMLLoader.load(getClass().getResource("/VARpedia/creationMenu/ImageSelectionMenu.fxml"));
            VarpediaApp.getStage().setScene(new Scene(getImages));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

	/**
	 * This method gets the the music resources
	 */
	public void refreshList() {
    	_musicBox.getItems().clear();
    	java.io.FileFilter filter = file -> !file.isHidden() && (file.isDirectory() || (file.getName().endsWith(".mp3")));
		File dir = new File( System.getProperty("user.dir") + "/music");
		if (dir.isDirectory()) {	
			File[] files = dir.listFiles(filter);
			for (File f: files) {
				String str = f.getName();
				str = str.replace(".mp3", "");
				_musicBox.getItems().add(str);
			}
		}
    }

	public void pressedPlay() {
		String selected = _musicBox.getSelectionModel().getSelectedItem();
		if (!selected.contentEquals("No Music")) {
			String cmd = "ffplay -nodisp ./src/VARpedia/resources/music/"+selected+ ".mp3 -t 00:00:08";
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			try {
				pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
	}
}
