package entities.drawing;


import javafx.scene.image.Image;

/**
 * Created by aliss on 11/13/2017.
 */
public class ImageStash {
    private String filename;
    private Image currImg;

    public ImageStash(String filename) {
        this.filename = filename;
    }

    public void updateFile (String filename) {
        this.filename = filename;
    }

    public Image getImage() {
        if (currImg == null) {
            currImg = new Image(filename);
        }
        return currImg;
    }

}
