import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class OreBlob extends AnimatedEntity {
    private PathingStrategy strategy = new SingleStepPathingStrategy();
    public OreBlob(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }


    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> blobTarget = world.findNearest(this.getPosition(), Vein.class);
        long nextPeriod = this.getActionPeriod();

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().getPosition();

            if (this.moveToOreBlob(world, blobTarget.get(), scheduler)) {
                ActiveEntity quake = CreateFactory.createQuake(tgtPos, imageStore.getImageList(WorldLoader.QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += this.getActionPeriod();
                quake.scheduleAction(scheduler, world, imageStore);
            }
        }
        scheduler.scheduleEvent(this, CreateFactory.createActivityAction(this, world, imageStore), nextPeriod);
    }

    public boolean moveToOreBlob(
            WorldModel world,
            Entity target,
            EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        } else {
            Point nextPos = this.nextPositionOreBlob(world, target.getPosition());

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

    public Point nextPositionOreBlob(WorldModel world, Point destPos) {
//        int horiz = Integer.signum(destPos.getX() - this.getPosition().getX());
//        Point newPos = new Point(this.getPosition().getX() + horiz, this.getPosition().getY());
//
//        Optional<Entity> occupant = world.getOccupant(newPos);
//
//        if (horiz == 0 || (occupant.isPresent() && !(occupant.get().getClass()
//                == Ore.class))) {
//            int vert = Integer.signum(destPos.getY() - this.getPosition().getY());
//            newPos = new Point(this.getPosition().getX(), this.getPosition().getY() + vert);
//            occupant = world.getOccupant(newPos);
//
//            if (vert == 0 || (occupant.isPresent() && !(occupant.get().getClass()
//                    == Ore.class))) {
//                newPos = this.getPosition();
//            }
//        }
//
//        return newPos;

        Point startPos = this.getPosition();
        Predicate<Point> canPassThrough = (Point p) -> (world.withinBounds(p)) && (!(world.isOccupied(p)) || (world.getOccupant(p).isPresent() && (world.getOccupant(p).get().getClass() == Ore.class)));
        BiPredicate<Point, Point> withinReach = (Point p, Point b) -> neighbors(p, b); //within reach is not used
        Function<Point, Stream<Point>> potentialNeighbors = strategy.CARDINAL_NEIGHBORS;
        List<Point> path = strategy.computePath(startPos, destPos, canPassThrough, withinReach, potentialNeighbors);
        if(path.size() == 0)
        {
            return startPos; //No where to go, return current position
        }
        return path.get(0);
    }

    public boolean neighbors(Point p1, Point p2)
    {
        return p1.getX()+1 == p2.getX() && p1.getY() == p2.getY() ||
                p1.getX()-1 == p2.getX() && p1.getY() == p2.getY() ||
                p1.getX() == p2.getX() && p1.getY()+1 == p2.getY() ||
                p1.getX() == p2.getX() && p1.getY()-1 == p2.getY();
    }

}
