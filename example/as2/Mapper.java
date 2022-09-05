import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
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
        }

        // if it's close enough, highlight it and show some information.
        if (clicked.distance(closest.location) < MAX_CLICKED_DISTANCE) {

            if(this.start == null) {
                this.start = closest;
                ArrayList<Segment> roads = new ArrayList<>();
                graph.setHightlightSe(roads);
            } else {
                // startNode = this.start
                // endNode = closest
                ArrayList<Segment> roads = new ArrayList<>();
                HashMap<Double, Node> distanceMap = new HashMap<>();
                HashMap<Double, Segment> seMap = new HashMap<>();
                HashMap<String, ArrayList<Segment>> memo = new HashMap<>();
                Collection<Segment> closeList = new HashSet<>();
                Collection<Segment> visitedList = new HashSet<>();

                // roads = this.aStarSearch(closest, this.start, roads, closeList, memo);
                roads = this.aStarSearch2(graph.nodes,  closest, this.start );
                graph.setHightlightSe(roads);
                // System.out.println("roads:" + roads + ", roads length: " + roads.size());
                this.start = null;
            }
            graph.setHighlight(closest);
            getTextOutputArea().setText(closest.toString());
        }

        // TODO: 1. Select two nodes
        // 2. A* search to select the good route.
        //  - Calc the distance form end to start node, if start == end,  end program 
        //  - Get the segements with end node, calc the distances from all segements' to start node, 
        //    segement's length as the `h`, the distance to start node as the `g`
        //  - Get the min f = g + h.  Set this node as newest node.  execue the Recursion
    }

    public ArrayList<Segment> aStarSearch2(Map<Integer, Node> nodes, Node start, Node goal) {
        Fringe startFringe = new Fringe(start, null, 0, start.location.distance(goal.location));
        ArrayList<Fringe> fringeList = new ArrayList<>();
        fringeList.add(startFringe);
        Collection<Node> visitedList = new ArrayList<>();
        ArrayList<Segment> roads = new ArrayList<>();
        Map<Node, Node> cameFrom = new HashMap<>();
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
                        //roads.add(s);
                        seMap.put(f, s);
                        nMap.put(f, neighbor);
                        // fringeList.add(new Fringe(neighbor, current, g, f));
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
                //visitedList.remove(fg.node);
                fMap.clear();
                seMap.clear();

            }
        }
        return roads;
    }

    private ArrayList<Segment> reconstructPath(Node current) {
        System.out.print("Current" + current.nodeID);
        return null;
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
        if (m == GUI.Move.NORTH) {
            origin = origin.moveBy(0, MOVE_AMOUNT / scale);
        } else if (m == GUI.Move.SOUTH) {
            origin = origin.moveBy(0, -MOVE_AMOUNT / scale);
        } else if (m == GUI.Move.EAST) {
            origin = origin.moveBy(MOVE_AMOUNT / scale, 0);
        } else if (m == GUI.Move.WEST) {
            origin = origin.moveBy(-MOVE_AMOUNT / scale, 0);
        } else if (m == GUI.Move.ZOOM_IN) {
            if (scale < MAX_ZOOM) {
                // yes, this does allow you to go slightly over/under the
                // max/min scale, but it means that we always zoom exactly to
                // the centre.
                scaleOrigin(true);
                scale *= ZOOM_FACTOR;
            }
        } else if (m == GUI.Move.ZOOM_OUT) {
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