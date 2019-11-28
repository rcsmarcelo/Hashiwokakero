import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class DrawGraph extends JPanel {

    private Graph<Island, DefaultEdge> Puzzle;

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
        for (Island v : Puzzle.vertexSet())
            g2d.fillOval((v.getCol() * 30 - 5) + shift, (v.getLine() * 30 - 5) + shift, 10, 10);
        for (int c = 0; c < edges.length; c++) {
            if (visited.contains(edges[c]));
            Island p1 = Puzzle.getEdgeSource(edges[c]);
            Island p2 = Puzzle.getEdgeTarget(edges[c]);
            visited.add(edges[c]);
            if (Puzzle.getAllEdges(p1, p2).size() == 2) {
                Line2D edge = new Line2D.Float(p1.getCol() * 30 + 2 + shift, p1.getLine() * 30 + 2 + shift,
                        p2.getCol() * 30 + 2 + shift, p2.getLine() * 30 + 2 + shift);
                Line2D edge2 = new Line2D.Float(p1.getCol() * 30 - 2 + shift, p1.getLine() * 30 - 2 + shift,
                        p2.getCol() * 30  - 2 + shift, p2.getLine() * 30 - 2 + shift);
                g2d.draw(edge);
                g2d.draw(edge2);
            } else {
                Line2D edge = new Line2D.Float(p1.getCol() * 30 + shift, p1.getLine()  * 30 + shift,
                        p2.getCol() * 30 + shift, p2.getLine() * 30 + shift);
                g2d.draw(edge);
            }
        }
    }
}
