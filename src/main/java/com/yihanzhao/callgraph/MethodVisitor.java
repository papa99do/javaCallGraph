package com.yihanzhao.callgraph;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Type;

public class MethodVisitor extends EmptyVisitor {

    private final MethodGen methodGen;
    private final JavaClass clazz;
    private final ConstantPoolGen constantPool;
    private final BiConsumer<CallNode, CallNode> biConsumer;

    public MethodVisitor(MethodGen methodGen, JavaClass clazz, BiConsumer<CallNode, CallNode> biConsumer) {

        this.methodGen = methodGen;
        this.clazz = clazz;
        this.constantPool = methodGen.getConstantPool();
        this.biConsumer = biConsumer;
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

        biConsumer.accept(caller, callee);
    }

    private List<String> typesToString(Type[] argumentTypes) {
        return Arrays.stream(argumentTypes).map(Type::getSignature).collect(Collectors.toList());
    }
}
