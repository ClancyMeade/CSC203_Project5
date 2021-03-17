import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Knight extends AnimatedEntity{

    public Knight(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod){
        super(id, position,images, actionPeriod,animationPeriod);
    }
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> dragonTarget = world.findNearest(this.getPosition(), Dragon.class);

        if (dragonTarget.isPresent() && this.moveTo(world, dragonTarget.get(), scheduler)) {
            //this.transformFull(world, scheduler, imageStore);
            // do what we do once we reach dragon

                Dragon  d = (Dragon) dragonTarget.get();
                d.decrementLife(world, scheduler);

            this.die(world, scheduler,imageStore);
        } else {
            scheduler.scheduleEvent(this,
                    CreateFactory.createActivityAction(this, world, imageStore),
                    this.getActionPeriod());
        }
    }
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            //moveToHelper(world, target, scheduler);
            return true;
        } else {
            Point nextPos = this.nextPositionKnight(world, target.getPosition());

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
    protected Point nextPositionKnight(WorldModel world, Point destPos) {
        Point startPos = this.getPosition();
        Predicate<Point> canPassThrough = (Point p) -> world.withinBounds(p) && (!(world.isOccupied(p)));
        BiPredicate<Point, Point> withinReach = (Point p1, Point p2) -> (p1.getX() == p2.getX() && Math.abs(p1.getY() - p2.getY()) <= 1) || (p1.getY() == p2.getY() && Math.abs(p1.getX() - p2.getX()) <= 1);     Function<Point, Stream<Point>> potentialNeighbors = super.getPathingStrategy().CARDINAL_NEIGHBORS;

        List<Point> path = super.getPathingStrategy().computePath(startPos, destPos, canPassThrough, withinReach, potentialNeighbors);
        if(path.size() == 0)
        {
            return startPos; //No where to go, return current position
        }
        return path.get(0);

    }
    public void die(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
    }
}
