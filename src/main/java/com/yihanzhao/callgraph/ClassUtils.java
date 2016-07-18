package com.yihanzhao.callgraph;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

public class ClassUtils {
    static void parseClass(String className, BiConsumer<CallNode, CallNode> methodCallConsumer, Consumer<JavaClass> classConsumer) {

        JavaClass clazz;
        try {
            clazz = Repository.lookupClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (classConsumer != null) {
            classConsumer.accept(clazz);
        }

        ConstantPoolGen constants = new ConstantPoolGen(clazz.getConstantPool());

        for (Method method : clazz.getMethods()) {
            MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
            MethodVisitor visitor = new MethodVisitor(mg, clazz, methodCallConsumer);
            visitor.start();
        }
    }

    public static void parseClass(String className, BiConsumer<CallNode, CallNode> methodCallConsumer) {
        parseClass(className, methodCallConsumer, null);
    }
}
