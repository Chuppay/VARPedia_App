package VARpedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.text.Font;
import javafx.stage.Stage;

public class VarpediaApp extends Application {

    private static Stage _stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        _stage = primaryStage;
        Font.loadFont(getClass().getResourceAsStream("/VARpedia/resources/DKCoolCrayon.ttf"),48);
        Parent root = FXMLLoader.load(getClass().getResource("mainMenu/MainMenu.fxml"));
        _stage.setScene(new Scene(root, 1200, 720));
        _stage.setTitle("Varpedia");
        _stage.setResizable(false);
        _stage.show();
        
        //Making directories using bash script
        String mkDirs = "if [ ! -e ./Creations ]; then\n" + 
				"			mkdir ./Creations\n" + 
				"	fi";
		executeBashCommand(mkDirs);
    }

    public static Stage getStage(){
        return _stage;
    }

    /**
     * This method executes the bash command passed through as a parameter
     */
    public static void executeBashCommand(String cmd) {
		try {
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			pb.start().waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * This method scans through the creation directory and returns a list of all the creations.
     */
	public static List<String> scanForCreations(){

        // deletes any incomplete creations
        File dir = new File("./Creations");
        File[] creationsDir =dir.listFiles();
        for (File f : creationsDir){
            File audioFile = new File(f.getAbsolutePath()+"/"+f.getName()+".mp4");
            if (!audioFile.exists()){
                String cmd = "rm -r Creations/"+f.getName();
                VarpediaApp.executeBashCommand(cmd);
            }
        }

        List<String> creations = new ArrayList<String>();
        try {
            // Scans through the Creations directory to see if there are any directories and outputs it
            String command = "ls -d */ | cut -d'/' -f 1";
            ProcessBuilder scanForCreations = new ProcessBuilder("bash", "-c", command);
            scanForCreations.directory(new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "Creations"));
            Process scanForCreationsProcess = scanForCreations.start();

            BufferedReader stdout = new BufferedReader(new InputStreamReader(scanForCreationsProcess.getInputStream()));

            // Adds the creations to a list that is then displayed to the user through a combobox.
            int exitStatus = scanForCreationsProcess.waitFor();
            String line;

            if (exitStatus == 0) {
                while ((line = stdout.readLine()) != null) {
                    creations.add(line);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return creations;
    }

    
    public static void main(String[] args) {
        launch(args);
    }
}
