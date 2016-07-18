package com.yihanzhao.callgraph.visual;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yihanzhao.callgraph.CallGraph;
import com.yihanzhao.callgraph.CallNode;

public class DotGraphVisualizer implements CallGraphVisualizer {
    private static final int INDENT_STEP = 2;
    private static final byte[] INDENT_PATTERN = "        ".getBytes();

    private final OutputStream out;
    private final Map<String, Set<CallNode>> subGroupMap;
    private final Set<String> callSet;
    private int indentation;

    public DotGraphVisualizer(OutputStream out) {
        this.out = out;
        subGroupMap = new HashMap<>();
        callSet = new HashSet<>();
        indentation = 0;
    }

    @Override
    public void visualize(CallGraph graph, String... nodeIds) {

        for(String nodeId : nodeIds) {
            graph.walkDepthFirst(graph.getNode(nodeId), (callee, caller) -> {
                addToSubGraph(caller);
                addToSubGraph(callee);
                callSet.add(String.format("\"%s\" -> \"%s\";", asString(caller), asString(callee)));
            });
        }

        startGraph();

        drawSubGroup();
        drawCallSet();

        endGraph();

    }

    private void drawCallSet() {
        callSet.forEach(this::writeln);
    }

    private void endGraph() {
        indentation -= INDENT_STEP;
        writeln("}");
    }

    private void startGraph() {
        writeln("digraph call_graph {");
        indentation += INDENT_STEP;
    }

    private void startSubGraph(String name) {
        writeln(String.format("subgraph \"cluster_%s\" {", name));
        indentation += INDENT_STEP;
    }



    private String asString(CallNode node) {
        return Integer.toHexString(node.hashCode());
    }

    private void drawSubGroup() {
        for (Map.Entry<String, Set<CallNode>> entry: subGroupMap.entrySet()) {
            startSubGraph(entry.getKey());

            for(CallNode node : entry.getValue()) {
                writeln(String.format("\"%s\" [label=\"%s\"];", asString(node), node.getMethodName()));
            }

            writeln(String.format("label = \"%s\"", entry.getKey()));

            endGraph();
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
            out.write(INDENT_PATTERN, 0, indentation);
            out.write((line + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
