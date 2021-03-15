import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy
{
    @Override
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        List<Point> path = new LinkedList<>();
        List<Node> openList = new ArrayList<>();
        Set<Node> closedList = new HashSet<>();
        //List<Node> closedList = new ArrayList<>();

        int startH = computeHeuristic(start, end);
        Node startNode = new Node(start, 0, startH, startH, null);

        openList.add(startNode);
        Node currentNode = startNode;

        while(!withinReach.test(currentNode.getPoint(), end)) {
            List<Point> validNeighborPoints = potentialNeighbors.apply(currentNode.getPoint()).filter(canPassThrough).collect(Collectors.toList());

            for (Point p : validNeighborPoints) {
                int g = currentNode.getG() + 1;
                int h = computeHeuristic(p, end);
                int f = g + h;
                Node currentNeighbor = new Node(p, g, h, f, currentNode);

                if (!closedList.contains(currentNeighbor)) {
                    if (!openList.contains(currentNeighbor)) {
                        openList.add(currentNeighbor);
                    } else if (currentNeighbor.getG() < openList.get(openList.indexOf(currentNeighbor)).getG()) {
                        openList.set(openList.indexOf(currentNeighbor), currentNeighbor); //check this for setting f and h values if need to recalculate
                        currentNode = currentNeighbor.getPrevious();
                    }
                }
            }

            openList.remove(currentNode);
            closedList.add(currentNode);

            if(openList.size() == 0)
                return new LinkedList<>();

            Comparator<Node> fComparator = (Node n1, Node n2) -> n1.getF() - n2.getF();
            Collections.sort(openList, fComparator);
            currentNode = openList.get(0);
        }
        //Build path from previous nodes

        while(!currentNode.equals(startNode))
        {
            path.add(0, currentNode.getPoint());
            currentNode = currentNode.getPrevious();
        }


        return path;
    }

    public int computeHeuristic(Point p1, Point p2)
    {
        return Math.abs(p1.getX()-p2.getX()) + Math.abs(p1.getY()-p2.getY());
    }

}
