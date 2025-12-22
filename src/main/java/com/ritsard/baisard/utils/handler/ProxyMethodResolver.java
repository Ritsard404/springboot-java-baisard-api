/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.ritsard.baisard.utils.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxyMethodResolver {
    private static final Logger log = LoggerFactory.getLogger(ProxyMethodResolver.class);

    public static List<String> resolveActualMethodLocations(Throwable ex) {
        ArrayList<String> results = new ArrayList<String>();
        try {
            if (ex == null || ex.getStackTrace() == null) {
                return results;
            }
            block6: for (StackTraceElement stackFrame : ex.getStackTrace()) {
                String className = stackFrame.getClassName();
                String methodName = stackFrame.getMethodName();
                if (className.contains("$$SpringCGLIB$$")) {
                    String originalClassName = className.substring(0, className.indexOf("$$"));
                    try {
                        Method[] methods;
                        Class<?> originalClass = Class.forName(originalClassName);
                        for (Method method : methods = originalClass.getDeclaredMethods()) {
                            if (!method.getName().equals(methodName)) continue;
                            String sourceFile = originalClass.getSimpleName() + ".java";
                            try {
                                Field sourceField = method.getClass().getDeclaredField("slot");
                                sourceField.setAccessible(true);
                                int slot = (Integer)sourceField.get(method);
                                String actualLocation = String.format("%s.%s(%s:%d)", originalClassName, methodName, sourceFile, method.toString().contains("line=") ? Integer.parseInt(method.toString().replaceAll(".*line=([0-9]+).*", "$1")) : -1);
                                results.add(actualLocation);
                            }
                            catch (Exception e) {
                                String actualLocation = String.format("%s.%s(%s:?)", originalClassName, methodName, sourceFile);
                                results.add(actualLocation);
                            }
                            continue block6;
                        }
                        continue;
                    }
                    catch (ClassNotFoundException e) {
                        log.debug("\uc6d0\ubcf8 \ud074\ub798\uc2a4\ub97c \ucc3e\uc744 \uc218 \uc5c6\uc74c: {}", (Object)originalClassName);
                        continue;
                    }
                }
                if (!className.startsWith("com.lodong") || className.contains("$$Lambda$") || stackFrame.getLineNumber() <= 0) continue;
                String actualLocation = String.format("%s.%s(%s:%d)", className, methodName, stackFrame.getFileName(), stackFrame.getLineNumber());
                results.add(actualLocation);
            }
        }
        catch (Exception e) {
            log.debug("\ud504\ub85d\uc2dc \uba54\uc18c\ub4dc \uc704\uce58 \ud574\uc11d \uc911 \uc624\ub958: {}", (Object)e.getMessage());
        }
        return results;
    }

    public static StackTraceElement findOriginalMethod(Throwable ex, String serviceClassName) {
        for (StackTraceElement element : ex.getStackTrace()) {
            String className = element.getClassName();
            if (!className.contains(serviceClassName) || !className.contains("$$SpringCGLIB$$")) continue;
            String originalClassName = className.substring(0, className.indexOf("$$"));
            try {
                Class<?> originalClass = Class.forName(originalClassName);
                for (Method method : originalClass.getDeclaredMethods()) {
                    if (!method.getName().equals(element.getMethodName())) continue;
                    return new StackTraceElement(originalClassName, method.getName(), (String)(element.getFileName() != null ? element.getFileName() : originalClass.getSimpleName() + ".java"), method.toString().contains("line=") ? Integer.parseInt(method.toString().replaceAll(".*line=([0-9]+).*", "$1")) : -1);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    public static Map<String, Integer> extractMethodLineNumbers(String className) {
        HashMap<String, Integer> methodLineNumbers = new HashMap<String, Integer>();
        try {
            Method[] methods;
            Class<?> clazz = Class.forName(className);
            for (Method method : methods = clazz.getDeclaredMethods()) {
                String methodInfo = method.toString();
                if (!methodInfo.contains("line=")) continue;
                int lineNumber = Integer.parseInt(methodInfo.replaceAll(".*line=([0-9]+).*", "$1"));
                methodLineNumbers.put(method.getName(), lineNumber);
            }
        }
        catch (Exception e) {
            log.debug("\uba54\uc18c\ub4dc \ub77c\uc778 \ucd94\ucd9c \uc911 \uc624\ub958: {}", (Object)e.getMessage());
        }
        return methodLineNumbers;
    }

    public static String guessMethodImplementation(String serviceClass, String methodName) {
        if (serviceClass == null || methodName == null) {
            return "\uc54c \uc218 \uc5c6\ub294 \uad6c\ud604";
        }
        if (methodName.equals("saveNewsAndTranslate")) {
            return "\ub274\uc2a4 \uc800\uc7a5 \ubc0f \ubc88\uc5ed \ucc98\ub9ac \uc911 \ub370\uc774\ud130\ubca0\uc774\uc2a4 \uc624\ub958 \ubc1c\uc0dd - NewsService.saveNewsAndTranslate() \uba54\uc18c\ub4dc \ud655\uc778 \ud544\uc694";
        }
        if (methodName.startsWith("save")) {
            return "\ub370\uc774\ud130 \uc800\uc7a5 \uc791\uc5c5 \uc911 \uc624\ub958 \ubc1c\uc0dd - \uc800\uc7a5 \ub300\uc0c1 \uac1d\uccb4\uc758 \ud544\ub4dc \uae38\uc774 \ud655\uc778 \ud544\uc694";
        }
        if (methodName.startsWith("update")) {
            return "\ub370\uc774\ud130 \uc5c5\ub370\uc774\ud2b8 \uc791\uc5c5 \uc911 \uc624\ub958 \ubc1c\uc0dd - \uc5c5\ub370\uc774\ud2b8 \ub300\uc0c1 \ud544\ub4dc\uc758 \uae38\uc774 \uc81c\ud55c \ud655\uc778 \ud544\uc694";
        }
        if (methodName.startsWith("translate")) {
            return "\ubc88\uc5ed \ub370\uc774\ud130 \ucc98\ub9ac \uc911 \uc624\ub958 \ubc1c\uc0dd - \ubc88\uc5ed \uacb0\uacfc \uae38\uc774 \ud655\uc778 \ud544\uc694";
        }
        return serviceClass + "." + methodName + "() \uba54\uc18c\ub4dc \uad6c\ud604 \ud655\uc778 \ud544\uc694";
    }
}

