package com.yihanzhao.callgraph;

import com.github.jankroken.commandline.annotations.LongSwitch;
import com.github.jankroken.commandline.annotations.Option;
import com.github.jankroken.commandline.annotations.ShortSwitch;
import com.github.jankroken.commandline.annotations.SingleArgument;
import com.github.jankroken.commandline.annotations.Toggle;

public abstract class BaseOptions {

    private String classPath;
    private boolean help;

    @Option
    @LongSwitch("help")
    @ShortSwitch("h")
    @Toggle(true)
    public void setHelp(boolean help) {
        this.help = help;
    }

    @Option
    @LongSwitch("classpath")
    @ShortSwitch("c")
    @SingleArgument
    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getClassPath() {
        if (!isHelp() && (classPath == null || classPath.isEmpty())) {
            throw new IllegalArgumentException("Classpath need to be specified");
        }
        return classPath;
    }

    public boolean isHelp() {
        return help;
    }

    abstract protected void usage();

}
