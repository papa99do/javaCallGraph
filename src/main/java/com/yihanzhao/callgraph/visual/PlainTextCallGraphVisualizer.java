package com.yihanzhao.callgraph.visual;

import com.yihanzhao.callgraph.CallGraph;
import com.yihanzhao.callgraph.CallNode;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class PlainTextCallGraphVisualizer implements CallGraphVisualizer {
    private final OutputStream writer;

    public PlainTextCallGraphVisualizer(PrintStream writer) {
        this.writer = writer;
    }

    @Override
    public void visualize(CallGraph graph, String nodeId) {

        CallNode startNode = graph.getNode(nodeId);

        graph.walkDepthFirst(startNode, (callee, caller) -> {
            try {
                writer.write(String.format("%s <- %s\n", asString(callee), asString(caller)).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private String asString(CallNode node) {
        return node.toShortName();
    }
}
