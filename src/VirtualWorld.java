import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


import processing.core.*;

public final class VirtualWorld extends PApplet {
    private static final int TIMER_ACTION_PERIOD = 100;

    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    private static final int WORLD_WIDTH_SCALE = 2;
    private static final int WORLD_HEIGHT_SCALE = 2;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
    private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static final String LOAD_FILE_NAME = "world.sav";

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private static final int KNIGHT_ACTION_TIME = 5;
    private static final int KNIGHT_ANIMATION_TIME = 6;

    private static double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    private long nextTime;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        this.imageStore = new ImageStore(
                createImageColored(TILE_WIDTH, TILE_HEIGHT,
                        DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
                createDefaultBackground(imageStore));
        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH,
                TILE_HEIGHT);
        this.scheduler = new EventScheduler(timeScale);

        loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        loadWorld(world, LOAD_FILE_NAME, imageStore);

        scheduleActions(world, scheduler, imageStore);

        nextTime = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
    }

    public void draw() {
        long time = System.currentTimeMillis();
        if (time >= nextTime) {
            this.scheduler.updateOnTime(time);
            nextTime = time + TIMER_ACTION_PERIOD;
        }
        view.drawViewport();
    }
    public void mousePressed()
    {
        Point pressed = mouseToPoint(mouseX, mouseY);
        //add fence object at points 10 by 10 around this
        int topRightX = pressed.getX() + 5;
        int topRightY = pressed.getY() - 5;
        ArrayList<Point> dragonPoints = new ArrayList<>();
        for(int i = 0; i <= 10; i++){
            Point p1 = new Point(topRightX - i, topRightY);
            Point p2 = new Point(topRightX - i, topRightY + 10);
            Point p3 = new Point(topRightX, topRightY + i);
            Point p4 = new Point(topRightX - 10, topRightY + i);
            Point[] points = {p1,p2,p3,p4};
            for(Point p: points ){
                Fence fence = CreateFactory.createFence("fence", p, this.imageStore.getImageList("fence"));
                if(world.isOccupied(p)) {
                    if (world.getOccupant(p).get() instanceof Miner){
                        dragonPoints.add(p);
                    }
                world.removeEntityAt(p);
                }
                this.world.tryAddEntity(fence);

            }
            //comment
            // a fence must be added to each of these points
            //create fence and add to world but idk what class to do that in
        }
//         now we add our knights and convert dragons
        Knight knight = CreateFactory.createKnight("Knight1", pressed, this.imageStore.getImageList("knight"), KNIGHT_ACTION_TIME, KNIGHT_ANIMATION_TIME);
        if(!world.isOccupied(pressed)) {
            this.world.tryAddEntity(knight);
        }
        knight.scheduleAction(scheduler,world,imageStore);

        for(Point np : dragonPoints) {
            Dragon dragon = CreateFactory.createDragon("dragon", np, this.imageStore.getImageList("dragon"), KNIGHT_ACTION_TIME, KNIGHT_ANIMATION_TIME, 2);
            if (world.isOccupied(np) &&world.getOccupant(np).get().getClass().equals(Fence.class)){
                this.world.removeEntityAt(np);
                this.world.tryAddEntity(dragon);
                dragon.scheduleAction(scheduler,world,imageStore);
            }else if(!world.isOccupied(np)){
                this.world.tryAddEntity(dragon);
                dragon.scheduleAction(scheduler,world,imageStore);
            }
        }
        //redraw();

    }

    private Point mouseToPoint(int x, int y)
    {
        return new Point(mouseX/TILE_WIDTH, mouseY/TILE_HEIGHT);
    }
    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP:
                    dy = -1;
                    break;
                case DOWN:
                    dy = 1;
                    break;
                case LEFT:
                    dx = -1;
                    break;
                case RIGHT:
                    dx = 1;
                    break;
            }
            view.shiftView(dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME,
                imageStore.getImageList(
                        DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }

    private static void loadImages(
            String filename, ImageStore imageStore, PApplet screen) {
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in, screen);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void loadWorld(
            WorldModel world, String filename, ImageStore imageStore) {
        try {
            Scanner in = new Scanner(new File(filename));
            WorldLoader.load(in, world, imageStore);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void scheduleActions(
            WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.getEntities()) {
            if(entity.getClass()!=Blacksmith.class && entity.getClass()!= Obstacle.class) {
                ((ActiveEntity) entity).scheduleAction(scheduler, world, imageStore);
            }
        }
    }

    public static void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG:
                    timeScale = Math.min(FAST_SCALE, timeScale);
                    break;
                case FASTER_FLAG:
                    timeScale = Math.min(FASTER_SCALE, timeScale);
                    break;
                case FASTEST_FLAG:
                    timeScale = Math.min(FASTEST_SCALE, timeScale);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        PApplet.main(VirtualWorld.class);
    }
}