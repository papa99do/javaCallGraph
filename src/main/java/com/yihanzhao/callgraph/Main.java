package com.yihanzhao.callgraph;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import com.yihanzhao.callgraph.visual.DotGraphVisualizer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class Main {
    public static void main(String[] args) throws Exception {

        try {
            CallGraphOptions options = CommandLineParser.parse(CallGraphOptions.class, args, OptionStyle.LONG_OR_COMPACT);

            Set<String> allTypes = getAllTypes(options.getPackages());

            CallGraph callGraph = new CallGraph();
            InterfaceHelper interfaceHelper = new InterfaceHelper();

            initializeCallGraph(callGraph, interfaceHelper, allTypes);

            new DotGraphVisualizer(options.getOutput(), interfaceHelper).visualize(callGraph, options.getMethods());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            CallGraphOptions.printUsage();
            System.exit(-1);
        }
    }

    private static void debugCallGraph(CallGraph callGraph) {
        callGraph.getCallNodeMap().values().stream()
                .map(CallNode::getId)
                .filter(nodeId -> nodeId.contains(""))
                .forEach(System.out::println);
    }

    private static Set<String> getAllTypes(String[] packages) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(new SubTypesScanner(false))
                .filterInputsBy(new FilterBuilder().includePackage(packages)));

        Set<String> allClasses = new HashSet<>();
        for (String superType : reflections.getStore().keySet()) {
            allClasses.addAll(reflections.getStore().get(superType).values());
        }
        return allClasses;
    }

    private static void initializeCallGraph(CallGraph callGraph, InterfaceHelper interfaceHelper, Set<String> allTypes) {
        int totalCount = allTypes.size();
        int count = 1;
        Instant then = Instant.now();
        for (String className: allTypes) {
            log(String.format("\rParsing %d of %d classes: %s", count++, totalCount, className));
            ClassUtils.parseClass(className, callGraph::addCall, interfaceHelper::addClass);
        }
        Duration duration = Duration.between(then, Instant.now());
        log(String.format("\nDone! Took %d seconds\n", duration.getSeconds()));

        addCallViaInterfaces(callGraph, interfaceHelper);
    }

    private static void addCallViaInterfaces(CallGraph callGraph, InterfaceHelper interfaceHelper) {
        for (CallNode node: callGraph.getCallNodeMap().values()) {
            Set<String> interfaceNames = interfaceHelper.getInterfacesWithThisOnlyChild(node.getClassName());
            for (String interfaceName: interfaceNames) {
                CallNode viaInterface = callGraph.getCallNodeMap().get(node.getId().replace(node.getClassName(), interfaceName));
                if (viaInterface != null) {
                    viaInterface.getInvokers().forEach(node::addInvoker);
                }
            }
        }
    }

    private static void log(String message) {
        System.out.print(message);
    }
}
