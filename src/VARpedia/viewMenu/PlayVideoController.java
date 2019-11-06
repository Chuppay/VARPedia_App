package VARpedia.viewMenu;

import java.io.File;
import java.io.IOException;

import VARpedia.VarpediaApp;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

/**
 * This class is the controller for the play video scene. It plays creation selected in the view menu scene.
 */

public class PlayVideoController {

	@FXML
	AnchorPane _pane;
	
	@FXML
	Button _back, _pause, _mute;
	
	@FXML
	Label _label, _nameLabel;

	@FXML
	Slider _timeSlider;

	MediaPlayer _player;

	/**
	 * Stops the player and returns back to the main menu
	 */
	public void pressedBack(ActionEvent event) {
		try {
            _player.stop();
            _player.dispose();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/VARpedia/viewMenu/ViewMenu.fxml"));
			VarpediaApp.getStage().setScene(new Scene(loader.load()));
			ViewMenuController controller = loader.getController();
			controller.refreshList();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	/**
	 * Play pauses the video
	 */
	public void pressedPause(ActionEvent event) {
		// checks the status of the video and assigns the button text according
		if ((_player.getStatus() == Status.PLAYING) && (_player.getTotalDuration().equals(_player.getCurrentTime()))) {
			Duration d = new Duration(0);
			_player.seek(d);
			_pause.setText("▶ Play");

		} else if(_player.getStatus() == Status.PLAYING) {
			_player.pause();
			_pause.setText("▶ Play");
		}else {
			_player.play();
			_pause.setText("Pause");
		}
		
	}

	/**
	 * Mutes and unmutes the video
	 */
	public void pressedMute(ActionEvent event) {
		// Reverses the sound state of the player
		_player.setMute( !_player.isMute() );
		// Checks the state and assigns text accordingly
		if (_player.isMute()) {
			_mute.setText("unmute");
		} else {
			_mute.setText("mute");
		}
	}

	/**
	 * Plays the given creation given the file URL
	 */
	public void play(File fileUrl) {

		// Displays file name
		String name = fileUrl.getName();
		name =name.substring(0, (name.length()-4));
		_nameLabel.setText(name);

		// Displays the video in the centered pane
		Media video = new Media(fileUrl.toURI().toString());
		_player = new MediaPlayer(video);
		_player.setAutoPlay(true);
		MediaView mediaView = new MediaView(_player);
		_pane.getChildren().addAll(mediaView);

		// Allows for the time/duration of the video to be display
		_player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
					Duration newValue) {
				String time = "";
				time += String.format("%02d", (int)newValue.toMinutes());
				time += ":";
				time += String.format("%02d", (int)newValue.toSeconds());
				_label.setText(time);
				
				Duration total = _player.getTotalDuration();
				Duration current = _player.getCurrentTime();
				
				if (total.lessThan(current.add(new Duration(200)))){ //Check we are at the end of the video
					_pause.setText("replay");
				}

				// updates the slider to show the whereabouts of the progress of the video
				if (!_timeSlider.isValueChanging()) {
					_timeSlider.setValue((newValue.toSeconds()/_player.getTotalDuration().toSeconds())*100);
				}
			}
		});
	}

	
}
