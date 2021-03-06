import processing.core.PImage;

import java.util.List;

public final class CreateFactory
{
    public static Action createAnimationAction(AnimatedEntity entity, int repeatCount) { //Leave it here for now, maybe world
        return new Animation(entity, null, null,
                repeatCount);
    }

    public static Action createActivityAction( //Leave
                                               ActiveEntity entity, WorldModel world, ImageStore imageStore) {
        return new Activity(entity, world, imageStore, 0);
    }

    public static Blacksmith createBlacksmith(
            String id, Point position, List<PImage> images) {
        return new Blacksmith(id, position, images);
    }
    public static Barrack createBarrack(
            String id, Point position, List<PImage> images, int actionPeriod) {
        return new Barrack(id, position, images, actionPeriod);
    }
    public static Knight createKnight(
            String id, Point position, List<PImage> images, int actionTime, int animationTime) {
        return new Knight(id,position,images,actionTime, animationTime);
    }

    public static MinerFull createMinerFull(
            String id,
            int resourceLimit,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images) {
        return new MinerFull(id, position, images,
                resourceLimit, resourceLimit, actionPeriod,
                animationPeriod);
    }

    public static MinerNotFull createMinerNotFull(
            String id,
            int resourceLimit,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images) {
        return new MinerNotFull(id, position, images,
                resourceLimit, 0, actionPeriod, animationPeriod);
    }


    public static Obstacle createObstacle(
            String id, Point position, List<PImage> images) {
        return new Obstacle(id, position, images);
    }

    public static Ore createOre(
            String id, Point position, int actionPeriod, List<PImage> images) {
        return new Ore(id, position, images, actionPeriod);
    }

    public static OreBlob createOreBlob(String id, Point position, int actionPeriod, int animationPeriod, List<PImage> images)
    {
        return new OreBlob(id, position, images, actionPeriod, animationPeriod);
    }

    public static Quake createQuake(Point position, List<PImage> images) {
        return new Quake(WorldLoader.QUAKE_ID, position, images, WorldLoader.QUAKE_ACTION_PERIOD, WorldLoader.QUAKE_ANIMATION_PERIOD);
    }

    public static Vein createVein(
            String id, Point position, int actionPeriod, List<PImage> images) {
        return new Vein(id, position, images, actionPeriod);
    }

    public static Dragon createDragon(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod, int lifeCount)
    {
        return new Dragon(id, position, images, actionPeriod, animationPeriod, lifeCount);
    }

    public static Fence createFence(String id, Point position, List<PImage> images)
    {
        return new Fence(id, position, images);
    }

    public static Fire createFire(Point position, List<PImage> images)
    {
        return new Fire(WorldLoader.FIRE_ID, position, images, WorldLoader.FIRE_ACTION_PERIOD, WorldLoader.FIRE_ANIMATION_PERIOD);
    }
}
