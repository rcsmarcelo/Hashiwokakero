import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class GenAlg {

    private static ArrayList<ArrayList<ArrayList<Island>>> HashiMatrix;
    private static ArrayList<Graph<Island, DefaultEdge>> Population;
    private static ArrayList<Integer> SelectedParents = new ArrayList<>();
    private static int Dimension;
    private static int STARTING_POPULATION;
    private static int Generations = Dimension * 50;

    public static void HGA(ArrayList<ArrayList<ArrayList<Island>>> matrix, ArrayList<Graph<Island,
            DefaultEdge>> population, int dimension, int starting) {
        HashiMatrix = matrix;
        Dimension = dimension;
        Population = population;
        STARTING_POPULATION = starting;
        //initialize parents
        for (int c = 0; c < STARTING_POPULATION; c++)
            initializePopulation(c);
        System.out.println(evaluateCandidates(0));

        //for (int c = 0; c < Generations; c++) {
            selectParents();
            produceOffspring();
            mutateOffspring();
            improveOffspring();
            selectSurvivors();
        //}
        DrawGraph draw = new DrawGraph(Population.get(Population.size() - 1));
        DrawGraph draw2 = new DrawGraph(Population.get(0));
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

    private static int evaluateCandidates(int index) {
        int fitness = 0;
        int auxcounter = 0;
        DefaultEdge[] edges = Population.get(index).edgeSet().toArray(new DefaultEdge[0]);
        //calculates amount of bridge intersections
        for (int c = 0; c < edges.length; c++) {
            for (int d = c + 1; d + 1 < edges.length; d++) {
                Island p1 = Population.get(index).getEdgeSource(edges[c]);
                Island p2 = Population.get(index).getEdgeTarget(edges[c]);
                Island p3 = Population.get(index).getEdgeSource(edges[d]);
                Island p4 = Population.get(index).getEdgeTarget(edges[d]);
                if (GFG.doIntersect(new GFG.Point(p1.getCol(), p1.getLine()),
                        new GFG.Point(p2.getCol(), p2.getLine()),
                        new GFG.Point(p3.getCol(), p3.getLine()),
                        new GFG.Point(p4.getCol(), p4.getLine())))
                    fitness++;
            }
        }
        //calculates amount of incomplete islands
        for (Island isl : Population.get(index).vertexSet()) {
            if (!isl.isComplete())
                auxcounter++;
            if (Population.get(index).degreeOf(isl) > 1)
                fitness -= Population.get(index).degreeOf(isl);
        }
        return fitness + auxcounter;
    }

    private static void selectParents() {
        for (int c = 0; c < 10; c++) {
            int pos = ThreadLocalRandom.current().nextInt(0, Population.size() - 1);
            int pos2 = ThreadLocalRandom.current().nextInt(0, Population.size() - 1);
            if (evaluateCandidates(pos) > evaluateCandidates(pos2))
                SelectedParents.add(pos2);
            else
                SelectedParents.add(pos);
        }
    }

    private static void produceOffspring() {
        int line = ThreadLocalRandom.current().nextInt(5, Dimension - 5);
        int col = ThreadLocalRandom.current().nextInt(5, Dimension - 5);
        for (int c = 0; c + 1 < SelectedParents.size(); c+=2) {
            Graph<Island, DefaultEdge> child = new Multigraph<>(DefaultEdge.class);
            Graph<Island, DefaultEdge> parent1 = Population.get(SelectedParents.get(c));
            Graph<Island, DefaultEdge> parent2 = Population.get(SelectedParents.get(c + 1));
            Island[] vtxs1 = parent1.vertexSet().toArray(new Island[0]);
            Island[] vtxs2 = parent2.vertexSet().toArray(new Island[0]);
            for (int i = 0; i < vtxs1.length; i++) {
                if (vtxs1[i].getLine() <= line && vtxs1[i].getCol() <= col)
                    child.addVertex(new Island(vtxs1[i]));
                if (vtxs2[i].getLine() > line && vtxs2[i].getCol() > col || vtxs2[i].getCol() > col
                    || vtxs2[i].getLine() > line)
                    child.addVertex(new Island(vtxs2[i]));
            }
            for (int i = 0; i < vtxs1.length; i++) {
                Island v = vtxs1[i];
                for (DefaultEdge bridge : parent1.outgoingEdgesOf(v)) {
                    Island i1 = parent1.getEdgeSource(bridge);
                    Island i2 = parent1.getEdgeTarget(bridge);
                    if (!child.containsEdge(i1, i2)) {
                        if ( (i1.getLine() <= line && i1.getCol() <= col &&
                                i2.getLine() <= line && i2.getCol() <= col)
                                || ((i1.getLine() > line && i1.getCol() > col || i1.getCol() > col
                                || i1.getLine() > line) && (i2.getLine() > line && i2.getCol() > col
                                || i2.getCol() > col || i2.getLine() > line))) {
                            child.addEdge(i1, i2, new DefaultEdge());
                            if (parent1.getAllEdges(i1, i2).size() > 1)
                                child.addEdge(i1, i2, new DefaultEdge());
                        }
                    }
                }
                v = vtxs2[i];
                for (DefaultEdge bridge : parent2.outgoingEdgesOf(v)) {
                    Island i1 = parent1.getEdgeSource(bridge);
                    Island i2 = parent1.getEdgeTarget(bridge);
                    if (!child.containsEdge(i1, i2)) {
                        if ( (i1.getLine() <= line && i1.getCol() <= col &&
                                i2.getLine() <= line && i2.getCol() <= col)
                                || ((i1.getLine() > line && i1.getCol() > col || i1.getCol() > col
                                || i1.getLine() > line) && (i2.getLine() > line && i2.getCol() > col
                                || i2.getCol() > col || i2.getLine() > line))) {
                            child.addEdge(i1, i2, new DefaultEdge());
                            if (parent2.getAllEdges(i1, i2).size() > 1)
                                child.addEdge(i1, i2, new DefaultEdge());
                        }
                    }
                }
            }
            Population.add(child);
        }
    }

    private static void mutateOffspring() {
        int probability = 10;
        for (Graph<Island, DefaultEdge> g : Population) {
            if (probability != ThreadLocalRandom.current().nextInt(0, 100)) continue;
        }
    }

    private static void improveOffspring() {}

    private static void selectSurvivors() {}
}
