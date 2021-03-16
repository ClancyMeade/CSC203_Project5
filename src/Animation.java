public class Animation implements Action
{
    private AnimatedEntity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;
    //git testing
    //another
    public Animation(
            AnimatedEntity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler)
    {
        this.entity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent((Entity)this.entity,
                    CreateFactory.createAnimationAction(this.entity, Math.max(this.repeatCount - 1, 0)),
                    this.entity.getAnimationPeriod());
        }
    }

}
