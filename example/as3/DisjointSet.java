import java.util.Collection;
import java.util.HashSet;

public class DisjointSet {
    private Collection<Node> forest;

    public DisjointSet(Collection<Node> nodes) {
        forest = new HashSet<>();

        for(Node n: nodes) {
            makeSet(n);
        }
    }

    void makeSet(Node n)  {
        n.parent = n;
        n.depth = 0;
       forest.add(n);
    }

     Node find(Node n) {
        if(n == null){
            return n;
        }
        if(n.parent.nodeID == n.nodeID) {
            return n;
        } else {
            Node root = find(n.parent);
            return root;
        }
    }
    void union(Node x, Node y){
        Node xRoot = find(x);
        Node yRoot = find(y);
        if(xRoot == yRoot) {
            return;
        } else {
            if(xRoot.depth < yRoot.depth) {
                xRoot.parent = yRoot;
                forest.remove(xRoot);
            } else {
                yRoot.parent = xRoot;
                forest.remove(yRoot);
                if (xRoot.depth == yRoot.depth){
                    xRoot.depth ++;
                }
            }
        }

    }

    public Collection<Node> getForest() {
        return forest;
    }
}


