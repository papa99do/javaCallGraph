package com.yihanzhao.callgraph;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import com.yihanzhao.callgraph.classutils.ClassScanner;
import com.yihanzhao.callgraph.visual.DotGraphVisualizer;
import org.apache.bcel.classfile.JavaClass;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class Main {
    public static void main(String[] args) throws Exception {

        DrawCallGraphOptions options = CommandLineParser.parse(DrawCallGraphOptions.class, args,
                OptionStyle.LONG_OR_COMPACT);

        try {
            ClassScanner scanner = new ClassScanner(options.getClassPath(), options.getPackages());
            Set<String> allTypes = scanner.getAllClassNames().collect(toSet());

            CallGraph callGraph = new CallGraph();
            InterfaceHelper interfaceHelper = new InterfaceHelper();

            initializeCallGraph(scanner, callGraph, interfaceHelper, allTypes);

            new DotGraphVisualizer(options.getOutput(), interfaceHelper).visualize(callGraph, options.getMethods());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            options.usage();
            System.exit(-1);
        }
    }

    private static void initializeCallGraph(ClassScanner scanner, CallGraph callGraph,
                                            InterfaceHelper interfaceHelper, Set<String> allTypes) {
        int totalCount = allTypes.size();
        int count = 1;
        Instant then = Instant.now();
        for (String className: allTypes) {
            log(String.format("\rParsing %d of %d classes: %s", count++, totalCount, className));
            JavaClass clazz;
            try {
                clazz = scanner.parseClass(className);
            } catch (ClassNotFoundException e) {
                continue;
            }
            ClassUtils.handleClass(clazz, callGraph::addCall, interfaceHelper::addClass);
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
