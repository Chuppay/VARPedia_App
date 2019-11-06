package VARpedia.workers;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.photos.Size;

/**
 * This class is a worker class which retrieves images from Flickr using the Flickr REST API. There are API keys
 * provided by XinMing Yang for the purpose of demonstration only.
 */

public class APIFlickrWorker extends Thread{

    private final String _numImages;
    private final String _searchTerm;
    private final String _creationName;

    public APIFlickrWorker(String searchTerm,String numImages, String creationName){
        _searchTerm = searchTerm;
        _numImages = numImages;
        _creationName = creationName;
    }

    /**
     * This code was taken from the 206 ACP FlickrExample supplied by Nasser Giacaman.
     */
    @Override
    public void run() {
        try {
            // These keys are temporary, this is hardcoded for the purpose of the demonstration of functionality
            String apiKey = "XXXXXXXXXXXX";
            String sharedSecret = "XXXXXXXXXXXX";

            Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());

            PhotosInterface photos = flickr.getPhotosInterface();
            SearchParameters params = new SearchParameters();
            params.setSort(SearchParameters.RELEVANCE);
            params.setMedia("photos");
            params.setText(_searchTerm);

            PhotoList<Photo> results = photos.search(params, Integer.parseInt(_numImages), 0);

            // Downloads each of the photos that was returned by the API
            for (Photo photo: results) {
                try {
                    BufferedImage image = photos.getImage(photo,Size.LARGE);
                    // renames the file so that it is valid.
                    String filename = _searchTerm.trim().replace(' ', '_')+"-"+System.currentTimeMillis()+"-"+photo.getId()+".jpg";
                    File outputFile = new File("Creations/" + _creationName,filename);
                    ImageIO.write(image, "jpg", outputFile);
                } catch (FlickrException fe) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
