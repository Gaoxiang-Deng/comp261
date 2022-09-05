import java.awt.Graphics;
import java.awt.Point;

/**
 * 在这里给出对类 Fringe 的描述。
 * 
 * @作者（你的名字）
 * @版本（一个版本号或者一个日期）
 */
public class Fringe
{
   public final  Node node;
   public final Fringe prev;
   public final double g;
   public final double f;
   public Fringe(Node node, Fringe prev, double g, double f) {
       this.node = node;
       this.prev = prev;
       this.g = g;
       this.f = f;
    }
    
}
