import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KruskalAlgorithm {
    private static class UnionFind {
        private Map<String, String> parent;
        private Map<String, Integer> rank;

        public UnionFind(List<String> nodes) {
            parent = new HashMap<>();
            rank = new HashMap<>();
            for (String node : nodes) {
                parent.put(node, node);
                rank.put(node, 0);
            }
        }

        public String find(String x) {
            if (!parent.get(x).equals(x)) {
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }

        public boolean union(String x, String y) {
            String rootX = find(x);
            String rootY = find(y);
            if (rootX.equals(rootY)) {
                return false; // Cycle
            }
            if (rank.get(rootX) < rank.get(rootY)) {
                parent.put(rootX, rootY);
            } else if (rank.get(rootX) > rank.get(rootY)) {
                parent.put(rootY, rootX);
            } else {
                parent.put(rootY, rootX);
                rank.put(rootX, rank.get(rootX) + 1);
            }
            return true;
        }
    }

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

    public static Result computeMST(Graph graph) {
        long startTime = System.nanoTime();
        int operationsCount = 0;

        List<Edge> edges = new ArrayList<>(graph.getEdges());
        Collections.sort(edges);
        operationsCount += edges.size() * Math.log(edges.size()); // Approximate sort operations

        UnionFind uf = new UnionFind(graph.getNodes());
        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;

        for (Edge edge : edges) {
            operationsCount += 2; // Find operations
            if (uf.union(edge.from, edge.to)) {
                mstEdges.add(edge);
                totalCost += edge.weight;
                operationsCount += 1; // Union
            }
            if (mstEdges.size() == graph.getNumVertices() - 1) {
                break;
            }
        }

        String root = uf.find(graph.getNodes().get(0));
        boolean connected = true;
        for (String node : graph.getNodes()) {
            if (!uf.find(node).equals(root)) {
                connected = false;
                break;
            }
        }
        if (!connected) {
            throw new IllegalStateException("Graph is disconnected");
        }

        double executionTimeMs = (System.nanoTime() - startTime) / 1_000_000.0;
        return new Result(mstEdges, totalCost, operationsCount, executionTimeMs);
    }
}