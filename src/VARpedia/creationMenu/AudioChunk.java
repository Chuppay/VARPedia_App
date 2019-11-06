package VARpedia.creationMenu;

import VARpedia.VarpediaApp;

/**
 * This class serves as a placeholder for an audio file. An AudioChunk object has the chosen synthensiser, and the
 * selected text to be converted into an audio file. Creation of the audio file happens only when the creation
 * is finalised.
 */

public class AudioChunk {

    private String _textProperty;
    private String _speechSynthesizer;

    public AudioChunk(String text, String speechSynthesizer) {
        _textProperty = text;
        _speechSynthesizer = speechSynthesizer;
    }

    /**
     * This method takes a string path and plays audio from espeak or festival
     */
    public void playAudioChunk() {
        String cmd="";

        // Checks to see the synthesizer and uses the the appropriate command to play it
        if (_speechSynthesizer.equals("eSpeak")) {
            cmd = "espeak '" + _textProperty + "'";
        } else if (_speechSynthesizer.equals("Festival")) {
            cmd = "echo '" + _textProperty +"' | festival --tts";
        }
        VarpediaApp.executeBashCommand(cmd);
    }

    public String getSpeechSynthesizer() {
        return _speechSynthesizer;
    }

    public String getTextProperty() {
        return _textProperty;
    }

    public String toString(){
        return _textProperty;
    }

}