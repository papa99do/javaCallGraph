package com.yihanzhao.callgraph.example;

public class Foo {

    public int getFoo(int i) {
        return getBar(i + 5, 2);
    }

    private int getBar(long i, int j) {
        return (int)i + new Bar().getBar();
    }
}
