package analysis.util;

public final class AnalysisTextProvider {

    private AnalysisTextProvider() {}

    public static String repositoryText() {
        return "Задачи и статистика токенов загружены из analysis_db";
    }

    public static String serviceText(String own, String external) {
        return "AnalysisService обработал: [" + own + "]"
                + " и получил из соседнего модуля: [" + external + "]";
    }

    public static String controllerText(String data) {
        return "AnalysisController: " + data;
    }
}