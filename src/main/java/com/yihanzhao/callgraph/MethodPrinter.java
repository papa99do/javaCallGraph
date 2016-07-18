package com.yihanzhao.callgraph;

import java.util.HashSet;
import java.util.Set;

public class MethodPrinter {

    public static void main(String[] args) {
        String className = "com.aconex.doccontrol.controller.DocConfidentialControl";
        String filter = "confidential";

        Set<String> methodSignatures = new HashSet<>();

        ClassUtils.parseClass(className, ((caller, callee) -> methodSignatures.add(callee.getId())));

        methodSignatures.stream()
//                .filter(name -> name.toLowerCase().contains(filter))
                .forEach(System.out::println);
    }
}
