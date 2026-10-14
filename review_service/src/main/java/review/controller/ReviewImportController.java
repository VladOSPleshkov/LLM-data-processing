package review.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
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
import java.nio.charset.StandardCharsets;
import java.util.*;

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
                rows = objectMapper.readValue(file.getInputStream(), new TypeReference<List<ReviewImportRow>>() {});
            } else if (filename != null && filename.endsWith(".csv")) {
                try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                     CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

                    String[] data;
                    while ((data = csvReader.readNext()) != null) {
                        if (data.length < 6 || data[0].isBlank()) {
                            continue; // Пропускаем некорректные или пустые строки
                        }

                        List<String> categories = (data[1] == null || data[1].isBlank())
                                ? List.of()
                                : Arrays.stream(data[1].split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .toList();

                        int rating = 5;
                        try {
                            rating = (int) Math.round(Double.parseDouble(data[3].trim()));
                        } catch (Exception ignored) {}

                        rows.add(new ReviewImportRow(
                                data[0].trim(),
                                categories,
                                data[2].trim(),
                                rating,
                                data[4].trim(),
                                data[5].trim()
                        ));
                    }
                }
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Разрешены только файлы .json и .csv"));
            }

            int processedCount = reviewImportService.bulkProcess(rows);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "imported_count", processedCount
            ));

        } catch (Exception e) {
            log.error("Ошибка при импорте файла отзывов", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Сбой обработки файла: " + e.getMessage()));
        }
    }
}