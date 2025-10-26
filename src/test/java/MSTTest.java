import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MSTTest {

    private Graph createTestGraph() {
        Graph graph = new Graph();
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addNode("D");
        graph.addEdge("A", "B", 1);
        graph.addEdge("A", "C", 4);
        graph.addEdge("B", "C", 2);
        graph.addEdge("B", "D", 5);
        graph.addEdge("C", "D", 3);
        return graph;
    }

    @Test
    public void testPrimCorrectness() {
        Graph graph = createTestGraph();
        PrimAlgorithm.Result result = PrimAlgorithm.computeMST(graph);

        assertEquals(6, result.totalCost);
        assertEquals(3, result.mstEdges.size()); // V-1
        assertTrue(result.executionTimeMs >= 0);
        assertTrue(result.operationsCount >= 0);
    }

    @Test
    public void testKruskalCorrectness() {
        Graph graph = createTestGraph();
        KruskalAlgorithm.Result result = KruskalAlgorithm.computeMST(graph);

        assertEquals(6, result.totalCost);
        assertEquals(3, result.mstEdges.size());
        assertTrue(result.executionTimeMs >= 0);
        assertTrue(result.operationsCount >= 0);
    }

    @Test
    public void testSameCost() {
        Graph graph = createTestGraph();
        PrimAlgorithm.Result prim = PrimAlgorithm.computeMST(graph);
        KruskalAlgorithm.Result kruskal = KruskalAlgorithm.computeMST(graph);
        assertEquals(prim.totalCost, kruskal.totalCost);
    }

    @Test
    public void testDisconnectedGraph() {
        Graph graph = new Graph();
        graph.addNode("A");
        graph.addNode("B");
        // No edges

        assertThrows(IllegalStateException.class, () -> PrimAlgorithm.computeMST(graph));
        assertThrows(IllegalStateException.class, () -> KruskalAlgorithm.computeMST(graph));
    }
}