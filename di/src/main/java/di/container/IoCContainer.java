package di.container;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IoCContainer {

    private final Map<String, Object> beansByName = new LinkedHashMap<>();
    private final Map<Class<?>, Object> beansByType = new LinkedHashMap<>();

    public void register(String name, Class<?> type, Object instance) {
        beansByName.put(name, instance);
        beansByType.put(type, instance);

        // регистрируем бин также по всем реализованным интерфейсам,
        // чтобы можно было запрашивать зависимость по контракту
        for (Class<?> iface : type.getInterfaces()) {
            beansByType.putIfAbsent(iface, instance);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        Object bean = beansByType.get(type);
        if (bean == null) {
            throw new RuntimeException("Бин не найден: " + type.getName());
        }
        return (T) bean;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String name, Class<T> type) {
        Object bean = beansByName.get(name);
        if (bean == null) {
            throw new RuntimeException("Бин не найден по имени: " + name);
        }
        return (T) bean;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getBeansOfType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Object bean : beansByName.values()) {
            if (type.isAssignableFrom(bean.getClass())) {
                result.add((T) bean);
            }
        }
        return result;
        // все бины реализующие указанный интерфейс
    }

    public boolean contains(Class<?> type) {
        return beansByType.containsKey(type);
    }

    public boolean containsName(String name) {
        return beansByName.containsKey(name);
    }

    public Map<String, Object> getAllBeans() {
        return beansByName;
    }
}