package com.yihanzhao.callgraph;

import com.github.jankroken.commandline.annotations.LooseArguments;
import com.github.jankroken.commandline.annotations.Option;

public class ListMethodsOptions extends BaseOptions {

    private String className;

    @Option
    @LooseArguments
    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        if (className == null) {
            throw new IllegalArgumentException("Full qualified class name must be provided!");
        }
        return className;
    }

    protected void usage() {
        System.out.println("\n" +
                "Usage: ./list_methods.sh -c classpath <className>\n" +
                "\t-c --classpath \t\t classpath of the classes under analysis\n" +
                "\t<className> \t\t full qualified className\n" +
                "\n\n");
    }
}
