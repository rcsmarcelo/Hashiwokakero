import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.MaskSubgraph;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GenAlg {
    private static ArrayList<Graph<Island, DefaultEdge>> Population;
    private static ArrayList<Integer> SelectedParents = new ArrayList<>();
    private static int Dimension;

    public static void HGA(ArrayList<Graph<Island, DefaultEdge>> population, int dimension, int starting) {
        Dimension = dimension;
        Population = population;
        //initialize parents
        for (int c = 0; c < starting; c++)
            initializePopulation(c);
        new DrawGraph(Population.get(0));
        for (int c = 0; c < Dimension * 50; c++) {
            selectParents();
            produceOffspring();
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
    while also avoiding invalid edges
    */
    private static void initializePopulation(int index) {
        Island[] vertices = Population.get(index).vertexSet().toArray(new Island[0]);
        Collections.shuffle(Arrays.asList(vertices));
        for (Island isl : vertices) {
            Collections.shuffle(isl.getAdjacentIslands());
            for (Island adj : isl.getAdjacentIslands()) {
                if (canAddEdge(adj, isl, Population.get(index)))
                    Population.get(index).addEdge(isl, adj);
                if (canAddEdge(adj, isl, Population.get(index)))
                    Population.get(index).addEdge(isl, adj);
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
    public static void produceOffspring() {
        int line = ThreadLocalRandom.current().nextInt(4, Dimension - 5);
        int col = ThreadLocalRandom.current().nextInt(4, Dimension - 5);
        for (int c = 0; c + 1 < SelectedParents.size(); c+=2) {
            Graph<Island, DefaultEdge> child = new Multigraph<>(DefaultEdge.class);
            Graph<Island, DefaultEdge> parent1 = Population.get(SelectedParents.get(c));
            Graph<Island, DefaultEdge> parent2 = Population.get(SelectedParents.get(c + 1));
            DefaultEdge[] pe1 = parent1.edgeSet().toArray(new DefaultEdge[0]);
            DefaultEdge[] pe2 = parent2.edgeSet().toArray(new DefaultEdge[0]);
            Island[] vp1 = parent1.vertexSet().toArray(new Island[0]);
            int size = Math.max(pe1.length, pe2.length);
            size = Math.max(size, parent1.vertexSet().size());
            for (int aux = 0; aux < size; aux++) {
                if(aux < vp1.length) {
                    if (!child.containsVertex(vp1[aux]))
                        child.addVertex(new Island(vp1[aux].getLine(), vp1[aux].getCol(),
                                vp1[aux].getBridgeNeeded()));
                }
                if (aux < parent1.edgeSet().size()) {
                    Island isl = parent1.getEdgeSource(pe1[aux]);
                    Island isl2 = parent1.getEdgeTarget(pe1[aux]);
                    Island ch1 = new Island(isl.getLine(), isl.getCol(), isl.getBridgeNeeded());
                    Island ch2 = new Island(isl2.getLine(), isl2.getCol(), isl2.getBridgeNeeded());
                    if (!child.containsVertex(ch1))
                        child.addVertex(ch1);
                    if (!child.containsVertex(ch2))
                        child.addVertex(ch2);
                    if (isl.getLine() <= line && isl.getCol() <= col &&
                            isl2.getLine() <= line && isl2.getCol() <= col)
                        child.addEdge(ch1, ch2);
                }
                if (aux < parent2.edgeSet().size()) {
                    Island isl =  parent2.getEdgeSource(pe2[aux]);
                    Island isl2 = parent2.getEdgeTarget(pe2[aux]);
                    Island ch1 = new Island(isl.getLine(), isl.getCol(), isl.getBridgeNeeded());
                    Island ch2 = new Island(isl2.getLine(), isl2.getCol(), isl2.getBridgeNeeded());
                    if (!child.containsVertex(ch1))
                        child.addVertex(ch1);
                    if (!child.containsVertex(ch2))
                        child.addVertex(ch2);
                    if ((isl.getLine() > line && isl.getCol() > col || isl.getCol() > col
                            || isl.getLine() > line) && (isl2.getLine() > line && isl2.getCol() > col
                            || isl2.getCol() > col || isl2.getLine() > line))
                        child.addEdge(ch1, ch2);
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
            //mutateOffspring(child);
            improveOffspring(child);
            Population.add(child);
        }
        SelectedParents.removeAll(SelectedParents);
    }

    /*
    Checks if it's possible to add an edge between p1 and p2
    */
    private static boolean canAddEdge(Island p1, Island p2, Graph<Island, DefaultEdge> puzzle) {
        if (p1.isComplete(puzzle) || p2.isComplete(puzzle)) return false;
        if (puzzle.getAllEdges(p1, p2).size() == 2) return false;
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
        int probability = 20;
        if (probability != ThreadLocalRandom.current().nextInt(0, 100)) return;
        int pos = ThreadLocalRandom.current().nextInt(0, child.edgeSet().size() - 1);
        DefaultEdge random = child.edgeSet().toArray(new DefaultEdge[0])[pos];
        child.removeEdge(random);
        Collections.shuffle(Arrays.asList(child.vertexSet().toArray(new Island[0])));
        for (Island isl : child.vertexSet()) {
            if (isl.isComplete(child)) continue;
            for (Island isl2 : isl.getAdjacentIslands()) {
                if (isl2.isComplete(child)) continue;
                if (canAddEdge(isl, isl2, child))
                    child.addEdge(isl, isl2);
            }
        }
    }

    private static void improveOffspring(Graph<Island, DefaultEdge> child) {
        int counter = 0;
        while (counter < 50) {
            int a = evaluateCandidate(child);
            mutateOffspring(child);
            int b = evaluateCandidate(child);
            if (b < a)
                return;
            counter++;
        }
    }

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
