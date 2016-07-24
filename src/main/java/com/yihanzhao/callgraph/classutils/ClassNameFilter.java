package com.yihanzhao.callgraph.classutils;

public interface ClassNameFilter {
    ClassNameFilter ALL = className -> true;

    boolean acceptClass(String className);
}
