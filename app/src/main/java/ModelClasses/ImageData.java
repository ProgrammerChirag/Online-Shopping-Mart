package ModelClasses;

import java.io.Serializable;
import java.util.List;

public class ImageData implements Serializable {
    List<String> imageData;


    public ImageData(List<String> imageData) {
        this.imageData = imageData;
    }

    public List<String> getImageData() {
        return imageData;
    }

    public void setImageData(List<String> imageData) {
        this.imageData = imageData;
    }
}
