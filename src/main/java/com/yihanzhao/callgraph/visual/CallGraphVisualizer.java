package com.yihanzhao.callgraph.visual;

import com.yihanzhao.callgraph.CallGraph;

public interface CallGraphVisualizer {
    void visualize(CallGraph graph, String... nodeId);
}
