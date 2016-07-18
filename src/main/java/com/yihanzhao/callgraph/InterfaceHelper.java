package com.yihanzhao.callgraph;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.bcel.classfile.JavaClass;

public class InterfaceHelper {

    private Map<String, Set<String>> classInterfaceMap = new HashMap<>();
    private Map<String, Set<String>> interfaceClassMap = new HashMap<>();

    public void addClass(JavaClass clazz) {
        Set<String> allInterfaces = Arrays.stream(clazz.getInterfaceNames()).collect(Collectors.toSet());
        classInterfaceMap.put(clazz.getClassName(), allInterfaces);

        for (String interfaceName : allInterfaces) {
            if (!interfaceClassMap.containsKey(interfaceName)) {
                interfaceClassMap.put(interfaceName, new HashSet<>());
            }
            interfaceClassMap.get(interfaceName).add(clazz.getClassName());
        }
    }

    public Set<String> getInterfacesWithThisOnlyChild(String className) {
        if (!classInterfaceMap.containsKey(className)) {
            return Collections.emptySet();
        }
        return classInterfaceMap.get(className).stream()
                .filter(interfaceName -> interfaceClassMap.get(interfaceName).size() == 1)
                .collect(Collectors.toSet());
    }
}
