package com.yihanzhao.callgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class CallGraph {
    private final Map<String, CallNode> callNodeMap;
    private Set<String> visitedCalls = new HashSet<>();

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

    public void walkDepthFirst(CallNode callee, BiConsumer<CallNode, CallNode> consumer) {
        callee.getInvokers().stream()
                .filter(caller -> !visitedCalls.contains(callSignature(callee, caller)))
                .forEach(caller -> {

            consumer.accept(callee, caller);
            visitedCalls.add(callSignature(callee, caller));
            walkDepthFirst(caller, consumer);
        });
    }

    private String callSignature(CallNode callee, CallNode caller) {
        return caller.getId() + callee.getId();
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

    public Map<String, CallNode> getCallNodeMap() {
        return callNodeMap;
    }
}
