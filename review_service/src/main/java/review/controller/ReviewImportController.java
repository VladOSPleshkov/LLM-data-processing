package review.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.shared.dto.review.ReviewImportRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import review.service.ReviewImportService;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/reviews/import")
@RequiredArgsConstructor
public class ReviewImportController {

    private final ReviewImportService reviewImportService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> importReviews(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Файл пуст"));
        }

        List<ReviewImportRow> rows = new ArrayList<>();
        String filename = file.getOriginalFilename();

        try {
            if (filename != null && filename.endsWith(".json")) {
                // 1. Парсинг JSON-файла
                rows = objectMapper.readValue(file.getInputStream(), new TypeReference<List<ReviewImportRow>>() {});
            } else if (filename != null && filename.endsWith(".csv")) {
                // 2. Парсинг CSV-файла (Ожидаемый порядок колонок: brand, categories(через запятую), content, rating, author, source)
                try (Reader reader = new InputStreamReader(file.getInputStream());
                     CSVReader csvReader = new CSVReader(reader)) {

                    List<String[]> allRows = csvReader.readAll();
                    // Пропускаем заголовок (первую строчку)
                    for (int i = 1; i < allRows.size(); i++) {
                        String[] data = allRows.get(i);
                        List<String> categories = Arrays.stream(data[1].split(","))
                                .map(String::trim)
                                .toList();

                        rows.add(new ReviewImportRow(
                                data[0], // brand_name
                                categories, // category_names
                                data[2], // content
                                Integer.parseInt(data[3]), // rating
                                data[4], // author_name
                                data[5]  // source
                        ));
                    }
                }
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Неподдерживаемый формат файла. Разрешены только .json и .csv"));
            }

            // 3. Передаем распарсенные строки в сервис для сохранения и отправки в Kafka
            int processedCount = reviewImportService.bulkProcess(rows);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Файл успешно принят в обработку",
                    "imported_count", processedCount
            ));

        } catch (Exception e) {
            log.error("Ошибка при импорте файла отзыва", e);
            return ResponseEntity.status(500).body(Map.of("error", "Сбой парсинга файла: " + e.getMessage()));
        }
    }
}