package analysis.dao;

import analysis.entity.Status;
import analysis.entity.Tasks;
import analysis.entity.TokenUsage;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

@Component
public class TasksDbMapper {

    public Tasks fromResultSet(ResultSet rs) throws SQLException {
        Timestamp completedAt = rs.getTimestamp("completed_at");
        return Tasks.builder()
                .id(rs.getObject("id", UUID.class))
                .brandId(rs.getObject("brand_id", UUID.class))
                .brandName(rs.getString("brand_name"))
                .query(rs.getString("query"))
                .status(Status.valueOf(rs.getString("status")))
                .result(rs.getString("result"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .completedAt(completedAt != null ? completedAt.toLocalDateTime() : null)
                .errorMessage(rs.getString("error_message"))
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .topK(rs.getInt("top_k"))
                .tokenUsed(rs.getInt("token_used"))
                .build();
    }

    public TokenUsage tokenUsageFromResultSet(ResultSet rs) throws SQLException {
        return TokenUsage.builder()
                .id(rs.getObject("id", UUID.class))
                .tasksId(UUID.fromString(rs.getString("tasks_id")))
                .promptTokens(rs.getLong("prompt_tokens"))
                .responseTokens(rs.getLong("response_tokens"))
                .modelName(rs.getString("model_name"))
                .recorderedAt(rs.getTimestamp("recordered_at").toLocalDateTime())
                .build();
    }
}