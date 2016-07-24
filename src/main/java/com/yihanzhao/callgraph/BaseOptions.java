package com.yihanzhao.callgraph;

import com.github.jankroken.commandline.annotations.LongSwitch;
import com.github.jankroken.commandline.annotations.Option;
import com.github.jankroken.commandline.annotations.ShortSwitch;
import com.github.jankroken.commandline.annotations.SingleArgument;

public abstract class BaseOptions {

    private String classPath;

    @Option
    @LongSwitch("classpath")
    @ShortSwitch("c")
    @SingleArgument
    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getClassPath() {
        if (classPath == null || classPath.isEmpty()) {
            throw new IllegalArgumentException("Classpath need to be specified");
        }
        return classPath;
    }

    abstract protected void usage();

}
