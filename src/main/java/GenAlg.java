import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GenAlg {

    private static ArrayList<ArrayList<ArrayList<Island>>> HashiMatrix;
    private static ArrayList<Graph<Island, DefaultEdge>> Population;
    private static ArrayList<Integer> SelectedParents = new ArrayList<>();
    private static int Dimension;
    private static int STARTING_POPULATION;

    public static void HGA(ArrayList<ArrayList<ArrayList<Island>>> matrix, ArrayList<Graph<Island,
            DefaultEdge>> population, int dimension, int starting) {
        HashiMatrix = matrix;
        Dimension = dimension;
        Population = population;
        STARTING_POPULATION = starting;
        //initialize parents
        for (int c = 0; c < STARTING_POPULATION; c++)
            initializePopulation(c);
        for (int c = 0; c < Dimension * 100; c++) {
            selectParents();
            produceOffspring();
            improveOffspring();
            selectSurvivors();
        }
        new DrawGraph(Collections.min(Population, (t1, t2) -> {
            int value1 = evaluateCandidate(t1);
            int value2 = evaluateCandidate(t2);
            if (value1 < value2)
                return -1;
            else if (value1 > value2)
                return 1;
            else return 0;
        }));
        for (Graph<Island, DefaultEdge> g : Population)
           System.out.println(evaluateCandidate(g));
    }

    /*
    run through all vertices trying to complete each one
    while also avoiding illegal edges
    */
    private static void initializePopulation(int index) {
        Island[] vertexes = Population.get(index).vertexSet().toArray(new Island[0]);
        for (Island isl : vertexes) {
            if (isl.isComplete(Population.get(index))) continue;
            for (Island adj : isl.getAdjacentIslands()) {
                if (canAddEdge(adj, isl, Population.get(index)))
                    Population.get(index).addEdge(isl, adj, new DefaultEdge());
                if (canAddEdge(adj, isl, Population.get(index)))
                    Population.get(index).addEdge(isl, adj, new DefaultEdge());
            }
        }
    }

    /*
    calculates amount of incomplete vertices
    */
    private static int evaluateCandidate(Graph<Island, DefaultEdge> g) {
        int fitness = 0;
        for (Island isl : g.vertexSet())
            if (!isl.isComplete(g))
                fitness++;
        return fitness;
    }

    /*
    10 x k binary tournament, where k = 2
    */
    private static void selectParents() {
        for (int c = 0; c < 10; c++) {
            int pos = ThreadLocalRandom.current().nextInt(0, Population.size() - 1);
            int pos2 = ThreadLocalRandom.current().nextInt(0, Population.size() - 1);
            if (evaluateCandidate(Population.get(pos)) > evaluateCandidate(Population.get(pos2)))
                SelectedParents.add(pos2);
            else
                SelectedParents.add(pos);
        }
    }

    /*
    Creates children where the top left corner of the puzzle
    belongs to a parent and the rest belongs to the other.
    After creation, mutation is attempted
    */
    private static void produceOffspring() {
        int line = ThreadLocalRandom.current().nextInt(0, Dimension -1);
        int col = ThreadLocalRandom.current().nextInt(0, Dimension - 1);
        for (int c = 0; c + 1 < SelectedParents.size(); c+=2) {
            Graph<Island, DefaultEdge> child = new Multigraph<>(DefaultEdge.class);
            Graph<Island, DefaultEdge> parent1 = Population.get(SelectedParents.get(c));
            Graph<Island, DefaultEdge> parent2 = Population.get(SelectedParents.get(c + 1));
            for (Island i : parent1.vertexSet())
                child.addVertex(i);
            for (DefaultEdge bridge1 : parent1.edgeSet()) {
                Island isl = parent1.getEdgeSource(bridge1);
                Island isl2 = parent1.getEdgeTarget(bridge1);
                if (!isl.isComplete(parent1) && !isl2.isComplete(parent1)) {
                    if (isl.getLine() <= line && isl.getCol() <= col &&
                            isl2.getLine() <= line && isl2.getCol() <= col)
                        child.addEdge(isl, isl2, new DefaultEdge());
                }
            }
            for (DefaultEdge bridge : parent2.edgeSet()) {
                Island isl = parent2.getEdgeSource(bridge);
                Island isl2 = parent2.getEdgeTarget(bridge);
                if (!isl.isComplete(parent2) && isl2.isComplete(parent2)) {
                    if ((isl.getLine() > line && isl.getCol() > col || isl.getCol() > col
                            || isl.getLine() > line) && (isl2.getLine() > line && isl2.getCol() > col
                            || isl2.getCol() > col || isl2.getLine() > line))
                        child.addEdge(isl, isl2, new DefaultEdge());
                }
            }
            //attempt to connect the disjointed sub-graphs
            /*Collections.shuffle(Arrays.asList(child.vertexSet().toArray(new Island[0])));
            for (Island isl : Arrays.asList(child.vertexSet().toArray(new Island[0]))) {
                if (isl.isComplete()) continue;
                for (Island isl2 : isl.getAdjacentIslands()) {
                    if (isl2.isComplete()) continue;
                    if (canAddEdge(isl, isl2, child)) {
                        child.addEdge(isl, isl2);
                        isl.increaseBridgeCount();
                        isl2.increaseBridgeCount();
                    }
                }
            }*/
            mutateOffspring(child);
            Population.add(child);
        }
        SelectedParents.removeAll(SelectedParents);
    }

    /*
    Checks if it's possible to add an edge between p1 and p2
    */
    private static boolean canAddEdge(Island p1, Island p2, Graph<Island, DefaultEdge> puzzle) {
        if (p1.isComplete(puzzle) || p2.isComplete(puzzle)) return false;
        for (DefaultEdge bridge : puzzle.edgeSet()) {
            Island p3 = puzzle.getEdgeSource(bridge);
            Island p4 = puzzle.getEdgeTarget(bridge);
            if (GFG.doLinesIntersect(new GFG.LineSegment(new GFG.Point(p1.getCol(), p1.getLine()),
                            new GFG.Point(p2.getCol(), p2.getLine())),
                    new GFG.LineSegment(new GFG.Point(p3.getCol(), p3.getLine()),
                            new GFG.Point(p4.getCol(), p4.getLine()))))
                return false;
        }
        return true;
    }

    /*
    Mutates child with a 10% chance
    */
    private static void mutateOffspring(Graph<Island, DefaultEdge> child) {
        int probability = 10;
        if (probability != ThreadLocalRandom.current().nextInt(0, 100)) return;
        boolean aux = true;
        for (Island isl : child.vertexSet()) {
            for (Island isl2 : isl.getAdjacentIslands()) {
                if (child.getAllEdges(isl, isl2).size() > 1) {
                    child.removeEdge(isl, isl2);
                    aux = false;
                    break;
                }
            }
            if (!aux) break;
        }
        for (Island isl : child.vertexSet()) {
            if (isl.isComplete(child)) continue;
            for (Island isl2 : isl.getAdjacentIslands()) {
                if (isl2.isComplete(child)) continue;
                if (canAddEdge(isl, isl2, child))
                    child.addEdge(isl, isl2, new DefaultEdge());
            }
        }
    }

    private static void improveOffspring() {}

    private static void selectSurvivors() {
        Population.sort((t1, t2) -> {
            int value1 = evaluateCandidate(t1);
            int value2 = evaluateCandidate(t2);
            if (value1 < value2)
                return -1;
            else if (value1 > value2)
                return 1;
            else return 0;
        });
        for (int c = 50; c < Population.size(); c++)
            Population.remove(c);
    }
}
