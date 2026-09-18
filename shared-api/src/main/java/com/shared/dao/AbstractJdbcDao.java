package com.shared.dao;

import com.shared.tx.ConnectionHolder;
import com.shared.tx.TransactionContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;


public class AbstractJdbcDao {
    protected final ConnectionHolder connectionHolder;
    protected final String dbKey;

    protected AbstractJdbcDao(ConnectionHolder connectionHolder, String dbKey) {
        this.connectionHolder = connectionHolder;
        this.dbKey = dbKey;
    }
    @FunctionalInterface
    protected interface JdbcAction<T> {
        T apply(NamedParameterJdbcTemplate template) throws SQLException;
    }

    protected <T> T execute(boolean wantTransactional, JdbcAction<T> action) {
        boolean partOfExternalTransaction = wantTransactional && TransactionContext.isActive(dbKey);
        Connection connection = null;
        boolean failed = false;
        try {
            connection = connectionHolder.getConnection(dbKey, wantTransactional);
            NamedParameterJdbcTemplate template = templateFor(connection);
            return action.apply(template);
        } catch (SQLException e) {
            failed = true;
            throw new RuntimeException("ошибка jdbc операции в dao" + dbKey +e);
        } catch (RuntimeException e) {
            failed = true;
            throw new RuntimeException(e);
        } finally {
            if (connection != null && !partOfExternalTransaction) {
                try {
                    connectionHolder.releaseConnection(dbKey, connection, failed);
                } catch (SQLException e) {
                    throw new RuntimeException("не удалось освободить коннект" + dbKey +e);
                }
            }
        }
    }
    protected boolean recordExistsById(String tableName, UUID id) {
        String sql = "select 1 from " + tableName + " where id= :id";
        return execute(false, template -> {
            var rows =template.query(sql, Map.of("id", id.toString()), (rs, rowNum) -> 1);
            return !rows.isEmpty();
                }
                );
    }
    private NamedParameterJdbcTemplate templateFor(Connection conn) {
        return new NamedParameterJdbcTemplate(new SingleConnectionDataSource(conn, true));
    }

}
