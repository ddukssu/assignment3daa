import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class PrimAlgorithm {
    public static class Result {
        List<Edge> mstEdges;
        int totalCost;
        int operationsCount;
        double executionTimeMs;

        public Result(List<Edge> mstEdges, int totalCost, int operationsCount, double executionTimeMs) {
            this.mstEdges = mstEdges;
            this.totalCost = totalCost;
            this.operationsCount = operationsCount;
            this.executionTimeMs = executionTimeMs;
        }
    }

    private static class VertexPriority implements Comparable<VertexPriority> {
        String vertex;
        int key;
        String parent;

        public VertexPriority(String vertex, int key, String parent) {
            this.vertex = vertex;
            this.key = key;
            this.parent = parent;
        }

        @Override
        public int compareTo(VertexPriority other) {
            return Integer.compare(this.key, other.key);
        }
    }

    public static Result computeMST(Graph graph) {
        long startTime = System.nanoTime();
        int operationsCount = 0;

        Map<String, List<Edge>> adj = graph.getAdjacencyList();
        String start = graph.getNodes().get(0);

        Map<String, Integer> key = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> inMST = new HashSet<>();

        PriorityQueue<VertexPriority> pq = new PriorityQueue<>();

        for (String v : graph.getNodes()) {
            key.put(v, Integer.MAX_VALUE);
            parent.put(v, null);
        }
        key.put(start, 0);
        pq.add(new VertexPriority(start, 0, null));
        operationsCount += graph.getNumVertices();

        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;

        while (!pq.isEmpty()) {
            VertexPriority u = pq.poll();
            operationsCount += 1;

            if (inMST.contains(u.vertex)) continue;
            inMST.add(u.vertex);

            if (u.parent != null) {
                mstEdges.add(new Edge(u.parent, u.vertex, u.key));
                totalCost += u.key;
            }

            for (Edge edge : adj.get(u.vertex)) {
                String v = edge.to;
                if (!inMST.contains(v) && edge.weight < key.get(v)) {
                    key.put(v, edge.weight);
                    parent.put(v, u.vertex);
                    pq.add(new VertexPriority(v, key.get(v), u.vertex));
                    operationsCount += 1;
                }
                operationsCount += 1;
            }
        }

        if (inMST.size() != graph.getNumVertices()) {
            throw new IllegalStateException("Graph is disconnected");
        }

        double executionTimeMs = (System.nanoTime() - startTime) / 1_000_000.0;
        return new Result(mstEdges, totalCost, operationsCount, executionTimeMs);
    }
}