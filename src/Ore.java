import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Ore extends ActiveEntity {

    public Ore(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images, actionPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Point pos = this.getPosition();

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        ActiveEntity blob = CreateFactory.createOreBlob(this.getId() + WorldLoader.BLOB_ID_SUFFIX, pos,
                this.getActionPeriod() / WorldLoader.BLOB_PERIOD_SCALE,
                WorldLoader.BLOB_ANIMATION_MIN + WorldLoader.rand.nextInt(
                        WorldLoader.BLOB_ANIMATION_MAX
                                - WorldLoader.BLOB_ANIMATION_MIN),
                imageStore.getImageList(WorldLoader.BLOB_KEY));

        world.addEntity(blob);
        blob.scheduleAction(scheduler, world, imageStore);
    }
}
