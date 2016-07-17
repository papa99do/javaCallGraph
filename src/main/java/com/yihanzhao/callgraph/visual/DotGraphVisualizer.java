package com.yihanzhao.callgraph.visual;

import com.yihanzhao.callgraph.CallGraph;
import com.yihanzhao.callgraph.CallNode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DotGraphVisualizer implements CallGraphVisualizer {
    private final OutputStream out;
    private final Map<String, Set<CallNode>> subGroupMap;

    public DotGraphVisualizer(OutputStream out) {
        this.out = out;
        subGroupMap = new HashMap<>();
    }

    @Override
    public void visualize(CallGraph graph, String nodeId) {
        CallNode startNode = graph.getNode(nodeId);

        try {
            writeln("digraph call_graph {");

            graph.walkDepthFirst(startNode, (callee, caller) -> {
                addToSubGraph(caller);
                addToSubGraph(callee);
                writeln(String.format("  \"%s\" -> \"%s\";", asString(caller), asString(callee)));
            });

            drawSubGroup();

            writeln("}");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String asString(CallNode node) {
        return node.toShortName();
    }

    private void drawSubGroup() throws IOException {
        for (Map.Entry<String, Set<CallNode>> entry: subGroupMap.entrySet()) {
            if (entry.getValue().size() <= 1) {
                continue;
            }

            writeln(String.format("subgraph \"cluster_%s\" {", entry.getKey()));

            for(CallNode node : entry.getValue()) {
                writeln(String.format("  \"%s\";", asString(node)));
            }

            writeln(String.format("label = \"%s\"", entry.getKey()));
            writeln("}");
        }
    }

    private void addToSubGraph(CallNode node) {
        String key = node.getSimpleClassName();
        if (!subGroupMap.containsKey(key)) {
            subGroupMap.put(key, new HashSet<>());
        }
        subGroupMap.get(key).add(node);
    }

    private void writeln(String line) {
        try {
            out.write((line + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
