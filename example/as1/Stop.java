import java.awt.*;
public class Stop{
    private final double la;
    private final String name;
    private final double lo;

    public Stop(String name, double la, double lo){
        this.name = name;
        this.la = la;
        this.lo = lo;
    }

    public String name(){
        return name;
    }
    public double la () { return la; }
    public double lo () { return lo; }

    public void redraw(Graphics g, Location origin, double scale) {
        Location pos = new Location (la, lo);
        Point p = pos.asPoint(origin, scale);
        //origin created from a random stops location
        //scale is from location and is just a number
        g.fillOval(p.x, p.y, 10, 10);
    }

}
