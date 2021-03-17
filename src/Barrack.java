import processing.core.PImage;

import java.util.List;

public class Barrack extends ActiveEntity{

    public Barrack(String id, Point position, List<PImage> images, int activityPeriod) {
        super(id, position, images, activityPeriod);
    }
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Point pos = new Point(super.getPosition().getX(), super.getPosition().getY() + 1);
        if(world.findNearest(pos, Dragon.class).isEmpty()){return;}
        System.out.println("here");
        ActiveEntity knight = CreateFactory.createKnight("knight",pos, imageStore.getImageList(WorldLoader.KNIGHT_KEY), WorldLoader.KNIGHT_ACTION_TIME, WorldLoader.KNIGHT_ANIMATION_TIME);

        world.addEntity(knight);
        knight.scheduleAction(scheduler, world, imageStore);
        scheduler.scheduleEvent(this,
                CreateFactory.createActivityAction(this, world, imageStore),
                this.getActionPeriod());
    }
}
