import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public abstract class Entity {
    private String id;
    private Point position;
    private List<PImage> images;

    public Entity(String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
    }

    protected String getId() {
        return this.id;
    }

    protected Point getPosition() {
        return this.position;
    }

    protected void setPosition(Point pos) {
        this.position = pos;
    }

    protected List<PImage> getImages() {
        return this.images;
    }

    protected PImage getCurrentImage() {
        return this.getImages().get(0);
    }

}