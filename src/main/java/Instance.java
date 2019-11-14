import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

public class Instance {
    private static int Dimension;
    private static int Size;

    private static String Filename = "Hs_16_100_25_00_001.has";
    private static ArrayList<ArrayList<Island>> HashiMatrix;
    private static Graph<Island, DefaultEdge> HashiPuzzle = new Multigraph<>(DefaultEdge.class);

    public static void main(String[] args) throws Exception {
        readInstance();
        createAuxStructures();
        printHashiMatrix();
    }

    private static void readInstance() throws IOException {
        File file = new File("C:\\Users\\tchel\\Downloads\\Hashi_Puzzles\\Hashi_Puzzles\\100\\" + Filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line, splitline[];

        line = br.readLine();
        splitline = line.split(" ");
        Dimension = Integer.valueOf(splitline[0]);
        Size = Integer.valueOf(splitline[2]);

        HashiMatrix = new ArrayList<>(Dimension);
        for (int lin = 0; lin < Dimension; lin++) {
            HashiMatrix.add(new ArrayList<>(Dimension));
            line = br.readLine();
            splitline = line.split(" ");
            for (int col = 0; col < Dimension; col++) {
                Island isl = new Island(lin, col, Integer.valueOf(splitline[col * 2 + 1]));
                HashiMatrix.get(lin).add(isl);
                if (isl.getBridgeNeeded() > 0)
                    HashiPuzzle.addVertex(isl);
            }
        }
    }

    private static void createAuxStructures() {
        for (int line = 0; line < Dimension; line++) {
            for (int col = 0; col < Dimension; col++) {
                if (HashiMatrix.get(line).get(col).getBridgeNeeded() == 0) continue;
                if (col - 1 >= 0 && HashiMatrix.get(line).get(col - 1).getBridgeNeeded() != 0)
                    HashiMatrix.get(line).get(col).addAdjacentIsland(HashiMatrix.get(line).get(col - 1));
                else if (col + 1 < Dimension && HashiMatrix.get(line).get(col + 1).getBridgeNeeded() != 0)
                    HashiMatrix.get(line).get(col).addAdjacentIsland(HashiMatrix.get(line).get(col + 1));
                if (line - 1 >= 0 && HashiMatrix.get(line - 1).get(col).getBridgeNeeded() != 0)
                    HashiMatrix.get(line).get(col).addAdjacentIsland(HashiMatrix.get(line - 1).get(col));
                else if (line + 1 < Dimension && HashiMatrix.get(line + 1).get(col).getBridgeNeeded() != 0)
                    HashiMatrix.get(line).get(col).addAdjacentIsland(HashiMatrix.get(line + 1).get(col));
            }
        }
    }

    private static void printHashiMatrix() {
        for (int c = 0; c < Dimension; c++) {
            for (int d = 0; d < Dimension; d++) {
                System.out.printf("%d ", HashiMatrix.get(c).get(d).getBridgeNeeded());
            }
            System.out.println();
        }
    }
}
