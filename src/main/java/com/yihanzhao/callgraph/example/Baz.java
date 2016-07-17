package com.yihanzhao.callgraph.example;

public class Baz {
    public void show(Bar bar, Foo foo) {
        int a = bar.getBar() + foo.getFoo(1);
        System.out.println(a);
    }
}
