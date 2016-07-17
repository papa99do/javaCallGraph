package com.yihanzhao.callgraph;

import com.yihanzhao.callgraph.visual.DotGraphVisualizer;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
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
        String[] methods = new String[] {"org.reflections.Configuration:getUrls()", "org.reflections.Reflections:getStore()"};

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
            parseClass(callGraph, className);
        }
        Duration duration = Duration.between(then, Instant.now());
        log(String.format("\nDone! Took %d seconds\n", duration.getSeconds()));
    }

    private static void parseClass(CallGraph callGraph, String className) {

        JavaClass clazz;
        try {
            clazz = Repository.lookupClass(className);
        } catch (ClassNotFoundException e) {
            return;
        }

        ConstantPoolGen constants = new ConstantPoolGen(clazz.getConstantPool());

        for (Method method : clazz.getMethods()) {
            MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
            MethodVisitor visitor = new MethodVisitor(mg, clazz, callGraph);
            visitor.start();
        }
    }

    private static void log(String message) {
        System.out.print(message);
    }
}
