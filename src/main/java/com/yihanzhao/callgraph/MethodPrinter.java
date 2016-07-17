package com.yihanzhao.callgraph;

import java.util.HashSet;
import java.util.Set;

public class MethodPrinter {

    public static void main(String[] args) {
        String className = "com.yihanzhao.callgraph.example.Foo";

        Set<String> methodSignatures = new HashSet<>();

        ClassUtils.parseClass(className, ((caller, callee) -> methodSignatures.add(caller.getId())));

        methodSignatures.forEach(System.out::println);
    }
}
