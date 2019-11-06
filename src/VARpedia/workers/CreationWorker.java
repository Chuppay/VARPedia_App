package VARpedia.workers;

import VARpedia.creationMenu.AudioChunk;
import VARpedia.popUps.PopUpWindow;
import VARpedia.resources.Storage;
import javafx.application.Platform;
import java.io.File;
import java.util.List;

/**
 * This class is a worker class which creates the creation in the background.
 */

public class CreationWorker extends Thread{

    private final String _creationName;
    private final String _query;
    private final List<AudioChunk> _chunks;
    private final String _music;

    public CreationWorker (String name,String searchTerm,List<AudioChunk> chunks, String music){
        _creationName = name;
        _query = searchTerm;
        _chunks = chunks;
        _music = music;
    }

    @Override
    public void run(){
        try {

            // Creates audio files from the the list of Audio Chunks
            File dir = new File(System.getProperty("user.dir") + System.getProperty("file.separator") +
                    "Creations" + System.getProperty("file.separator") + _creationName);
            int i = 0;
            for (AudioChunk chunk : _chunks){
                String cmd ="";
                // Checks to see which synthesizer to use
                if (chunk.getSpeechSynthesizer().equals("eSpeak")) {
                    cmd = "espeak '" + chunk.getTextProperty() + "' -w " + i  + ".wav";
                } else if (chunk.getSpeechSynthesizer().equals("Festival")) {
                    cmd = "echo '" + chunk.getTextProperty() + "' | text2wave -o " +i + ".wav";
                }
                i++;
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
                pb.directory(dir);

                Process process = pb.start();
                process.waitFor();
            }

            // Combines the audio files
            String cmd = "$(ffmpeg ";
            for (int j = 0; j < _chunks.size(); j++){
                cmd += "-i " + j + ".wav ";
            }
            cmd += "-filter_complex '";
            for (int k =0; k < _chunks.size();k++){
                cmd += "[" + k +":0]";
            }
            cmd += "concat=n=" + _chunks.size()+":v=0:a=1[out]' -map '[out]' out.wav ) ; ";
            String audioName = "out.wav";
            
            if (_music != "No Music") { //ensures music only added when selected
            	cmd += "ffmpeg -i out.wav -i ../../src/VARpedia/resources/music/"+ _music +".mp3 -filter_complex amix=inputs=2:duration=shortest output.mp3;";
            	audioName = "output.mp3"; //use overlayed audio file with music instead of raw audio chunk file.
            }

            // Combines all the images into a slideshow and merges it with the audio to form creation
            cmd += "$( cat *.jpg | ffmpeg -f image2pipe -framerate \"$(ls *.jpg | wc -l)/$(soxi -D out.wav)\" -i - -vf " +
                    "\"scale=w=600:h=400:force_original_aspect_ratio=1,pad=600:400:(ow-iw)/2:(oh-ih)/2\" -r 25 -y  out.mp4) ; " +
                    "ffmpeg -i ./out.mp4 -filter_complex \"drawtext=text='"+ _query +
                    "':fontcolor=white:fontsize=32:x=(w-text_w)/2:y=(h-text_h)/2\" -y ./visual.mp4 \n" +
                    "$(ffmpeg -i visual.mp4 -i "+ audioName + " -strict -2 \""+_creationName+"\".mp4) ; mv  " + _creationName + ".mp4;";
            ProcessBuilder pb = new ProcessBuilder("bash","-c",cmd);
            pb.directory(dir);
            Process p = pb.start();
            p.waitFor();

        } catch (Exception e) {
            e.printStackTrace();

        }

        // signals that the Creation is done
        Platform.runLater(() -> {
            try {
                PopUpWindow.getStage().close();
                PopUpWindow.popUP("Success!", "Your file: " + _creationName + " has been created!");
                Storage.getInstance().clearAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

}

