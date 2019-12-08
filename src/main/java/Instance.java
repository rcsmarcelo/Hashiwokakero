import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

public class Instance {
    private static final int STARTING_POPULATION = 100;
    private static int Dimension;

    private static ArrayList<ArrayList<ArrayList<Island>>> HashiMatrix = new ArrayList<>();
    private static ArrayList<Graph<Island, DefaultEdge>> Population = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        for (int c = 0; c < STARTING_POPULATION; c++) {
            readInstance();
            createAuxStructures();
        }
        GenAlg.HGA(Population, Dimension, STARTING_POPULATION);
    }

    private static void readInstance() throws IOException {
        String filename = "Hs_24_200_25_05_028.has";
        File file = new File("C:\\Users\\tchel\\Downloads\\Hashi_Puzzles\\200\\" + filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        Graph<Island, DefaultEdge> hashiPuzzle = new Multigraph<>(DefaultEdge.class);
        String line;
        String[] splitline;

        line = br.readLine();
        splitline = line.split(" ");
        Dimension = Integer.parseInt(splitline[0]);
        HashiMatrix.add(new ArrayList<>(Dimension));
        for (int lin = 0; lin < Dimension; lin++) {
            HashiMatrix.get(HashiMatrix.size() - 1).add(new ArrayList<>(Dimension));
            line = br.readLine();
            splitline = line.split(" ");
            for (int col = 0; col < Dimension; col++) {
                Island isl = new Island(lin, col, Integer.parseInt(splitline[col * 2 + 1]));
                HashiMatrix.get(HashiMatrix.size() - 1).get(lin).add(isl);
                if (isl.getBridgeNeeded() > 0)
                    hashiPuzzle.addVertex(isl);
            }
        }
        Population.add(hashiPuzzle);
    }

    private static void createAuxStructures() {
        for (int line = 0; line < Dimension; line++) {
            for (int col = 0; col < Dimension; col++) {
                Island curr = HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col);
                if (curr.getBridgeNeeded() == 0) continue;
                for (int col1 = col + 1; col1 < Dimension; col1++) {
                    if (HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col1).getBridgeNeeded() != 0) {
                        Island add = HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col1);
                        curr.addAdjacentIsland(add);
                        break;
                    }
                }
                for (int col1 = col - 1; col1 >= 0; col1--) {
                    if (HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col1).getBridgeNeeded() != 0) {
                        Island add = HashiMatrix.get(HashiMatrix.size() - 1).get(line).get(col1);
                        curr.addAdjacentIsland(add);
                        break;
                    }
                }
                for (int line1 = line + 1; line1 < Dimension; line1++) {
                    if (HashiMatrix.get(HashiMatrix.size() - 1).get(line1).get(col).getBridgeNeeded() != 0) {
                        Island add = HashiMatrix.get(HashiMatrix.size() - 1).get(line1).get(col);
                        curr.addAdjacentIsland(add);
                        break;
                    }
                }
                for (int line1 = line - 1; line1 >= 0; line1--) {
                    if (HashiMatrix.get(HashiMatrix.size() - 1).get(line1).get(col).getBridgeNeeded() != 0) {
                        Island add = HashiMatrix.get(HashiMatrix.size() - 1).get(line1).get(col);
                        curr.addAdjacentIsland(add);
                        break;
                    }
                }
            }
        }
    }
}
