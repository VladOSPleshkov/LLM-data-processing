package review.util;

public final class ReviewTextProvider {

    private ReviewTextProvider() {}

    public static String repositoryText() {
        return "Отзывы и векторные эмбеддинги загружены из review_db";
    }

    public static String serviceText(String data) {
        return "ReviewService обработал: [" + data + "]";
    }

    public static String controllerText(String data) {
        return "ReviewController: " + data;
    }
}