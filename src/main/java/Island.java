import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;

public class Island {
    private int Line;
    private int Col;
    private int BridgeNeeded;

    private ArrayList<Island> AdjacentIslands = new ArrayList<>();

    Island(int Line, int Col, int BridgeNeeded) {
        this.Line = Line;
        this.Col = Col;
        this.BridgeNeeded = BridgeNeeded;
    }

    Island(int Line, int Col, int BridgeNeeded, ArrayList<Island> adj) {
        this.Line = Line;
        this.Col = Col;
        this.BridgeNeeded = BridgeNeeded;
        this.AdjacentIslands = adj;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        if (obj == this) return true;
        return (((Island)obj).getCol() == this.getCol() && ((Island)obj).getLine() == this.getLine());
    }

    @Override
    public int hashCode() {
        return 31 * (this.getLine() + Integer.toString(this.getCol())).hashCode();
    }

    int getLine() {
        return Line;
    }

    int getCol() {
        return Col;
    }

    int getBridgeNeeded() {
        return BridgeNeeded;
    }

    void addAdjacentIsland(Island island) {
        AdjacentIslands.add(island);
    }

    boolean isComplete(Graph<Island, DefaultEdge> g) {
        return BridgeNeeded == g.degreeOf(this);
    }

    ArrayList<Island> getAdjacentIslands() { return this.AdjacentIslands; }
}
