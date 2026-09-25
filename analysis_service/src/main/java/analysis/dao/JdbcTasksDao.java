package analysis.dao;

import analysis.entity.Tasks;
import com.shared.dao.AbstractJdbcDao;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Repository;
import com.shared.tx.ConnectionHolder;

import java.sql.Connection;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository("tasksDao")
public class JdbcTasksDao extends AbstractJdbcDao implements ITasksDao {

    private static final String DB_KEY = "ANALYSIS_DB";
    private final TasksDbMapper tasksDbMapper;

    public JdbcTasksDao(ConnectionHolder connectionHolder, TasksDbMapper tasksDbMapper) {
        super(connectionHolder, DB_KEY);
        this.tasksDbMapper = tasksDbMapper;
    }
    private MapSqlParameterSource getParamSource(Tasks task) {
        return new MapSqlParameterSource()
                .addValue("id", task.getId(), Types.OTHER)
                .addValue("brandId", task.getBrandId())
                .addValue("brandName", task.getBrandName())
                .addValue("query", task.getQuery())
                .addValue("status", task.getStatus())
                .addValue("result", task.getResult())
                .addValue("createdAt", task.getCreatedAt())
                .addValue("completedAt", task.getCompletedAt())
                .addValue("errorMessage", task.getErrorMessage())
                .addValue("updatedAt", task.getUpdatedAt())
                .addValue("topK", task.getTopK())
                .addValue("tokenUsed", task.getTokenUsed());
    }

    @Override
    public Tasks save(Tasks task) {
        UUID id = task.getId() != null ? task.getId() : UUID.randomUUID();
        Tasks toPersist = task.toBuilder().id(id).build();
        String sql = """
                INSERT INTO tasks (brand_id, brand_name, query, status, result, created_at, completed_at, error_message, updated_at, top_k, token_used)
                VALUES (:brandId, :brandName, :query, :status, :result, :createdAt, :completedAt, :errorMessage, :updatedAt, :topK, :tokenUsed)
                """;
        execute(true, template -> template.update(sql, getParamSource(toPersist)));
        return toPersist;
    }
        @Override
        public Optional<Tasks> findById(UUID id) {
            String sql = "SELECT * FROM tasks WHERE id = :id";

            List<Tasks> results = execute(false, template ->
                    template.query(sql, Map.of("id", id.toString()), (rs, rowNum) -> tasksDbMapper.fromResultSet(rs)));
            return results.stream().findFirst();
        }

        @Override
        public List<Tasks> findAll() {
            String sql = "SELECT * FROM tasks";

            return execute(false, template ->
                    template.query(sql, (rs, rowNum) -> tasksDbMapper.fromResultSet(rs)));
        }

        @Override
        public Tasks update(Tasks task) {
            String sql = """
            UPDATE tasks 
            SET brand_id = :brandId, brand_name = :brandName, query = :query, status = :status, 
                result = :result, created_at = :createdAt, completed_at = :completedAt, 
                error_message = :errorMessage, updated_at = :updatedAt, top_k = :topK, token_used = :tokenUsed
            WHERE id = :id
            """;

            execute(true, template -> template.update(sql, getParamSource(task)));
            return task;
        }

        @Override
        public boolean deleteById(UUID id) {
            String sql = "DELETE FROM tasks WHERE id = :id";

            int rowsAffected = execute(true, template -> template.update(sql, Map.of("id", id.toString())));
            return rowsAffected > 0;
        }

    @Override
    public boolean existsById(UUID id) {
        return recordExistsById("tasks", id);
    }
         /* Создает легковесный JdbcTemplate вокруг одного конкретного Connection.
         * suppressClose = true запрещает шаблону закрывать коннект физически, оставляя управление холдеру.
         */
        private NamedParameterJdbcTemplate createTemplateForConnection(Connection conn) {
            return new NamedParameterJdbcTemplate(new SingleConnectionDataSource(conn, true));
        }
}
