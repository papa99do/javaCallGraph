package com.yihanzhao.callgraph;

import java.util.HashSet;
import java.util.Set;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import com.yihanzhao.callgraph.classutils.ClassScanner;

public class MethodPrinter {

    public static void main(String[] args) throws Exception {

        ListMethodsOptions options = CommandLineParser.parse(ListMethodsOptions.class, args,
                OptionStyle.LONG_OR_COMPACT);

        if (options.isHelp()) {
            options.usage();
            return;
        }

        try {
            String className = options.getClassName();

            ClassScanner scanner = new ClassScanner(options.getClassPath());

            Set<String> methodSignatures = new HashSet<>();

            ClassUtils.handleClass(scanner.parseClass(className), ((caller, callee) -> methodSignatures.add(caller.getId())));

            System.out.println(String.format("\nMethods in class %s:", className));
            methodSignatures.forEach(System.out::println);
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: " + e.getMessage());
            options.usage();
            System.exit(-1);
        }
    }
}
