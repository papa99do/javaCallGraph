package com.yihanzhao.callgraph;

import com.github.jankroken.commandline.annotations.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CallGraphOptions {

    private Set<String> packages;
    private List<String> methods;
    private OutputStream output;

    @Option
    @LongSwitch("packages")
    @ShortSwitch("p")
    @AllAvailableArguments
    public void setPackages(List<String> packages) {
        this.packages = new HashSet<>(packages);
    }

    @Option
    @LooseArguments
    @Multiple
    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    @Option
    @LongSwitch("output")
    @ShortSwitch("o")
    @SingleArgument
    public void setOutputFile(String fileName) {
        try {
            output = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public String[] getPackages() {
        return packages == null ? getPackagesFromMethods() : packages.toArray(new String[packages.size()]);
    }

    private String[] getPackagesFromMethods() {
        assertMethodsExist();
        System.out.println("Packages not specified, will derive from methods");

        Set<String> packageNameSet = methods.stream().map(method -> {
            String className = method.substring(0, method.indexOf(':'));
            String packageName = className.substring(0, className.lastIndexOf('.'));
            return packageName;
        }).collect(Collectors.toSet());

        return packageNameSet.toArray(new String[packageNameSet.size()]);
    }

    public String[] getMethods() {
        assertMethodsExist();
        return methods.toArray(new String[methods.size()]);
    }

    private void assertMethodsExist() {
        if (methods == null || methods.size() < 1 || methods.get(0).isEmpty()) {
            throw new IllegalArgumentException("At least one method need to be specified");
        }
    }

    public OutputStream getOutput() {
        return output == null ? System.out : output;
    }

    public static void printUsage() {
        System.out.println("\n" +
                "Usage: ./draw_call_graph.sh [-p <package1> <package2>] [-o <output.dot>] <method1> [<method2>]\n" +
                "\t-p --package \t\t packages to be included, optional, will derive from methods is not specified\n" +
                "\t-o --output \t\t output file, optional, will use standard output if nto specified\n" +
                "\t<method> \t\t method signature from list_method.sh, at least one is required\n" +
                "\n\n");
    }

    @Override
    public String toString() {
        return "CallGraphOptions{" +
                "packages=" + Arrays.toString(getPackages()) +
                ", methods=" + Arrays.toString(getMethods()) +
                '}';
    }
}
