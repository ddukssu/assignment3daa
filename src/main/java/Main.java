import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] inputFiles = {"resources/small_graph.json", "resources/medium_graph.json", "resources/large_graph.json"};
        List<JsonObject> allResults = new ArrayList<>();

        for (String inputFile : inputFiles) {
            try {
                JsonObject inputJson = JsonParser.parseReader(new FileReader(inputFile)).getAsJsonObject();
                JsonArray graphs = inputJson.getAsJsonArray("graphs");

                for (JsonElement graphElem : graphs) {
                    JsonObject graphJson = graphElem.getAsJsonObject();
                    int graphId = graphJson.get("id").getAsInt();
                    JsonArray nodesJson = graphJson.getAsJsonArray("nodes");
                    JsonArray edgesJson = graphJson.getAsJsonArray("edges");

                    Graph graph = new Graph();
                    for (JsonElement node : nodesJson) {
                        graph.addNode(node.getAsString());
                    }
                    for (JsonElement edgeElem : edgesJson) {
                        JsonObject edge = edgeElem.getAsJsonObject();
                        graph.addEdge(edge.get("from").getAsString(), edge.get("to").getAsString(), edge.get("weight").getAsInt());
                    }

                    // Run Prim
                    PrimAlgorithm.Result primResult = PrimAlgorithm.computeMST(graph);

                    // Run Kruskal
                    KruskalAlgorithm.Result kruskalResult = KruskalAlgorithm.computeMST(graph);

                    // Build output
                    JsonObject result = new JsonObject();
                    result.addProperty("graph_id", graphId);

                    JsonObject inputStats = new JsonObject();
                    inputStats.addProperty("vertices", graph.getNumVertices());
                    inputStats.addProperty("edges", graph.getNumEdges());
                    result.add("input_stats", inputStats);

                    JsonObject primJson = new JsonObject();
                    JsonArray primEdges = new JsonArray();
                    for (Edge e : primResult.mstEdges) {
                        JsonObject eJson = new JsonObject();
                        eJson.addProperty("from", e.from);
                        eJson.addProperty("to", e.to);
                        eJson.addProperty("weight", e.weight);
                        primEdges.add(eJson);
                    }
                    primJson.add("mst_edges", primEdges);
                    primJson.addProperty("total_cost", primResult.totalCost);
                    primJson.addProperty("operations_count", primResult.operationsCount);
                    primJson.addProperty("execution_time_ms", primResult.executionTimeMs);
                    result.add("prim", primJson);

                    JsonObject kruskalJson = new JsonObject();
                    JsonArray kruskalEdges = new JsonArray();
                    for (Edge e : kruskalResult.mstEdges) {
                        JsonObject eJson = new JsonObject();
                        eJson.addProperty("from", e.from);
                        eJson.addProperty("to", e.to);
                        eJson.addProperty("weight", e.weight);
                        kruskalEdges.add(eJson);
                    }
                    kruskalJson.add("mst_edges", kruskalEdges);
                    kruskalJson.addProperty("total_cost", kruskalResult.totalCost);
                    kruskalJson.addProperty("operations_count", kruskalResult.operationsCount);
                    kruskalJson.addProperty("execution_time_ms", kruskalResult.executionTimeMs);
                    result.add("kruskal", kruskalJson);

                    allResults.add(result);

                    String outputFile = "outputs/" + inputFile.split("/")[1].replace(".json", "_output.json");
                    JsonObject outputWrapper = new JsonObject();
                    JsonArray resultsArray = new JsonArray();
                    resultsArray.add(result);
                    outputWrapper.add("results", resultsArray);
                    try (FileWriter writer = new FileWriter(outputFile)) {
                        new Gson().toJson(outputWrapper, writer);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                System.out.println("Graph disconnected: " + e.getMessage());
            }
        }

        try (FileWriter csvWriter = new FileWriter("outputs/results_summary.csv")) {
            csvWriter.append("Graph_ID,Algorithm,Total_Cost,Vertices,Edges,Operations,Execution_Time_ms\n");
            for (JsonObject res : allResults) {
                int graphId = res.get("graph_id").getAsInt();
                JsonObject stats = res.get("input_stats").getAsJsonObject();
                int vertices = stats.get("vertices").getAsInt();
                int edges = stats.get("edges").getAsInt();

                JsonObject prim = res.get("prim").getAsJsonObject();
                csvWriter.append(graphId + ",Prim," + prim.get("total_cost").getAsInt() + "," + vertices + "," + edges + "," +
                        prim.get("operations_count").getAsInt() + "," + prim.get("execution_time_ms").getAsDouble() + "\n");

                JsonObject kruskal = res.get("kruskal").getAsJsonObject();
                csvWriter.append(graphId + ",Kruskal," + kruskal.get("total_cost").getAsInt() + "," + vertices + "," + edges + "," +
                        kruskal.get("operations_count").getAsInt() + "," + kruskal.get("execution_time_ms").getAsDouble() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}