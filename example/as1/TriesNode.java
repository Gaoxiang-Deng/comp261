import java.awt.*;
import java.util.*;

public class TriesNode {
    public boolean isLabel; // if this node is an end of a label
    Set<String> roads;
    public Map<Character, TriesNode> children;

    public TriesNode() {
        isLabel = false;
        roads = new HashSet<>();
        children = new HashMap<Character,TriesNode>();
    }


    public void getAllLeaves(ArrayList<String> roads) {
        // base case to exit recursion
        if (this.isLabel && !this.roads.isEmpty()) {
            roads.addAll(this.roads);
        }
        for (TriesNode node : this.children.values()) {
            node.getAllLeaves(roads);
        }
    }
}
