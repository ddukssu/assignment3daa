import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private List<String> nodes;
    private List<Edge> edges;
    private Map<String, List<Edge>> adjacencyList;

    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        adjacencyList = new HashMap<>();
    }

    public void addNode(String node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
            adjacencyList.put(node, new ArrayList<>());
        }
    }

    public void addEdge(String from, String to, int weight) {
        addNode(from);
        addNode(to);
        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        adjacencyList.get(from).add(edge);
        Edge reverse = new Edge(to, from, weight);
        adjacencyList.get(to).add(reverse);
    }

    public List<String> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Map<String, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    public int getNumVertices() {
        return nodes.size();
    }

    public int getNumEdges() {
        return edges.size();
    }
}