package di.processor;

import di.annotation.Autowire;
import di.annotation.Component;
import di.annotation.Qualifier;
import di.annotation.Value;
import di.container.IoCContainer;
import di.scanner.ModuleScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnnotationProcessor {

    private static final IoCContainer CONTAINER = new IoCContainer();
    private static final Map<String, String> PROPERTIES = new HashMap<>();
    private static final ModuleScanner SCANNER = new ModuleScanner();
    private static final Set<Class<?>> DISCOVERED = new LinkedHashSet<>();
    private static boolean initialized = false;

    static {
        loadAllPropertyFiles(AnnotationProcessor.class.getClassLoader());
    }

    public static IoCContainer initialize(ClassLoader classLoader,
                                          List<String> basePackages) {
        if (initialized) return CONTAINER;

        if (classLoader != AnnotationProcessor.class.getClassLoader()) {
            loadAllPropertyFiles(classLoader);
        }

        for (String basePackage : basePackages) {
            DISCOVERED.addAll(SCANNER.scan(basePackage, classLoader));
        }

        for (Class<?> clazz : DISCOVERED) {
            resolve(clazz, new HashSet<>());
        }

        initialized = true;
        return CONTAINER;
    }

    private static Object resolve(Class<?> clazz, Set<Class<?>> inProgress) {
        if (CONTAINER.contains(clazz)) {
            return CONTAINER.getBean(clazz);
        }

        Class<?> target = clazz.isInterface()
                ? findImplementation(clazz)
                : clazz;

        if (CONTAINER.contains(target)) {
            return CONTAINER.getBean(target);
        }

        if (inProgress.contains(target)) {
            throw new RuntimeException(
                    "Циклическая зависимость: " + target.getName());
        }
        inProgress.add(target);

        try {
            Object instance = instantiate(target, inProgress);
            CONTAINER.register(resolveBeanName(target), target, instance);

            injectValues(target, instance);
            injectFields(target, instance, inProgress);
            injectSetters(target, instance, inProgress);

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания бина "
                    + target.getName() + ": " + e.getMessage(), e);
        } finally {
            inProgress.remove(target);
        }
    }

    private static Object instantiate(Class<?> clazz,
                                      Set<Class<?>> inProgress)
            throws Exception {

        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Autowire.class)) {
                Parameter[] parameters = constructor.getParameters();
                Object[] args = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    args[i] = resolveParameter(parameters[i], inProgress);
                }
                constructor.setAccessible(true);
                return constructor.newInstance(args);
            }
        }

        Constructor<?> def = clazz.getDeclaredConstructor();
        def.setAccessible(true);
        return def.newInstance();
    }

    private static void injectFields(Class<?> clazz, Object instance,
                                     Set<Class<?>> inProgress)
            throws Exception {

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Autowire.class)) continue;

            Object dependency = field.isAnnotationPresent(Qualifier.class)
                    ? resolveByQualifier(
                    field.getAnnotation(Qualifier.class).value(),
                    inProgress)
                    : resolve(field.getType(), inProgress);

            field.setAccessible(true);
            field.set(instance, dependency);
        }
    }

    private static void injectSetters(Class<?> clazz, Object instance,
                                      Set<Class<?>> inProgress)
            throws Exception {

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Autowire.class)) continue;
            if (method.getParameterCount() != 1) continue;

            Parameter parameter = method.getParameters()[0];
            Object dependency = resolveParameter(parameter, inProgress);

            method.setAccessible(true);
            method.invoke(instance, dependency);
        }
    }

    private static Object resolveParameter(Parameter parameter,
                                           Set<Class<?>> inProgress) {
        if (parameter.isAnnotationPresent(Qualifier.class)) {
            return resolveByQualifier(
                    parameter.getAnnotation(Qualifier.class).value(),
                    inProgress);
        }
        return resolve(parameter.getType(), inProgress);
    }

    private static Object resolveByQualifier(String beanName,
                                             Set<Class<?>> inProgress) {
        if (CONTAINER.containsName(beanName)) {
            return CONTAINER.getBean(beanName, Object.class);
        }

        for (Class<?> candidate : DISCOVERED) {
            if (resolveBeanName(candidate).equals(beanName)) {
                return resolve(candidate, inProgress);
            }
        }

        throw new RuntimeException(
                "Бин с квалификатором не найден: " + beanName);
    }

    private static void injectValues(Class<?> clazz, Object instance)
            throws Exception {

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Value.class)) continue;

            Value annotation = field.getAnnotation(Value.class);
            String raw = PROPERTIES.getOrDefault(
                    annotation.key(), annotation.defaultValue());

            field.setAccessible(true);
            field.set(instance, convert(raw, field.getType()));
        }
    }

    private static Object convert(String raw, Class<?> targetType) {
        if (raw == null || raw.isEmpty()) {
            if (targetType == int.class)     return 0;
            if (targetType == Integer.class) return null;
            if (targetType == long.class)    return 0L;
            if (targetType == Long.class)    return null;
            if (targetType == boolean.class) return false;
            if (targetType == Boolean.class) return null;
            return "";
        }
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(raw);
        }
        if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(raw);
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(raw);
        }
        return raw;
    }

    private static Class<?> findImplementation(Class<?> iface) {
        List<Class<?>> candidates = new ArrayList<>();
        for (Class<?> candidate : DISCOVERED) {
            if (iface.isAssignableFrom(candidate)
                    && !candidate.isInterface()) {
                candidates.add(candidate);
            }
        }
        if (candidates.isEmpty()) {
            throw new RuntimeException(
                    "Реализация не найдена: " + iface.getName());
        }
        if (candidates.size() > 1) {
            throw new RuntimeException(
                    "Несколько реализаций " + iface.getName()
                            + " — используйте @Qualifier: " + candidates);
        }
        return candidates.get(0);
    }

    private static String resolveBeanName(Class<?> clazz) {
        Component annotation = clazz.getAnnotation(Component.class);
        if (annotation != null && !annotation.name().isEmpty()) {
            return annotation.name();
        }
        return clazz.getName();
    }

    private static void loadAllPropertyFiles(ClassLoader classLoader) {
        try {
            Enumeration<URL> resources = classLoader.getResources(".env");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (InputStream stream = url.openStream()) {
                    parseProperties(stream);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки .env: " + e.getMessage());
        }
    }

    private static void parseProperties(InputStream stream)
            throws IOException {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    PROPERTIES.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
    }
}