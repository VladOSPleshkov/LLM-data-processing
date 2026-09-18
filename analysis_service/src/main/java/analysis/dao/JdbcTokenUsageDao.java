package analysis.dao;

import analysis.entity.Tasks;
import analysis.entity.TokenUsage;
import analysis.service.TasksMapper;
import com.shared.dao.AbstractJdbcDao;
import com.shared.tx.ConnectionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository("tokenUsageDao")
public class JdbcTokenUsageDao extends AbstractJdbcDao implements ITokenUsageDao {

    private static final String DB_KEY = "ANALYSIS_DB"; // бд где лежат токены
    private final ConnectionHolder connectionHolder;
    private final TasksDbMapper tasksDbMapper;

    public JdbcTokenUsageDao(ConnectionHolder connectionHolder, TasksDbMapper tasksDbMapper) {
        super(connectionHolder,  DB_KEY);
        this.connectionHolder = connectionHolder;
        this.tasksDbMapper = tasksDbMapper;
    }

    @Override
    public TokenUsage save(TokenUsage usage) {
        UUID id = usage.getId() != null ? usage.getId() : UUID.randomUUID();
        TokenUsage toPersist = usage.toBuilder().id(id).build();
        String sql = """
                INSERT INTO token_usage (task_id, token_count, model_name, created_at)
                VALUES (:tasksId, :tokensCount, :modelName, :createdAt)
                """;

        execute(true, template -> template.update(sql, getParamSource(toPersist)));
        return toPersist;
    }

    @Override
    public Optional<TokenUsage> findById(UUID id) {
        String sql = "SELECT * FROM token_usage WHERE id = :id";
        List<TokenUsage> results = execute(false, template ->
                template.query(sql, Map.of("id", id), (rs, rowNum) -> tasksDbMapper.tokenUsageFromResultSet(rs)));
        return results.stream().findFirst();
    }

    @Override
    public List<TokenUsage> findByTasksId(UUID tasksId) {
        String sql = "SELECT * FROM token_usage WHERE tasks_id = :tasksId";

        return execute(false, template ->
                template.query(sql, Map.of("tasksId", tasksId.toString()), (rs, rowNum) ->
                        tasksDbMapper.tokenUsageFromResultSet(rs)));
    }

    @Override
    public List<TokenUsage> findAll() {
        String sql = "SELECT * FROM token_usage";
        try (Connection conn = connectionHolder.getConnection(DB_KEY, false)) {
            NamedParameterJdbcTemplate jdbcTemplate = createTemplateForConnection(conn);

            List<TokenUsage> results = jdbcTemplate.query(sql,
                    (rs, rowNum) -> tasksDbMapper.tokenUsageFromResultSet(rs));

            connectionHolder.releaseConnection(DB_KEY, conn, true);
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения всех TokenUsage", e);
        }
    }

    @Override
    public TokenUsage update(TokenUsage usage) {
        String sql = """
                UPDATE token_usage 
                SET task_id = :taskId, token_count = :tokenCount, model_name = :modelName, created_at = :createdAt
                WHERE id = :id
                """;
        execute(true, template -> template.update(sql, getParamSource(usage)));
        return usage;
    }

    @Override
    public boolean deleteById(UUID id) {
        String sql = "DELETE FROM token_usage WHERE id = :id";
        int rowsAffected = execute(true, template ->
                template.update(sql, Map.of("id", id.toString())));
        return rowsAffected > 0;
    }

    private NamedParameterJdbcTemplate createTemplateForConnection(Connection conn) {
        return new NamedParameterJdbcTemplate(new SingleConnectionDataSource(conn, true));
    }
    @Override
    public boolean existsById(UUID id) {
        return recordExistsById("token_usage", id);
    }

    private MapSqlParameterSource getParamSource(TokenUsage usage) {
        return new MapSqlParameterSource()
                .addValue("id", usage.getId())
                .addValue("taskId", usage.getTasksId())
                .addValue("ResponseTokens", usage.getResponseTokens())
                .addValue("modelName", usage.getModelName())
                .addValue("recorderedAt", usage.getRecorderedAt());
    }
}
