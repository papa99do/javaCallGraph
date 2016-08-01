package com.yihanzhao.callgraph;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.jankroken.commandline.annotations.LongSwitch;
import com.github.jankroken.commandline.annotations.LooseArguments;
import com.github.jankroken.commandline.annotations.Multiple;
import com.github.jankroken.commandline.annotations.Option;
import com.github.jankroken.commandline.annotations.ShortSwitch;
import com.github.jankroken.commandline.annotations.SingleArgument;

public class DrawCallGraphOptions extends BaseOptions {

    private Set<String> packages;
    private List<String> methods;
    private OutputStream output;

    protected void usage() {
        System.out.println("\n" +
                "Usage: ./draw_call_graph.sh -c classpath [-p <package1>:<package2>] [-o <output.dot>] <method1> [<method2>]\n" +
                "\t-c --classpath \t\t classpath of the classes under analysis\n" +
                "\t-p --packages \t\t packages to be included, optional, will derive from methods is not specified\n" +
                "\t-o --output \t\t output file, optional, will use standard output if nto specified\n" +
                "\t<method> \t\t method signature from list_method.sh, at least one is required\n" +
                "\n\n");
    }

    @Option
    @LongSwitch("packages")
    @ShortSwitch("p")
    @SingleArgument
    public void setPackages(String packages) {
        this.packages = new HashSet<>(Arrays.asList(packages.split(":")));
    }

    @Option
    @LooseArguments
    @Multiple
    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public String[] getPackages() {
        return packages == null ? getPackagesFromMethods() : packages.toArray(new String[packages.size()]);
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
}
