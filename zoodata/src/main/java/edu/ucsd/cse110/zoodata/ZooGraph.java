package edu.ucsd.cse110.zoodata;

import androidx.annotation.NonNull;

import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.json.JSONImporter;

import java.io.Reader;
import java.util.function.Supplier;

public class ZooGraph extends DefaultUndirectedWeightedGraph<String, ZooGraph.Edge> {
    public ZooGraph() {
        // Omit the edge type in construction.
        super(Edge.class);
    }

    public ZooGraph(Class<? extends Edge> edgeClass) {
        // Use a subtype of IWE in construction. Needed to respect Liskov Substitution.
        super(edgeClass);
    }

    public ZooGraph(Supplier<String> vertexSupplier, Supplier<Edge> edgeSupplier) {
        super(vertexSupplier, edgeSupplier);
    }

    public static ZooGraph fromJson(Reader reader) {
        var g = new ZooGraph();
        var importer = new JSONImporter<String, Edge>();
        importer.setVertexFactory(v -> v);
        importer.addEdgeAttributeConsumer(ZooGraph.Edge::attributeConsumer);
        importer.importGraph(g, reader);
        return g;
    }

    public static class Edge extends DefaultWeightedEdge {
        private String id = null;

        @SuppressWarnings("unused")
        public Edge() { /* required by SupplierUtil.createSupplier inside JGraphT */ }

        public Edge(String id) {
            this.id = id;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        @NonNull
        @Override
        public String toString() {
            return String.format("(%s :%s: %s)", getSource(), id, getTarget());
        }

        public static void attributeConsumer(Pair<Edge, String> pair, Attribute attr) {
            var edge = pair.getFirst();
            var attrName = pair.getSecond();
            var attrValue = attr.getValue();

            if (attrName.equals("id")) {
                edge.setId(attrValue);
            }
        }
    }
}
