import java.awt.*;
import java.util.ArrayList;

public class Trie {

    private static TriesNode root;
    double la;
    double lo;
    public Trie(String num) {
        root = new TriesNode();
    }
    public static void add(String word, String road) {
        TriesNode node = root;
        word = word.trim();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (node.children.get(c) == null) {
                node.children.put(c, new TriesNode());
            }
            node = node.children.get(c);
        }
        node.isLabel = true;
        node.roads.add(road);
    }

    public ArrayList<String> find(String word) {
        TriesNode node = root;
        word = word.trim();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (node.children.get(c) == null) {
                return null;
            } else {
                node = node.children.get(c);
            }
        }
        // Use ArrayList instead of HashSet to keep the output in alphabet order
        // seems not working?
        ArrayList<String> roads = new ArrayList<>();
        node.getAllLeaves(roads);
        return roads;
    }
    public void redraw(Graphics g ) {

        Location pos = new Location (la, lo);
        Point p = pos.asPoint(origin, scale);
        //origin created from a random stops location
        //scale is from location and is just a number
        g.drawLine(p.x, p.y, 10, 10);
    }
}