package VARpedia.quiz;

import java.io.File;
import java.util.Random;

import VARpedia.VarpediaApp;
import VARpedia.popUps.PopUpWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This class is the controller for the quiz. The quiz is the active learning component of the VARpedia app. This class
 * is in charge of managing the quiz and keeping the score of the user based on how many guess the user gets correct.
 * The quiz displays an image which the user has to guess search term for.
 */

public class QuizController {

	@FXML
	TextField _guessField;
	
	@FXML
	ImageView _imageView;
	
	@FXML
	Label _scoreLabel;
	
	private int _score;
	private int _initScore; //The initial score (corresponds to number of images)
	private ObservableList<File> _images = FXCollections.observableArrayList();

	/**
	 * This method handles the event where the user clicks the guess button.
	 */
	public void guessPressed(ActionEvent event) {

		// Determines if the guess is correct
		String guess = _guessField.getText();
		File file = new File (_imageView.getImage().getUrl());
		String filename = file.getName();
		String term = filename.substring(0, filename.indexOf('-'));
		
		if (guess.toLowerCase().contentEquals(term.toLowerCase())) {
			_score ++;
			_scoreLabel.setText("Score: " + _score + "/" + _initScore);
		} 

		// Displays the score when there are no more images left
		if (_images.size()<=0) {
			PopUpWindow.popUP("Congratulations!","Your final score: " + _score + "/" + _initScore);
			try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VARpedia/quiz/QuizMenu.fxml"));
	            VarpediaApp.getStage().setScene(new Scene(loader.load()));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		} else {
			newQuestion();
		}
		
	}

	/**
	 * This method changes the scene back to the main menu
	 */
	public void backPressed(ActionEvent event) {
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VARpedia/quiz/QuizMenu.fxml"));
            VarpediaApp.getStage().setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	/**
	 * This method loads a new question
	 */
	public void newQuestion() {

		// picks a random image to be displayed
		Random r = new Random();
		int randomNum = r.nextInt(_images.size());
		Image image = new Image(_images.get(randomNum).toURI().toString());
	    _imageView.setImage(image);
	    
	    //remove image so no duplicate questions
	    _images.remove(randomNum);
		
	}

	/**
	 * This method gets images prepared to use for the quiz
	 */
	public void setImages(ObservableList<String> list) {
		_images.clear();
		
		//This filter determines what is deemed a valid image. Change extension to match output of bash scripts
		java.io.FileFilter filter = file -> !file.isHidden() && (file.isDirectory() || (file.getName().endsWith(".jpg")));
		
		for (String creation : list) {
			File dir = new File("./Creations/" + creation);
			if (dir.isDirectory()) {	
				_images.addAll(dir.listFiles(filter));	
			}
		}

		_initScore = _images.size();
		_score = 0;
		_scoreLabel.setText("Score: " + _score + "/" + _initScore);
	}
		
}
