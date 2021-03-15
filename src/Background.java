
import java.util.List;

import processing.core.PImage;

public final class Background {
    private String id;
    private List<PImage> images;
    private int imageIndex;

    public Background(String id, List<PImage> images) {
        this.id = id;
        this.images = images;
    }

    public List<PImage> getImages() {
        return this.images;
    }

    public int getImageIndex() {
        return this.imageIndex;
    }

    public PImage getCurrentImage()
    {
        if(this.getClass() != Background.class)
        {
            throw new UnsupportedOperationException(
                    String.format("getCurrentImage not supported for %s",
                            this));
        }
        else {
            return this.getImages().get(
                    this.getImageIndex());
        }
    }


}