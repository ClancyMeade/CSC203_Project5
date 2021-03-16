import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Miner extends AnimatedEntity {
    private int resourceLimit;
    private int resourceCount;
    //private PathingStrategy strategy = new SingleStepPathingStrategy();
    private PathingStrategy strategy = new AStarPathingStrategy();


    public Miner(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
    }

    protected Point nextPositionMiner(WorldModel world, Point destPos) {
//        int horiz = Integer.signum(destPos.getX() - this.getPosition().getX());
//        Point newPos = new Point(this.getPosition().getX() + horiz, this.getPosition().getY());
//
//        if (horiz == 0 || world.isOccupied(newPos)) {
//            int vert = Integer.signum(destPos.getY() - this.getPosition().getY());
//            newPos = new Point(this.getPosition().getX(), this.getPosition().getY() + vert);
//
//            if (vert == 0 || world.isOccupied(newPos)) {
//                newPos = this.getPosition();
//            }
//        }
//
//        return newPos;

        Point startPos = this.getPosition();
        Predicate<Point> canPassThrough = (Point p) -> world.withinBounds(p) && (!(world.isOccupied(p)));
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



    protected int getResourceLimit() {
        return this.resourceLimit;
    }

    protected int getResourceCount() {
        return this.resourceCount;
    }

    protected void incrementResourceCount()
    {
        this.resourceCount += 1;
    }

    protected boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            moveToHelper(world, target, scheduler);
            return true;
        } else {
            Point nextPos = this.nextPositionMiner(world, target.getPosition());

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

    protected abstract void moveToHelper(WorldModel world, Entity target, EventScheduler scheduler);

}
