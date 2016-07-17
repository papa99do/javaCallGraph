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

import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        String[] packages = new String[] {"org.reflections"};
        String method = "org.reflections.Configuration:getUrls()";

        CallGraph callGraph = new CallGraph();

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(new SubTypesScanner(false))
                .filterInputsBy(new FilterBuilder().includePackage(packages)));

        Set<String> allTypes = reflections.getAllTypes();
        log("Parsing " + allTypes.size() + " classes...\n");
        allTypes.forEach(className -> parseClass(callGraph, className));
        log("\nDone!\n");

        new DotGraphVisualizer(System.out).visualize(callGraph, method);

    }

    private static void parseClass(CallGraph callGraph, String className) {
        log("\rParsing class: " + className);
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
