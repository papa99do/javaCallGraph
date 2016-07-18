package com.yihanzhao.callgraph;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.yihanzhao.callgraph.visual.DotGraphVisualizer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class Main {
    public static void main(String[] args) throws Exception {
        String[] packages = new String[] {"com.aconex"};
        String[] methods = new String[] {
                "com.aconex.doccontrol.bo.UpdatingControlledDocBO:makeConfidential(J, Ljava/lang/Long;, Ljava/util/List;)",
                "com.aconex.doccontrol.bo.UpdatingControlledDocBO:makeConfidential(J, Ljava/lang/Long;)",
                "com.aconex.doccontrol.bo.UpdatingControlledDocBO:makeDocInRecipientRegisterConfidential(Lcom/aconex/doccontrol/bean/ControlledDocument;, Lcom/aconex/doccontrol/bean/ControlledDocument;, Lcom/aconex/security/bean/User;, Ljava/lang/Long;, Lcom/aconex/doccontrol/bo/CDSource;)",
                "com.aconex.doccontrol.bo.UpdatingControlledDocBO:makeNonConfidential(J, Ljava/lang/Long;)",
                "com.aconex.doccontrol.bo.UpdatingControlledDocBO:makeNonConfidential(Lcom/aconex/doccontrol/bean/ControlledDocument;, Ljava/lang/Long;)",

                "com.aconex.doccontrol.bo.UpdatingControlledDocBO:addConfidentialUsers(Lcom/aconex/doccontrol/bean/ControlledDocument;, Ljava/lang/Long;, Ljava/util/List;, Z)",
                "com.aconex.doccontrol.bo.UpdatingControlledDocBO:addConfidentialUsersForRegister(Lcom/aconex/doccontrol/bean/ControlledDocument;, Lcom/aconex/doccontrol/bo/CDSource;, Ljava/lang/Long;, Lcom/aconex/security/bean/User;)",
                "com.aconex.doccontrol.bo.UpdatingControlledDocBO:removeConfidentialUsers(Lcom/aconex/doccontrol/bean/ControlledDocument;, Ljava/lang/Long;, Ljava/util/List;, Z)",
                "com.aconex.doccontrol.bo.UpdatingControlledDocBO:removeAllConfidentialUsers(Lcom/aconex/doccontrol/bean/ControlledDocument;, Ljava/lang/Long;)",
                "com.aconex.doccontrol.bo.UpdatingControlledDocBO:updateConfidentialUsers(Lcom/aconex/doccontrol/bean/ControlledDocument;, Lcom/aconex/doccontrol/bean/ControlledDocument;, Lcom/aconex/security/bean/User;, Ljava/lang/Long;, Lcom/aconex/doccontrol/bo/CDSource;)",
                "com.aconex.doccontrol.bo.UpdatingControlledDocBO:updateConfidentialUsers(Lcom/aconex/doccontrol/bean/ControlledDocument;, Ljava/lang/Long;, Ljava/util/List;)",
                "com.aconex.doccontrol.bo.UpdatingControlledDocBO:saveConfidentialChanges(Lcom/aconex/doccontrol/bean/ControlledDocument;, Lcom/aconex/doccontrol/bean/ControlledDocument;, Ljava/lang/Long;)"
        };

        Set<String> allTypes = getAllTypes(packages);
//        allTypes.forEach(System.out::println);

        CallGraph callGraph = new CallGraph();

        initializeCallGraph(callGraph, allTypes);

//        debugCallGraph(callGraph);

        new DotGraphVisualizer(System.out).visualize(callGraph, methods);

    }

    private static void debugCallGraph(CallGraph callGraph) {
        callGraph.getCallNodeMap().values().stream()
                .map(CallNode::getId)
                .filter(nodeId -> nodeId.contains("DocConfidentialControl"))
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

    private static void initializeCallGraph(CallGraph callGraph, Set<String> allTypes) {
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
