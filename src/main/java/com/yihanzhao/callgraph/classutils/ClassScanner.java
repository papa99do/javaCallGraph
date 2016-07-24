package com.yihanzhao.callgraph.classutils;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.SyntheticRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import static java.util.stream.Collectors.toSet;

public class ClassScanner {
    private final Set<File> classPathEntries;
    private final SyntheticRepository repository;
    private final ClassNameFilter classNameFilter;

    public ClassScanner(final String classPath, final String... packages) {
        classPathEntries = getClassPathEntries(classPath);
        repository = SyntheticRepository.getInstance(new ClassPath(classPath));
        classNameFilter = packages.length == 0 ? ClassNameFilter.ALL : new PackageBasedClassNameFilter(packages);
    }

    private Set<File> getClassPathEntries(String classPath) {
        return Arrays.stream(classPath.split(File.pathSeparator)).map(File::new).collect(toSet());
    }

    public JavaClass parseClass(String className) throws ClassNotFoundException {
        return repository.loadClass(className);
    }

    public Stream<String> getAllClassNames() {
        return classPathEntries.stream().flatMap(this::getClassNamesFrom);
    }

    private Stream<String> getClassNamesFrom(File file) {
        if (!file.exists()) {
            return Stream.empty();
        }

        if (file.isDirectory()) {
            return getClassNamesFromDirectory(file, "");
        }

        if (file.isFile()) {
            return getClassNamesFromJar(file);
        }

        return Stream.empty();
    }

    private Stream<String> getClassNamesFromJar(File jarFile) {
        try (ZipFile zipFile = new ZipFile(jarFile)) {

            return enumerationAsStream(zipFile.entries())
                    .filter(entry -> !entry.isDirectory())
                    .filter(entry -> entry.getName().endsWith(".class"))
                    .map(entry -> {
                        String className = entry.getName();
                        className = className.substring(0, className.length() - 6).replace('/', '.');
                        return className;
                    })
                    .filter(classNameFilter::acceptClass);

        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Stream.empty();
    }

    private Stream<String> getClassNamesFromDirectory(File dir, String base) {
        return Arrays.stream(dir.listFiles()).flatMap(child -> {
            if (child.isDirectory()) {
                return getClassNamesFromDirectory(child, withBase(base, child.getName()));
            } else {
                return Stream.of(child).filter(file -> file.getName().endsWith(".class")).map(file -> {
                    String className = file.getName();
                    className = className.substring(0, className.length() - 6);
                    return withBase(base, className);
                });
            }
        });
    }

    private String withBase(String base, String name) {
        return (base.isEmpty() ? base : base + ".") + name;
    }


    public static <T> Stream<T> enumerationAsStream(Enumeration<T> e) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        new Iterator<T>() {
                            public T next() {
                                return e.nextElement();
                            }

                            public boolean hasNext() {
                                return e.hasMoreElements();
                            }
                        },
                        Spliterator.ORDERED), false);
    }

}
