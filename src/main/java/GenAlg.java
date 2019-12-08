import org.apache.commons.lang3.time.StopWatch;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GenAlg {
    private static ArrayList<Graph<Island, DefaultEdge>> Population;
    private static ArrayList<Integer> SelectedParents = new ArrayList<>();
    private static int Dimension;

    public static void HGA(ArrayList<Graph<Island, DefaultEdge>> population, int dimension, int starting) throws InterruptedException {
        Dimension = dimension;
        Population = population;
        //initialize parents
        for (int c = 0; c < starting; c++)
            initializePopulation(c);
        StopWatch watch = new StopWatch();
        watch.start();
        for (int c = 0; c < Dimension * 10; c++) {
            if (evaluateCandidate(Population.get(0)) == 0)
                break;
            selectParents();
            produceOffspring();
            selectSurvivors();
        }
        watch.stop();
        System.out.println("Duration: " + watch.getTime() / 1000 + "s");
        new DrawGraph(Collections.min(Population, (t1, t2) -> {
            int value1 = evaluateCandidate(t1);
            int value2 = evaluateCandidate(t2);
            if (value1 < value2)
                return -1;
            else if (value1 > value2)
                return 1;
            else return 0;
        }));
    }

    private static ArrayList<Island> BFS(Graph<Island, DefaultEdge> child, Island v) {
        HashMap<Island, Boolean> discoveredAdd = new HashMap<>();
        HashMap<Island, Boolean> discoveredRm = new HashMap<>();
        ArrayList<Island> path = new ArrayList<>();
        Queue<Island> queue = new ArrayDeque<>();
        boolean isAdd = true;
        queue.add(v);
        path.add(v);
        while (!queue.isEmpty()) {
            v = queue.poll();
            if (!v.isComplete(child) && !isAdd) {
                path.add(v);
                return path;
            }
            if (isAdd) {
                discoveredAdd.put(v, true);
                for (Island u : v.getAdjacentIslands()) {
                    if (!discoveredAdd.containsKey(u))
                        discoveredAdd.put(u, false);
                    if (!discoveredAdd.get(u) && child.getAllEdges(v, u).size() <= 1
                            && canAddEdge2(u, v, child)) {
                        discoveredAdd.put(u, true);
                        queue.add(u);
                        path.add(u);
                        isAdd = false;
                        break;
                    }
                }
            }
            else {
                discoveredRm.put(v, true);
                for (Island u : v.getAdjacentIslands()) {
                    if (!discoveredRm.containsKey(u))
                        discoveredRm.put(u, false);
                    if (!discoveredRm.get(u) && child.getAllEdges(v, u).size() > 0) {
                        discoveredRm.put(u, true);
                        queue.add(u);
                        path.add(u);
                        isAdd = true;
                        break;
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    /*
    run through all vertices trying to complete each one
    while also avoiding invalid edges
    */
    private static void initializePopulation(int index) {
        Island[] vertices = Population.get(index).vertexSet().toArray(new Island[0]);
        for (Island isl : vertices) {
            if (isl.getBridgeNeeded() == 8) {
                for (Island adj : isl.getAdjacentIslands()) {
                    if (canAddEdge(isl, adj, Population.get(index)))
                        Population.get(index).addEdge(isl, adj);
                    if (canAddEdge(isl, adj, Population.get(index)))
                        Population.get(index).addEdge(adj, isl);
                }
            }
            else if (isl.getBridgeNeeded() == 7) {
                for (Island adj : isl.getAdjacentIslands()) {
                    if (canAddEdge(isl, adj, Population.get(index)))
                        Population.get(index).addEdge(isl, adj);
                }
            }
            else if (isl.getBridgeNeeded() == 5 && ((isl.getCol() == 0 && isl.getLine() != 0)
                        || (isl.getCol() == Dimension - 1 && isl.getLine() != 0)
                        || (isl.getLine() == 0 && isl.getCol() != 0)
                        || (isl.getLine() == Dimension - 1 && isl.getCol() != 0))) {
                for (Island adj : isl.getAdjacentIslands())
                    if (canAddEdge(isl, adj, Population.get(index)))
                        Population.get(index).addEdge(isl, adj);
            }
        }

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
        if(!GraphTests.isConnected(g))
            fitness+=15;
        return fitness;
    }

    /*
    50 x k binary tournament, where k = 2
    */
    private static void selectParents() {
        for (int c = 0; c < 50; c++) {
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
    After creation, mutation is attempted and local search is performed
    */
    public static void produceOffspring() throws InterruptedException {
        for (int c = 0; c + 1 < SelectedParents.size(); c+=2) {
            int line = ThreadLocalRandom.current().nextInt(4, Dimension - 5);
            int col = ThreadLocalRandom.current().nextInt(4, Dimension - 5);
            Graph<Island, DefaultEdge> child = new Multigraph<>(DefaultEdge.class);
            Graph<Island, DefaultEdge> parent1 = Population.get(SelectedParents.get(c));
            Graph<Island, DefaultEdge> parent2 = Population.get(SelectedParents.get(c + 1));
            DefaultEdge[] pe1 = parent1.edgeSet().toArray(new DefaultEdge[0]);
            DefaultEdge[] pe2 = parent2.edgeSet().toArray(new DefaultEdge[0]);
            Island[] vp1 = parent1.vertexSet().toArray(new Island[0]);
            int size = Math.max(pe1.length, pe2.length);
            size = Math.max(size, parent1.vertexSet().size());
            //run through parents adding vertices and edges to child
            for (int aux = 0; aux < size; aux++) {
                if(aux < vp1.length) {
                    if (!child.containsVertex(vp1[aux]))
                        child.addVertex(new Island(vp1[aux].getLine(), vp1[aux].getCol(),
                                vp1[aux].getBridgeNeeded(), vp1[aux].getAdjacentIslands()));
                }
                if (aux < parent1.edgeSet().size()) {
                    Island isl = parent1.getEdgeSource(pe1[aux]);
                    Island isl2 = parent1.getEdgeTarget(pe1[aux]);
                    Island ch1 = new Island(isl.getLine(), isl.getCol(), isl.getBridgeNeeded(),
                            isl.getAdjacentIslands());
                    Island ch2 = new Island(isl2.getLine(), isl2.getCol(), isl2.getBridgeNeeded(),
                            isl2.getAdjacentIslands());
                    if (!child.containsVertex(ch1))
                        child.addVertex(ch1);
                    if (!child.containsVertex(ch2))
                        child.addVertex(ch2);
                    if ((isl.getLine() <= line && isl.getCol() <= col) &&
                            (isl2.getLine() <= line && isl2.getCol() <= col)
                    )
                        child.addEdge(ch1, ch2);

                }
                if (aux < parent2.edgeSet().size()) {
                    Island isl =  parent2.getEdgeSource(pe2[aux]);
                    Island isl2 = parent2.getEdgeTarget(pe2[aux]);
                    Island ch1 = new Island(isl.getLine(), isl.getCol(), isl.getBridgeNeeded(),
                            isl.getAdjacentIslands());
                    Island ch2 = new Island(isl2.getLine(), isl2.getCol(), isl2.getBridgeNeeded(),
                            isl2.getAdjacentIslands());
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
            mutateOffspring(child);
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

    private static boolean canAddEdge2(Island p1, Island p2, Graph<Island, DefaultEdge> puzzle) {
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
    Removes all bridges from an incomplete island and its neighbors with a 10% chance
    */
    private static void mutateOffspring(Graph<Island, DefaultEdge> child) {
        if (new Random().nextInt(100) > 20) return;
        ArrayList<DefaultEdge> edges = new ArrayList<>();
        Island isl;
        int counter = 0;
        //find incomplete island
        do {
            int pos = ThreadLocalRandom.current().nextInt(0, child.vertexSet().size() - 1);
            isl = child.vertexSet().toArray(new Island[0])[pos];
            counter++;
            if (counter >= child.vertexSet().size())
                return;
        } while (isl.getBridgeNeeded() != 8 && isl.getBridgeNeeded() != 7 && isl.isComplete(child));
        edges.addAll(child.edgesOf(isl));
        //find neighbors' edges
        for (Island adj : isl.getAdjacentIslands()) {
            if (adj.getBridgeNeeded() == 8|| adj.getBridgeNeeded() == 7) continue;
            edges.addAll(child.getAllEdges(adj, isl));
        }
        //remove everything
        child.removeAllEdges(edges);
    }

    private static void improveOffspring(Graph<Island, DefaultEdge> child) {
        int size = evaluateCandidate(child);
        for (int c = 0; c < size; c++) {
            Island start;
            int counter = 0;
            do {
                int pos = ThreadLocalRandom.current().nextInt(0, child.vertexSet().size() - 1);
                start = child.vertexSet().toArray(new Island[0])[pos];
                if (counter >= child.vertexSet().size())
                    return;
                counter++;
            } while (start.isComplete(child));
            if (start == null)
                return;
            ArrayList<Island> path = BFS(child, start);
            for (int d = 0; d + 1 < path.size(); d++) {
                if (d % 2 == 0)
                    child.addEdge(path.get(d), path.get(d + 1));
                else
                    child.removeEdge(path.get(d), path.get(d + 1));
            }
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
        for (int c = 100; c < Population.size(); c++)
            Population.remove(c);
    }
}
