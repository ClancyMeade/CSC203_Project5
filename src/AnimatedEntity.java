import processing.core.PImage;

import java.util.List;

public abstract class AnimatedEntity extends ActiveEntity {
    private int animationPeriod;
    private int imageIndex;
    private PathingStrategy ps= new AStarPathingStrategy();
    public AnimatedEntity(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod);
        this.animationPeriod = animationPeriod;
        this.imageIndex = 0;
    }

    protected PathingStrategy getPathingStrategy(){return ps;}
    protected int getAnimationPeriod() {
        return this.animationPeriod;
    }

    protected void nextImage() {
        this.imageIndex = (this.imageIndex + 1) % this.getImages().size();
    }


    protected int getImageIndex() {
        return this.imageIndex;
    }

    @Override
    protected PImage getCurrentImage() {
        return this.getImages().get(this.getImageIndex());
    }

    @Override
    protected void scheduleAction(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        super.scheduleAction(scheduler, world, imageStore);

        scheduler.scheduleEvent(this, CreateFactory.createAnimationAction(this, 0), this.getAnimationPeriod());
    }


}
