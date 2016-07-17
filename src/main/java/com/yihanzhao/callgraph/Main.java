package com.yihanzhao.callgraph;

import com.yihanzhao.callgraph.visual.DotGraphVisualizer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        String[] packages = new String[] {"org.reflections"};
        String[] methods = new String[] {
                "org.reflections.Configuration:getUrls()",
                "org.reflections.Reflections:getStore()"
        };

        CallGraph callGraph = new CallGraph();

        initializeCallGraph(packages, callGraph);

        new DotGraphVisualizer(System.out).visualize(callGraph, methods);

    }

    private static void initializeCallGraph(String[] packages, CallGraph callGraph) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(new SubTypesScanner(false))
                .filterInputsBy(new FilterBuilder().includePackage(packages)));

        Set<String> allTypes = reflections.getAllTypes();
        int totalCount = allTypes.size();
        int count = 1;
        Instant then = Instant.now();
        for (String className: allTypes) {
            log(String.format("\rParsing %d of %d classes: %s", count++, totalCount, className));
            ClassUtils.parseClass(className, callGraph::addCall);
        }
        Duration duration = Duration.between(then, Instant.now());
        log(String.format("\nDone! Took %d seconds\n", duration.getSeconds()));
    }

    private static void log(String message) {
        System.out.print(message);
    }
}
