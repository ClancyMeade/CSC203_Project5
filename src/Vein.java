import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public class Vein extends ActiveEntity {

    public Vein(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images, actionPeriod);
    }


    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Point> openPt = world.findOpenAround(this.getPosition());

        if (openPt.isPresent()) {
            ActiveEntity ore = CreateFactory.createOre(WorldLoader.ORE_ID_PREFIX + this.getId(), openPt.get(),
                    WorldLoader.ORE_CORRUPT_MIN + WorldLoader.rand.nextInt(
                            WorldLoader.ORE_CORRUPT_MAX - WorldLoader.ORE_CORRUPT_MIN),
                    imageStore.getImageList(WorldLoader.ORE_KEY));
            world.addEntity(ore);
            ore.scheduleAction(scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                CreateFactory.createActivityAction(this, world, imageStore),
                this.getActionPeriod());
    }
}
