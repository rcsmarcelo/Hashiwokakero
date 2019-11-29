import java.util.ArrayList;

public class Island {
    private int Line;
    private int Col;
    private int BridgeCount;
    private int BridgeNeeded;

    public ArrayList<Island> AdjacentIslands = new ArrayList<Island>();

    public Island(int Line, int Col, int BridgeNeeded) {
        this.Line = Line;
        this.Col = Col;
        this.BridgeNeeded = BridgeNeeded;
        this.BridgeCount = 0;
    }

    public Island(Island isl) {
        this.Line = isl.getLine();
        this.Col = isl.getCol();
        this.BridgeNeeded = isl.getBridgeNeeded();
        this.BridgeCount = 0;
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

    public int getBridgeCount() {
        return BridgeCount;
    }

    public int getBridgeNeeded() {
        return BridgeNeeded;
    }

    public void addAdjacentIsland(Island island) {
        AdjacentIslands.add(island);
    }

    public boolean isComplete() {
        return getBridgeNeeded() == getBridgeCount();
    }

    public void increaseBridgeCount() {
        this.BridgeCount++;
    }

    public ArrayList<Island> getAdjacentIslands() { return this.AdjacentIslands; }

    public void decreaseBridgeCount() { this.BridgeCount--; }
}
