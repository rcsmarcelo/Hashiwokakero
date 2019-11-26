import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GenAlg {

    private static ArrayList<ArrayList<ArrayList<Island>>> HashiMatrix;
    private static ArrayList<Graph<Island, DefaultEdge>> Population;
    private static int Dimension;
    private static int STARTING_POPULATION;

    public static void HGA(ArrayList<ArrayList<ArrayList<Island>>> matrix, ArrayList<Graph<Island,
            DefaultEdge>> population, int dimension, int starting) {
        HashiMatrix = matrix;
        Dimension = dimension;
        Population = population;
        STARTING_POPULATION = starting;
        //initialize 50 parents
        for (int c = 0; c < STARTING_POPULATION; c++)
            initializePopulation(c);
        System.out.println(evaluateCandidates());
        DrawGraph draw = new DrawGraph(Population.get(0));
        /*
        while (true) {
            selectParents();
            produceOffspring();
            mutateOffspring();
            improveOffspring();
            selectSurvivors();
        }*/
    }

    private static void initializePopulation(int index) {
        Island[] vertexes = Population.get(index).vertexSet().toArray(new Island[0]);
        Collections.shuffle(Arrays.asList(vertexes));
        for (int c = 0; c < vertexes.length; c++) {
            Island curr = vertexes[c];
            if (curr.isComplete()) continue;
            int line = curr.getLine();
            int col = curr.getCol();
            for (int l1 = line + 1; l1 < Dimension; l1++) {
                Island highneighbor = HashiMatrix.get(index).get(l1).get(col);
                if (highneighbor.isComplete() && highneighbor.getBridgeNeeded() != 0) break;
                if (highneighbor.getBridgeNeeded() == 0) continue;
                Population.get(index).addEdge(curr, highneighbor);
                curr.increaseBridgeCount();
                highneighbor.increaseBridgeCount();
                break;
            }
            if (curr.isComplete()) continue;
            for (int c1 = col + 1; c1 < Dimension; c1++) {
                Island righthneighbor = HashiMatrix.get(index).get(line).get(c1);
                if (righthneighbor.isComplete()  && righthneighbor.getBridgeNeeded() != 0) break;
                if (righthneighbor.getBridgeNeeded() == 0) continue;
                Population.get(index).addEdge(curr, righthneighbor);
                curr.increaseBridgeCount();
                righthneighbor.increaseBridgeCount();
                break;
            }
            if (curr.isComplete()) continue;
            for (int l1 = line - 1; l1 > 0; l1--) {
                Island lowleighbor = HashiMatrix.get(index).get(l1).get(col);
                if (lowleighbor.isComplete() && lowleighbor.getBridgeNeeded() != 0) break;
                if (lowleighbor.getBridgeNeeded() == 0) continue;
                Population.get(index).addEdge(curr, lowleighbor);
                curr.increaseBridgeCount();
                lowleighbor.increaseBridgeCount();
                break;
            }
            if (curr.isComplete()) continue;
            for (int c1 = col - 1; c1 > 0; c1--) {
                Island leftleighbor = HashiMatrix.get(index).get(line).get(c1);
                if (leftleighbor.isComplete() && leftleighbor.getBridgeNeeded() != 0) break;
                if (leftleighbor.getBridgeNeeded() == 0) continue;
                Population.get(index).addEdge(curr, leftleighbor);
                curr.increaseBridgeCount();
                leftleighbor.increaseBridgeCount();
                break;
            }
        }
    }

    private static int evaluateCandidates() {
        int fitness = 0;
        int auxcounter = 0;
        DefaultEdge[] edges = Population.get(0).edgeSet().toArray(new DefaultEdge[0]);
        //calculates amount of bridge intersections
        for (int c = 0; c < edges.length; c++) {
            for (int d = c + 1; d + 1 < edges.length; d++) {
                Island p1 = Population.get(0).getEdgeSource(edges[c]);
                Island p2 = Population.get(0).getEdgeTarget(edges[c]);
                Island p3 = Population.get(0).getEdgeSource(edges[d]);
                Island p4 = Population.get(0).getEdgeTarget(edges[d]);
                if (GFG.doIntersect(new GFG.Point(p1.getCol(), p1.getLine()),
                        new GFG.Point(p2.getCol(), p2.getLine()),
                        new GFG.Point(p3.getCol(), p3.getLine()),
                        new GFG.Point(p4.getCol(), p4.getLine())))
                    fitness++;
            }
        }
        //calculates amount of incomplete islands
        for (Island isl : Population.get(0).vertexSet()) {
            if (!isl.isComplete())
                auxcounter++;
            if (Population.get(0).degreeOf(isl) > 1)
                fitness -= Population.get(0).degreeOf(isl);
        }
        return fitness;
    }

    private static void selectParents() {}

    private static void produceOffspring() {}

    private static void mutateOffspring() {}

    private static void improveOffspring() {}

    private static void selectSurvivors() {}
}
