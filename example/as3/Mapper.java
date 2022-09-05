import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

/**
 * This is the main class for the mapping program. It extends the GUI abstract
 * class and implements all the methods necessary, as well as having a main
 * function.
 *
 * @author tony
 */
public class Mapper extends GUI {
    public static final Color NODE_COLOUR = new Color(77, 113, 255);
    public static final Color SEGMENT_COLOUR = new Color(130, 130, 130);
    public static final Color HIGHLIGHT_COLOUR = new Color(255, 219, 77);
    public static final Color HIGHLIGHT_END_COLOUR = new Color(255, 0, 77);

    // these two constants define the size of the node squares at different zoom
    // levels; the equation used is node size = NODE_INTERCEPT + NODE_GRADIENT *
    // log(scale)
    public static final int NODE_INTERCEPT = 1;
    public static final double NODE_GRADIENT = 0.8;

    // defines how much you move per button press, and is dependent on scale.
    public static final double MOVE_AMOUNT = 100;
    // defines how much you zoom in/out per button press, and the maximum and
    // minimum zoom levels.
    public static final double ZOOM_FACTOR = 1.3;
    public static final double MIN_ZOOM = 1, MAX_ZOOM = 200;

    // how far away from a node you can click before it isn't counted.
    public static final double MAX_CLICKED_DISTANCE = 0.15;

    // these two define the 'view' of the program, ie. where you're looking and
    // how zoomed in you are.
    private Location origin;
    private double scale = 200;

    // our data structures.
    private Graph graph;

    @Override
    protected void redraw(Graphics g) {
        if (graph != null)
            graph.draw(g, getDrawingAreaDimension(), origin, scale);
    }

    private Node start = null;
    private HashSet<Node> APs = new HashSet<>();
    private PriorityQueue<Segment> fringe = new PriorityQueue<>((o1, o2) -> {
        return (int)(o1.length * 1000000000 - o2.length * 1000000000);
    });

    @Override
    protected void onClick(MouseEvent e) {
        Location clicked = Location.newFromPoint(e.getPoint(), origin, scale);
        // find the closest node.
        double bestDist = Double.MAX_VALUE;
        Node closest = null;

        for (Node node : graph.nodes.values()) {
            double distance = clicked.distance(node.location);
            if (distance < bestDist) {
                bestDist = distance;
                closest = node;
            }
            node.depth = Double.POSITIVE_INFINITY;
        }

        // if it's close enough, highlight it and show some information.
        if (clicked.distance(closest.location) < MAX_CLICKED_DISTANCE) {
            /**
             * A search
             */
//            if(this.start == null) {
//                this.start = closest;
//                ArrayList<Segment> roads = new ArrayList<>();
//                graph.setHightlightSe(roads);
//            } else {
//                // startNode = this.start
//                // endNode = closest
//                ArrayList<Segment> roads = new ArrayList<>();
//
//                // roads = this.aStarSearch(closest, this.start, roads, closeList, memo);
//                roads = this.aStarSearch2(graph.nodes,  closest, this.start );
//                graph.setHightlightSe(roads);
//                // System.out.println("roads:" + roads + ", roads length: " + roads.size());
//                this.start = null;
//            }

            /**
             * Critical intersections
             */
            graph.setHighlightNodes(Collections.emptyList());
            Node root = closest;
            root.depth = 0;
            int numSubTrees = 0;
            for(Segment s: root.segments) {
                Node neighbor = getNeighbor(root, s);
                if(neighbor.depth == Double.POSITIVE_INFINITY) {
                    // B ->
                    recArtPts(neighbor, 1, root);
                    numSubTrees ++;
                }
                if (numSubTrees > 1){
                    APs.add(root);
                }
            }

            //   graph.setHighlightNodes(APs);
            Collection<Node> allNodes = graph.nodes.values();
            for(Segment s: graph.segments) {
                if(allNodes.contains(s.start) && allNodes.contains(s.end)) {
                    fringe.offer(s);
                }
            }

            /**
             *  Get Minimum spanning tree
             */
            DisjointSet disjointSet = new DisjointSet(allNodes);
            Collection<Segment> mst = kruskal(disjointSet, fringe);
            graph.setHightlightSe(mst);


            // graph.setHighlight(closest);
            getTextOutputArea().setText(closest.toString());
        }

        // TODO: 1. Select two nodes
        // 2. A* search to select the good route.
        //  - Calc the distance form end to start node, if start == end,  end program
        //  - Get the segements with end node, calc the distances from all segements' to start node,
        //    segement's length as the `h`, the distance to start node as the `g`
        //  - Get the min f = g + h.  Set this node as newest node.  execue the Recursion
    }

    public ArrayList<Segment> aStarSearch(Node start, Node end, ArrayList<Segment> roads,
                                          Collection<Segment> closeList, HashMap<String,ArrayList<Segment>> memo) {
        // return Collection<Road>
        HashMap<Double, Node> distanceMap = new HashMap<>();
        HashMap<Double, Segment> seMap = new HashMap<>();
        String roadKey = this.concatIntToStr(start.nodeID, end.nodeID);
        System.out.println("road key"+ roadKey);

        if(memo.get(roadKey) != null) {
            roads = memo.get(roadKey);
        }
        if(start.nodeID == end.nodeID) {
            return roads;
        }

        // System.out.println("start: "+ start.nodeID + ", end: " + end.nodeID);
        Collection<Segment> segments = start.segments;
        System.out.println("segments length:" + segments.size());
        for (Segment s : segments) {
            System.out.println("is close:" + closeList.contains(s) + ", roadId: "+ s.start.nodeID);
            if(closeList.contains(s)) {
                continue;
            }
            Node nextNode = null;

            if(s.start.nodeID == start.nodeID) {
                nextNode = s.end;
            } else {
                nextNode = s.start;
            }

            double distance = nextNode.location.distance(end.location);
            double f = distance + s.length;
            distanceMap.put(f, nextNode);
            seMap.put(f, s);
        }
        if(distanceMap.isEmpty()) {
            // TODO: Not closed path
            return roads;
        }
        double min = Collections.min(distanceMap.keySet());
        // System.out.println("Min: " + min);
        Node minNode = distanceMap.get(min);
        Segment road = seMap.get(min);
        String newKey = concatIntToStr(minNode.nodeID, end.nodeID);
        roads.add(road);
        closeList.add(road);
        roads = aStarSearch(minNode, end, roads, closeList, memo);
        memo.put(newKey, roads);

        return roads;
    }

    public ArrayList<Segment> aStarSearch2(Map<Integer, Node> nodes, Node start, Node goal) {
        Fringe startFringe = new Fringe(start, null, 0, start.location.distance(goal.location));
        ArrayList<Fringe> fringeList = new ArrayList<>();
        fringeList.add(startFringe);
        Collection<Node> visitedList = new ArrayList<>();
        ArrayList<Segment> roads = new ArrayList<>();
        Map<Integer, Double> gScore = new HashMap<>();
        gScore.put(start.nodeID, 0.00);
        Map<Integer, Double> fScore = new HashMap<>();
        fScore.put(start.nodeID, h(start, goal));
        Map<Double, Fringe> fMap = new HashMap<>();
        HashMap<Double, Segment> seMap = new HashMap<>();
        HashMap<Double, Node> nMap = new HashMap<>();

        while(!fringeList.isEmpty()){
            Fringe fringe = fringeList.get(0);
            System.out.print("size: " + fringeList.size());
            if(!visitedList.contains(fringe.node)) {
                visitedList.add(fringe.node);
                nodes.get(fringe.node.nodeID).prev = fringe;
                fringeList.remove(0);
                if(fringe.node.nodeID == goal.nodeID) {
                    return roads;
                }
                Node current = fringe.node;
                for(Segment s: current.segments) {
                    Node neighbor = null;
                    if(s.start.nodeID == current.nodeID) {
                        neighbor = s.end;
                    } else {
                        neighbor = s.start;
                    }
                    if(!visitedList.contains(neighbor)){
                        double g = fringe.g + s.length;
                        double f = g + h(neighbor, goal);
                        System.out.println("roads: " + neighbor.nodeID +"->" + fringe.node.nodeID + " - g: " + g + " - f: " +f);
                        fMap.put(f, new Fringe(neighbor, fringe, g , f));
                        seMap.put(f, s);
                        nMap.put(f, neighbor);
                    }


                }
                if(fMap.isEmpty()) {
                    fringeList.add(fringe.prev);

                    visitedList.remove(fringe.prev.node);
                    Segment r = getSegmentByNode(fringe.prev.node.segments, fringe.node);
                    if(r != null) {
                        roads.remove(r);
                    }


                    continue;
                }
                // Calc min
                double min = Collections.min(fMap.keySet());

                Fringe fg = fMap.get(min);
                fringeList.add(fg);
                roads.add(seMap.get(min));
                fMap.clear();
                seMap.clear();

            }
        }
        return roads;
    }

    private double recArtPts(Node node, double depth, Node parent) {
        // node = B, 1
        node.depth = depth;
        double reachBack = depth;  // 1
        for(Segment s: node.segments) {
            Node neighbor = getNeighbor(node, s);
            if(neighbor.nodeID == parent.nodeID) {
                continue;
            }
            if(neighbor.depth < Double.POSITIVE_INFINITY) {
                reachBack = Math.min(neighbor.depth, reachBack);
            } else {
                // neighbor = C , 2,
                double childReach = recArtPts(neighbor, depth + 1, node);
                reachBack = Math.min(childReach, reachBack);
                if(childReach>= depth) {
                    APs.add(node);
                }
            }
        }
        return reachBack;
    }

    private Collection<Segment> kruskal(DisjointSet disjointSet, PriorityQueue<Segment> fringe ) {
        Collection<Node> forest = disjointSet.getForest();
        Collection<Segment> treeSet = new HashSet<>();
        while (forest.size() > 1 && !fringe.isEmpty()) {
            Segment s = fringe.poll();
            // if s.start and s.end are in different tree in forest, merge them
            Node startRoot = disjointSet.find(s.start);
            Node endRoot = disjointSet.find(s.end);

            if(startRoot != endRoot) {
                disjointSet.union(startRoot, endRoot);
                treeSet.add(s);
            }
        }
        return treeSet;
    }

    private Node getNeighbor(Node node, Segment segment) {
        return node.nodeID == segment.start.nodeID ? segment.end : segment.start;
    }


    private double h(Node start,Node goal) {
        return start.location.distance(goal.location);
    }

    private Segment getSegmentByNode(Collection<Segment> segments, Node node) {
        for(Segment s: segments) {
            if(s.start.nodeID == node.nodeID || s.end.nodeID == node.nodeID){
                return s;
            }

        }
        return null;
    }

    private String concatIntToStr(int a, int b){
        return Integer.toString(a) + Integer.toString(b);
    }

    @Override
    protected void onSearch() {
        // Does nothing

    }

    @Override
    protected void onMove(Move m) {
        if (m == Move.NORTH) {
            origin = origin.moveBy(0, MOVE_AMOUNT / scale);
        } else if (m == Move.SOUTH) {
            origin = origin.moveBy(0, -MOVE_AMOUNT / scale);
        } else if (m == Move.EAST) {
            origin = origin.moveBy(MOVE_AMOUNT / scale, 0);
        } else if (m == Move.WEST) {
            origin = origin.moveBy(-MOVE_AMOUNT / scale, 0);
        } else if (m == Move.ZOOM_IN) {
            if (scale < MAX_ZOOM) {
                // yes, this does allow you to go slightly over/under the
                // max/min scale, but it means that we always zoom exactly to
                // the centre.
                scaleOrigin(true);
                scale *= ZOOM_FACTOR;
            }
        } else if (m == Move.ZOOM_OUT) {
            if (scale > MIN_ZOOM) {
                scaleOrigin(false);
                scale /= ZOOM_FACTOR;
            }
        }
    }

    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons) {
        graph = new Graph(nodes, roads, segments, polygons);
        origin = new Location(-250, 250); // close enough
        scale = 1;
    }

    /**
     * This method does the nasty logic of making sure we always zoom into/out
     * of the centre of the screen. It assumes that scale has just been updated
     * to be either scale * ZOOM_FACTOR (zooming in) or scale / ZOOM_FACTOR
     * (zooming out). The passed boolean should correspond to this, ie. be true
     * if the scale was just increased.
     */
    private void scaleOrigin(boolean zoomIn) {
        Dimension area = getDrawingAreaDimension();
        double zoom = zoomIn ? 1 / ZOOM_FACTOR : ZOOM_FACTOR;

        int dx = (int) ((area.width - (area.width * zoom)) / 2);
        int dy = (int) ((area.height - (area.height * zoom)) / 2);

        origin = Location.newFromPoint(new Point(dx, dy), origin, scale);
    }

    public static void main(String[] args) {
        new Mapper();
    }
}

// code for COMP261 assignments
