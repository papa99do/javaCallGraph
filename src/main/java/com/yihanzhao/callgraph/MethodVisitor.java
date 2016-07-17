package com.yihanzhao.callgraph;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodVisitor extends EmptyVisitor {

    private final MethodGen methodGen;
    private final JavaClass clazz;
    private final ConstantPoolGen constantPool;
    private final CallGraph callGraph;

    public MethodVisitor(MethodGen methodGen, JavaClass clazz, CallGraph callGraph) {

        this.methodGen = methodGen;
        this.clazz = clazz;
        this.constantPool = methodGen.getConstantPool();
        this.callGraph = callGraph;
    }

    public void start() {
        if (methodGen.isAbstract() || methodGen.isNative()) {
            return;
        }

        for(InstructionHandle ih = methodGen.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
            Instruction instruction = ih.getInstruction();

            short opcode = instruction.getOpcode();
            boolean visitInstruction = InstructionConstants.INSTRUCTIONS[opcode] != null
                    && !(instruction instanceof ConstantPushInstruction)
                    && !(instruction instanceof ReturnInstruction);

            if(!visitInstruction) {
                instruction.accept(this);
            }
        }
    }

    @Override
    public void visitInvokeInstruction(InvokeInstruction obj) {
        CallNode caller = new CallNode(clazz.getClassName(), methodGen.getName(),
                typesToString(methodGen.getArgumentTypes()));
        CallNode callee = new CallNode(obj.getReferenceType(constantPool).toString(),
                obj.getMethodName(constantPool), typesToString(obj.getArgumentTypes(constantPool)));

        callGraph.addCall(caller, callee);
    }

    private List<String> typesToString(Type[] argumentTypes) {
        return Arrays.stream(argumentTypes).map(Type::getSignature).collect(Collectors.toList());
    }
}
