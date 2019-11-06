package VARpedia.resources;

import VARpedia.creationMenu.AudioChunk;

import java.util.List;

public class Storage {

    private static Storage _storage;
    private  String _nameOfCreation;
    private  String _query;
    private List<AudioChunk> _audioChunks;
    private String _music;
    private String _numImages;


    public static Storage getInstance(){
        if (_storage == null){
            _storage = new Storage();
        }
        return _storage;
    }

    public void setMusic(String music) {
    	_music = music;
    }
    
    public String getMusic() {
    	return _music;
    }
    
    public void setNameOfCreation(String nameOfCreation){
        _nameOfCreation = nameOfCreation;
    }

    public String getCreationName(){
        return _nameOfCreation;
    }

    public void setQuery(String query){
        _query = query;
    }

    public String getQuery(){
        return _query;
    }

    public void setAudioChunks(List<AudioChunk> audioChunks) {
        _audioChunks = audioChunks;
    }

    public List<AudioChunk> getAudioChucks(){
        return _audioChunks;
    }

    public void setNumImages(String n){
        _numImages=n;
    }

    public String getNumImages(){
        return _numImages;
    }

    public boolean isClear(){
        if ((_nameOfCreation == null) && (_query == null) && (_audioChunks == null) && (_music == null)){
            return true;
        } else {
            return false;
        }
    }

    public void clearAll(){
        _audioChunks = null;
        _query=null;
        _audioChunks=null;
        _storage = null;
        _music = null;
    }
}
