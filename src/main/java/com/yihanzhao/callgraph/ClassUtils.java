package com.yihanzhao.callgraph;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

import java.util.function.BiConsumer;

public class ClassUtils {
    static void parseClass(String className, BiConsumer<CallNode, CallNode> action) {

        JavaClass clazz;
        try {
            clazz = Repository.lookupClass(className);
        } catch (ClassNotFoundException e) {
            return;
        }

        ConstantPoolGen constants = new ConstantPoolGen(clazz.getConstantPool());

        for (Method method : clazz.getMethods()) {
            MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
            MethodVisitor visitor = new MethodVisitor(mg, clazz, action);
            visitor.start();
        }
    }
}
