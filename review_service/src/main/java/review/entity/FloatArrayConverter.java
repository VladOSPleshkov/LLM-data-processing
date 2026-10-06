package review.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class FloatArrayConverter implements AttributeConverter<float[], String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(float[] attribute) {
        if (attribute == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(attribute.length * 8);
        sb.append("[");
        for (int i = 0; i < attribute.length; i++) {
            if (i > 0) {
                sb.append(DELIMITER);
            }
            sb.append(attribute[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public float[] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        String clean = dbData.replace("[", "").replace("]", "").trim();
        if (clean.isEmpty()) {
            return new float[0];
        }
        String[] parts = clean.split(DELIMITER);
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }
}
