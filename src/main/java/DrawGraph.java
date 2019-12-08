import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class DrawGraph extends JPanel {

    private Graph<Island, DefaultEdge> Puzzle;
    private int scale = 20;

    public DrawGraph(Graph<Island, DefaultEdge> puzzle) {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.setSize(550,550);
        Puzzle = puzzle;
        setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke((float) 1));
        int shift = 40;
        DefaultEdge[] edges = Puzzle.edgeSet().toArray(new DefaultEdge[0]);
        ArrayList<DefaultEdge> visited = new ArrayList<>();
        for (Island v : Puzzle.vertexSet()) {
            if (!v.isComplete(Puzzle))
                g2d.setColor(Color.red);
            else
                g2d.setColor(Color.black);
            g2d.fillOval((v.getCol() * scale - 5) + shift, (v.getLine() * scale - 5) + shift, scale/2, scale/2);
            g2d.drawString(Integer.toString(v.getBridgeNeeded()), v.getCol() * scale + 5 + shift,
                    v.getLine() * scale - 5 + shift);
        }
        g2d.setColor(Color.black);
        for (int c = 0; c < edges.length; c++) {
            if (visited.contains(edges[c])) continue;
            Island p1 = Puzzle.getEdgeSource(edges[c]);
            Island p2 = Puzzle.getEdgeTarget(edges[c]);
            visited.add(edges[c]);
            if (Puzzle.getAllEdges(p1, p2).size() == 2) {
                Line2D edge = new Line2D.Float(p1.getCol() * scale + 2 + shift, p1.getLine() * scale + 2 + shift,
                        p2.getCol() * scale + 2 + shift, p2.getLine() * scale + 2 + shift);
                Line2D edge2 = new Line2D.Float(p1.getCol() * scale - 2 + shift, p1.getLine() * scale - 2 + shift,
                        p2.getCol() * scale  - 2 + shift, p2.getLine() * scale - 2 + shift);
                g2d.draw(edge);
                g2d.draw(edge2);
            } else {
                Line2D edge = new Line2D.Float(p1.getCol() * scale + shift, p1.getLine()  * scale + shift,
                        p2.getCol() * scale + shift, p2.getLine() * scale + shift);
                g2d.draw(edge);
            }
        }
    }
}
