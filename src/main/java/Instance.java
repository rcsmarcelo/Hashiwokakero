import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

public class Instance {
    private static final int STARTING_POPULATION = 50;
    private static int Dimension;
    private static int Size;

    private static String Filename = "Hs_16_100_25_00_001.has";
    private static ArrayList<ArrayList<ArrayList<Island>>> HashiMatrix = new ArrayList<>();
    private static Graph<Island, DefaultEdge> HashiPuzzle;
    private static ArrayList<Graph<Island, DefaultEdge>> Population = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        for (int c = 0; c < STARTING_POPULATION; c++) {
            readInstance();
            createAuxStructures();
        }
        GenAlg.HGA(HashiMatrix, Population, Dimension, STARTING_POPULATION);
    }

    private static void readInstance() throws IOException {
        File file = new File("C:\\Users\\marcelo.costasantos\\Downloads\\Hashi_Puzzles\\100\\" + Filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        HashiPuzzle = new Multigraph<>(DefaultEdge.class);
        String line, splitline[];

        line = br.readLine();
        splitline = line.split(" ");
        Dimension = Integer.valueOf(splitline[0]);
        Size = Integer.valueOf(splitline[2]);

        HashiMatrix.add(new ArrayList<>(Dimension));
        for (int lin = 0; lin < Dimension; lin++) {
            HashiMatrix.get(HashiMatrix.size() - 1).add(new ArrayList<>(Dimension));
            line = br.readLine();
            splitline = line.split(" ");
            for (int col = 0; col < Dimension; col++) {
                Island isl = new Island(lin, col, Integer.valueOf(splitline[col * 2 + 1]));
                HashiMatrix.get(HashiMatrix.size() - 1).get(lin).add(isl);
                if (isl.getBridgeNeeded() > 0)
                    HashiPuzzle.addVertex(isl);
            }
        }
        Population.add(HashiPuzzle);
    }

    private static void createAuxStructures() {
        for (int line = 0; line < Dimension; line++) {
            for (int col = 0; col < Dimension; col++) {
                Island curr = HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col);
                if (curr.getBridgeNeeded() == 0) continue;
                for (int col1 = col + 1; col1 < Dimension; col1++)
                    if (HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col1).getBridgeNeeded() != 0) {
                        Island add = HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col1);
                        curr.addAdjacentIsland(new Island(add.getLine(), add.getCol(), add.getBridgeNeeded()));
                        break;
                    }
                for (int col1 = col - 1; col1 > 0; col1--)
                    if (HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col1).getBridgeNeeded() != 0) {
                        Island add = HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col1);
                        curr.addAdjacentIsland(new Island(add.getLine(), add.getCol(), add.getBridgeNeeded()));
                        break;
                    }
                for (int line1 = line + 1; line1 < Dimension; line1++)
                    if (HashiMatrix.get(HashiMatrix.size() - 1).get(line1).get(col).getBridgeNeeded() != 0) {
                        Island add = HashiMatrix.get(HashiMatrix.size() - 1).get(line1).get(col);
                        curr.addAdjacentIsland(new Island(add.getLine(), add.getCol(), add.getBridgeNeeded()));
                        break;
                    }
                for (int line1 = line - 1; line1 > 0; line1--)
                    if (HashiMatrix.get(HashiMatrix.size() - 1).get(line1).get(col).getBridgeNeeded() != 0) {
                        Island add = HashiMatrix.get(HashiMatrix.size() - 1).get(line1).get(col);
                        curr.addAdjacentIsland(new Island(add.getLine(), add.getCol(), add.getBridgeNeeded()));
                        break;
                    }
            }
        }
    }

    private static void printHashiMatrix(ArrayList<ArrayList<Island>> matrix) {
        for (int c = 0; c < Dimension; c++) {
            for (int d = 0; d < Dimension; d++) {
                System.out.printf("%d ", matrix.get(c).get(d).getBridgeNeeded());
            }
            System.out.println();
        }
    }
}
