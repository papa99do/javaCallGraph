package com.yihanzhao.callgraph;

import java.util.HashSet;
import java.util.Set;

public class MethodPrinter {

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Full class name is needed as first parameter");
        }

        String className = args[0];

        Set<String> methodSignatures = new HashSet<>();

        ClassUtils.parseClass(className, ((caller, callee) -> methodSignatures.add(caller.getId())));

        System.out.println(String.format("\nMethods in class %s:", className));
        methodSignatures.forEach(System.out::println);
    }
}
