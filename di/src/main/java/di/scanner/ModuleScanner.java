package di.scanner;

import di.annotation.Component;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleScanner {

    public List<Class<?>> scan(String basePackage, ClassLoader classLoader) {
        List<Class<?>> result = new ArrayList<>();
        String path = basePackage.replace('.', '/');

        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                if ("jar".equals(url.getProtocol())) {
                    scanJar(url, path, classLoader, result);
                } else {
                    scanDirectory(new File(url.toURI()), basePackage,
                            classLoader, result);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сканирования пакета "
                    + basePackage + ": " + e.getMessage(), e);
        }

        return result;
    }

    // сканирование модуля упакованного в jar-архив
    private void scanJar(URL url, String path, ClassLoader classLoader,
                         List<Class<?>> result) throws Exception {

        String fullPath = url.getPath();
        String jarPath = fullPath.substring(
                fullPath.indexOf("file:") + 5,
                fullPath.indexOf("!"));
        jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String entryName = entries.nextElement().getName();
                if (entryName.startsWith(path) && entryName.endsWith(".class")) {
                    String className = entryName
                            .replace('/', '.')
                            .substring(0, entryName.length() - 6);
                    addIfComponent(className, classLoader, result);
                }
            }
        }
    }

    private void scanDirectory(File dir, String packageName,
                               ClassLoader classLoader,
                               List<Class<?>> result) {

        if (!dir.exists()) return;

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(),
                        classLoader, result);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "."
                        + file.getName().replace(".class", "");
                addIfComponent(className, classLoader, result);
            }
        }
    }

    private void addIfComponent(String className, ClassLoader classLoader,
                                List<Class<?>> result) {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            if (clazz.isAnnotationPresent(Component.class)) {
                result.add(clazz);
            }
        } catch (Throwable ignored) {
            // класс может быть недоступен из-за отсутствия транзитивных
            // зависимостей — такие классы пропускаем без остановки сканирования
        }
    }
}