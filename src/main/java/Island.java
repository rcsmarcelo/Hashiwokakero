import java.util.ArrayList;

public class Island {
    private int Line;
    private int Col;
    private int BridgeCount = 0;
    private int BridgeNeeded;

    public ArrayList<Island> AdjacentIslands = new ArrayList<Island>();

    public Island(int Line, int Col, int BridgeNeeded) {
        this.Line = Line;
        this.Col = Col;
        this.BridgeNeeded = BridgeNeeded;
    }

    public Island(Island isl) {
        this.Line = isl.getLine();
        this.Col = isl.getCol();
        this.BridgeNeeded = isl.getBridgeNeeded();
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

    public void setLine(int line) {
        Line = line;
    }

    public int getCol() {
        return Col;
    }

    public void setCol(int col) {
        Col = col;
    }

    public int getBridgeCount() {
        return BridgeCount;
    }

    public void setBridgeCount(int bridgeCount) {
        BridgeCount = bridgeCount;
    }

    public int getBridgeNeeded() {
        return BridgeNeeded;
    }

    public void setBridgeNeeded(int BridgeNeeded) {
        BridgeNeeded = BridgeNeeded;
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
}
