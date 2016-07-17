package com.yihanzhao.callgraph;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CallGraph {
    private final Map<String, CallNode> callNodeMap;


    public CallGraph() {
        this.callNodeMap = new HashMap<>();
    }

    public void addCall(CallNode caller, CallNode callee) {
        caller = findCallNode(caller);
        callee = findCallNode(callee);
        callee.addInvoker(caller);
    }

    public boolean hasNode(String id) {
        return callNodeMap.containsKey(id);
    }

    public void walkWidthFirst(CallNode callee, BiConsumer<CallNode, CallNode> consumer) {
        for (CallNode caller: callee.getInvokers()) {
            consumer.accept(callee, caller);
        }

        for (CallNode caller: callee.getInvokers()) {
            walkWidthFirst(caller, consumer);
        }
    }

    public void walkDepthFirst(CallNode callee, BiConsumer<CallNode, CallNode> consumer) {
        for (CallNode caller: callee.getInvokers()) {
            consumer.accept(callee, caller);
            walkDepthFirst(caller, consumer);
        }
    }

    private CallNode findCallNode(CallNode callNode) {
        if (!callNodeMap.containsKey(callNode.getId())) {
            callNodeMap.put(callNode.getId(), callNode);
        }
        return callNodeMap.get(callNode.getId());
    }

    public CallNode getNode(String nodeId) {
        if (!hasNode(nodeId)) {
            throw new IllegalArgumentException("Node " + nodeId + " does not exist");
        }
        return callNodeMap.get(nodeId);
    }
}
