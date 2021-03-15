import java.util.Objects;

public class Node {
    private Point point;
    private int g; //g value - actual distance
    private int h; //h value - heuristic, distance guess
    private int f; //f value - f = g + h
    private Node previous;

    public Node(Point point, int g, int h, int f, Node previous) {
        this.point = point;
        this.g = g;
        this.h = h;
        this.f = f;
        this.previous = previous;
    }

    public int getF() {
        return f;
    }

    public int getG() {
        return g;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Node getPrevious() {
        return this.previous;
    }



    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (!this.getClass().equals(other.getClass()))
            return false;

        Node n = (Node) other;
        return point.equals(n.point); //Only want to know if they have the same points, because there are no duplicate points in virtual world
        //return g == n.g && h == n.h && f == n.f && point.equals(n.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point);
    }


}
