package com.yihanzhao.callgraph.classutils;

import java.util.Arrays;

public class PackageBasedClassNameFilter implements ClassNameFilter {

    private String[] packages;

    public PackageBasedClassNameFilter(String[] packages) {
        this.packages = packages;
    }

    @Override
    public boolean acceptClass(String className) {
        return Arrays.stream(packages).anyMatch(className::startsWith);
    }
}
