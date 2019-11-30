import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;

public class Island {
    private int Line;
    private int Col;
    private int BridgeNeeded;

    public ArrayList<Island> AdjacentIslands = new ArrayList<Island>();

    public Island(int Line, int Col, int BridgeNeeded) {
        this.Line = Line;
        this.Col = Col;
        this.BridgeNeeded = BridgeNeeded;
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
        return 31 * (Integer.toString(this.getLine()) + Integer.toString(this.getCol())).hashCode();
    }

    public int getLine() {
        return Line;
    }

    public int getCol() {
        return Col;
    }

    public int getBridgeNeeded() {
        return BridgeNeeded;
    }

    public void addAdjacentIsland(Island island) {
        AdjacentIslands.add(island);
    }

    public boolean isComplete(Graph<Island, DefaultEdge> g) {
        return BridgeNeeded == g.degreeOf(this);
    }

    public ArrayList<Island> getAdjacentIslands() { return this.AdjacentIslands; }
}
