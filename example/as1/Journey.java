import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;

public class Journey extends GUI {
    private Map<String,Stop> position = new HashMap<String, Stop>();
    private Map<String, Trie> connection = new HashMap<String, Trie>();
    private Set<Trie> distance = new HashSet<Trie>();
    private Location origin;
    private double scale;

    @Override
    protected void redraw(Graphics g ) {
      for(Map.Entry<String,Stop> stop : position.entrySet()){
            stop.getValue().redraw(g,origin,scale );
      }
      for(Trie read : distance){
          read.redraw(g);
      }

     }

    @Override
    protected void onClick(MouseEvent e) {
     Point a = e.getPoint();
     Location ab  = Location.newFromPoint(a, former, scaleTime );
     Stop c = null;
     double d = Double.POSITIVE_INFINITY;

    }

    @Override
    protected void onSearch() {

    }

    @Override
    protected void onMove(Move m) {

    }

    @Override
    protected void onLoad(File stopFile, File tripFile){
        this.stopFile(stopFile);
        this.tripFile(tripFile);
        Location a = Location.newFromLatLon(Location.getCentreLat(),Location.getCentreLon());

    }

    // here are some useful methods you'll need.
    public void stopFile(File stopFile){
        try{
            FileReader fr = new FileReader(stopFile);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            br.readLine();
            while((line = br.readLine())!=null){
                String data[] = line.split("\\t");
                String id =  data[0];
                String name = data[1];
                Double la = Double.parseDouble(data[2]);
                Double lo = Double.parseDouble(data[3]);
                Stop city = new Stop(name, la, lo);
                position.put(id,city);

            }
            fr.close();
            System.out.println("Contents of file:");
//            System.out.println(ss.toString());
        }catch(Exception e) {}
    }

    public void tripFile(File tripFile){
        try{
            FileReader fr = new FileReader(tripFile);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            br.readLine();
            while((line = br.readLine())!=null){
                String data[] = line.split("\\t");
                String id =  data[0];
                String num = "";
                for (int i = 1; i < data.length; i++) {
                     num = data[i];
                }
                Trie distance = new Trie(num);
                connection.put(id,distance);
            }
            fr.close();
            System.out.println("Contents of file:");
//            System.out.println(ss.toString());
        }catch(Exception e) {}
    }
}
