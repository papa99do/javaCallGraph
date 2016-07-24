package com.yihanzhao.callgraph;

import com.github.jankroken.commandline.annotations.LooseArguments;
import com.github.jankroken.commandline.annotations.Option;
import com.github.jankroken.commandline.annotations.Required;

public class ListMethodsOptions extends BaseOptions {

    private String className;

    @Option
    @LooseArguments
    @Required
    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    protected void usage() {
        System.out.println("\n" +
                "Usage: ./list_methods.sh -cp classpath <className>\n" +
                "\t-c --classpath \t\t classpath of the classes under analysis\n" +
                "\t<className> \t\t full qualified className\n" +
                "\n\n");
    }
}
