import contract.Controller;
import di.container.IoCContainer;
import di.processor.AnnotationProcessor;

import java.util.List;

public class Main {

    // корневые пакеты подключённых микросервисных модулей
    private static final List<String> MODULE_PACKAGES = List.of("auth", "review", "analysis"
    );

    public static void main(String[] args) {
        ClassLoader classLoader = Main.class.getClassLoader();

        System.out.println("=== Инициализация DI контейнера ===");
        IoCContainer container =
                AnnotationProcessor.initialize(classLoader, MODULE_PACKAGES);

        System.out.println("\n=== Контейнер сформирован ===");
        container.getAllBeans().keySet()
                .forEach(name -> System.out.println("  " + name));

        System.out.println("\n=== Вызов контроллеров модулей ===");

        // запрашиваем у контейнера все контроллеры
        List<Controller> controllers = container.getBeansOfType(Controller.class);

        for (Controller controller : controllers) {
            System.out.println("\n[" + controller.getModuleName() + "]");
            System.out.println(controller.execute());
        }
    }
}