package com.yihanzhao.callgraph;

import java.util.*;
import java.util.stream.Collectors;

public class CallNode {
    private final String id;
    private final String className;
    private String simpleClassName;
    private final String methodName;
    private final List<String> argumentTypes;

    private final Set<CallNode> invokers;

    public CallNode(String className, String methodName, List<String> argumentTypes) {
        this.className = className;
        this.methodName = methodName;
        this.argumentTypes = argumentTypes;

        this.id = String.format("%s:%s(%s)", className, methodName,
                argumentTypes.stream().collect(Collectors.joining(", ")));

        this.simpleClassName = simpleName(className);

        this.invokers = new HashSet<>();
    }

    private String simpleName(String className) {
        int index = className.lastIndexOf('.');
        return index < 0 ? className : className.substring(index + 1);
    }

    public String getId() {
        return id;
    }

    public Set<CallNode> getInvokers() {
        return Collections.unmodifiableSet(invokers);
    }

    public void addInvoker(CallNode caller) {
        invokers.add(caller);
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getArgumentTypes() {
        return argumentTypes;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public String toShortName() {
        return simpleClassName + ":" + methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CallNode)) return false;

        CallNode callNode = (CallNode) o;

        return id.equals(callNode.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
