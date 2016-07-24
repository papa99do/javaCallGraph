package com.yihanzhao.callgraph;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import com.yihanzhao.callgraph.classutils.ClassScanner;

import java.util.HashSet;
import java.util.Set;

public class MethodPrinter {

    public static void main(String[] args) throws Exception {

        ListMethodsOptions options = CommandLineParser.parse(ListMethodsOptions.class, args,
                OptionStyle.LONG_OR_COMPACT);

        String className = options.getClassName();

        ClassScanner scanner = new ClassScanner(options.getClassPath());

        Set<String> methodSignatures = new HashSet<>();

        ClassUtils.handleClass(scanner.parseClass(className), ((caller, callee) -> methodSignatures.add(caller.getId())));

        System.out.println(String.format("\nMethods in class %s:", className));
        methodSignatures.forEach(System.out::println);
    }
}
