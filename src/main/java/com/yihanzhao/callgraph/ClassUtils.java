package com.yihanzhao.callgraph;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

public class ClassUtils {

    public static void handleClass(JavaClass clazz, BiConsumer<CallNode, CallNode> methodCallConsumer,
                                   Consumer<JavaClass> classConsumer) {
        classConsumer.accept(clazz);
        handleClass(clazz, methodCallConsumer);
    }

    public static void handleClass(JavaClass clazz, BiConsumer<CallNode, CallNode> methodCallConsumer) {
        ConstantPoolGen constants = new ConstantPoolGen(clazz.getConstantPool());

        for (Method method : clazz.getMethods()) {
            MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
            MethodVisitor visitor = new MethodVisitor(mg, clazz, methodCallConsumer);
            visitor.start();
        }
    }

}
