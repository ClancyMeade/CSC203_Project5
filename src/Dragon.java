import processing.core.PImage;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Dragon extends AnimatedEntity
{
    private int lifeCount;
    private PathingStrategy strategy = new AStarPathingStrategy();
    public Dragon(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod, int lifeCount) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.lifeCount = lifeCount;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> dragonTarget = world.findNearest(this.getPosition(), Fence.class);

        if (dragonTarget.isPresent()) {
            Point tgtPos = dragonTarget.get().getPosition();

            if (this.moveTo(world, dragonTarget.get(), scheduler))
            {
                //Do whatever happens when dragon encounters fence
                //Maybe create new entity fire that occurs after the fence is destroyed (like quake):

                //ActiveEntity fire = CreateFactory.createFire(tgtPos, imageStore.getImageList(WorldLoader.FIRE_KEY));
                //world.addEntity(fire);
                //nextPeriod += this.getActionPeriod();
                //fire.scheduleAction(scheduler, world, imageStore);
            }
        }
        scheduler.scheduleEvent(this, CreateFactory.createActivityAction(this, world, imageStore), this.getActionPeriod());
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition()))
        {
            world.removeEntity(target); //remove fence
            scheduler.unscheduleAllEvents(target);
            return true;
        }

        else {
            Point nextPos = this.nextPositionDragon(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    public Point nextPositionDragon(WorldModel world, Point destPos) {
        Point startPos = this.getPosition();
        Predicate<Point> canPassThrough = (Point p) -> (world.withinBounds(p)) && (!(world.isOccupied(p)) || (world.getOccupant(p).isPresent() && (world.getOccupant(p).get().getClass() == Fence.class)));
        BiPredicate<Point, Point> withinReach = this::neighbors;
        Function<Point, Stream<Point>> potentialNeighbors = strategy.CARDINAL_NEIGHBORS;
        List<Point> path = strategy.computePath(startPos, destPos, canPassThrough, withinReach, potentialNeighbors);
        if(path.size() == 0)
        {
            return startPos; //No where to go, return current position
        }
        return path.get(0);
    }

    public void decrementLife()
    {
        this.lifeCount = this.lifeCount -1;
    }


    public boolean neighbors(Point p1, Point p2)
    {
        return p1.getX()+1 == p2.getX() && p1.getY() == p2.getY() ||
                p1.getX()-1 == p2.getX() && p1.getY() == p2.getY() ||
                p1.getX() == p2.getX() && p1.getY()+1 == p2.getY() ||
                p1.getX() == p2.getX() && p1.getY()-1 == p2.getY();
    }
}
