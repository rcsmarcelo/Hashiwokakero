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
        printHashiMatrix();
        GenAlg.HGA(HashiMatrix, Population, Dimension, STARTING_POPULATION);
    }

    private static void readInstance() throws IOException {
        File file = new File("C:\\Users\\ramos\\Downloads\\Hashi_Puzzles\\100\\" + Filename);
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
                if (HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col).getBridgeNeeded() == 0) continue;
                if (col - 1 >= 0 && HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col - 1).getBridgeNeeded() != 0)
                    HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col).
                            addAdjacentIsland(HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col - 1));
                else if (col + 1 < Dimension && HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col + 1).
                        getBridgeNeeded() != 0)
                    HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col).
                            addAdjacentIsland(HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col + 1));
                if (line - 1 >= 0 && HashiMatrix.get(HashiMatrix.size() - 1).get(line - 1).get(col).
                        getBridgeNeeded() != 0)
                    HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col).
                            addAdjacentIsland(HashiMatrix.get(HashiMatrix.size() - 1).get(line - 1).get(col));
                else if (line + 1 < Dimension && HashiMatrix.get(HashiMatrix.size() - 1).get(line + 1).get(col).
                        getBridgeNeeded() != 0)
                    HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col).addAdjacentIsland(
                            HashiMatrix.get(HashiMatrix.size() - 1).get(line + 1).get(col));
            }
        }
    }

    private static void printHashiMatrix() {
        for (int c = 0; c < Dimension; c++) {
            for (int d = 0; d < Dimension; d++) {
                System.out.printf("%d ", HashiMatrix.get(HashiMatrix.size() - 1).get(c).get(d).getBridgeNeeded());
            }
            System.out.println();
        }
    }
}
