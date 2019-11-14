public class GenAlg {

    public static void HGA() {
        initializePopulation();
        evaluateCandidates();
        while (true) {
            selectParents();
            produceOffspring();
            mutateOffspring();
            improveOffspring();
            selectSurvivors();
        }
    }

    private static void initializePopulation() {}

    private static void evaluateCandidates() {}

    private static void selectParents() {}

    private static void produceOffspring() {}

    private static void mutateOffspring() {}

    private static void improveOffspring() {}

    private static void selectSurvivors() {}
}
