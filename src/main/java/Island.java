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
}
