# JavaCallGraph
This tool helps analyse Java method call hierarchies of a given method/methods. It will generate 
a dot graph illustrating the call graph, which can be further visualised by tools like 
[WebGraphviz](http://www.webgraphviz.com/)

# Usage
## List all methods of a given class
```
Usage: ./list_methods.sh -c classpath <className>
        -c --classpath           classpath of the classes under analysis
        <className>              full qualified className
```
Sample:
```
./list_methods.sh -c /home/yihan/.gradle/caches/modules-2/files-2.1/com.github.jankroken/commandline/1.7.0/c5edda82e1cae51c6af3efd54b52c496ace9f97c/commandline-1.7.0.jar com.github.jankroken.commandline.domain.OptionSet
```

## Print the call graph
```
Usage: ./draw_call_graph.sh -c classpath [-p <package1>:<package2>] [-o <output.dot>] <method1> [<method2>]
        -c --classpath           classpath of the classes under analysis
        -p --packages            packages to be included, optional, will derive from methods is not specified
        -o --output              output file, optional, will use standard output if nto specified
        <method>                 method signature from list_method.sh, at least one is required
```

Sample
```
./draw_call_graph.sh -c /home/yihan/.gradle/caches/modules-2/files-2.1/com.github.jankroken/commandline/1.7.0/c5edda82e1cae51c6af3efd54b52c496ace9f97c/commandline-1.7.0.jar -p com.github.jankroken.commandline 'com.github.jankroken.commandline.domain.OptionSet:consumeOptions(Lcom/github/jankroken/commandline/domain/Tokenizer;)'
```

# Follow the default implementation of an interface
If an interface only has one implementation in the classpath, then calls to the methods on this interface will be seen
calls directly to the methods on its implementation. For interfaces with more than two implementations, it is hard to 
decide which implementation to follow, so this auto matching delegation cannot be done.
