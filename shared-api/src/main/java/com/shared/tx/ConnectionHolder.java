package com.shared.tx;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConnectionHolder {
    @Qualifier("authDataSource")
    private final DataSource authDataSource;
    @Qualifier("reviewDataSource")
    private final DataSource reviewDataSource;
    @Qualifier("analysisDataSource")
    private final DataSource analysisDataSource;

    private final Map<String, List<ConnectionState>> storage = new ConcurrentHashMap<>();
    {
        storage.put("AUTH_DB", new ArrayList<>());
        storage.put("REVIEW_DB", new ArrayList<>());
        storage.put("ANALYSIS_DB", new ArrayList<>());
    }

    @Synchronized
    public Connection getConnection(String dbKey, boolean wantTransactional) throws SQLException {
        long currentThreadId = Thread.currentThread().getId();
        List<ConnectionState> pool = storage.get(dbKey);
        if (pool == null) {
            throw new IllegalArgumentException("Неверный аргумент к бд: " + dbKey);
        }

        pool.removeIf(state -> !state.isTransactional() && !state.isValid());

        if (wantTransactional) {
            for (ConnectionState state : pool) {
                if (state.getOwnerThreadId() == currentThreadId && state.isTransactional()) {
                    if (!state.isValid()) {
                        throw new IllegalStateException("Транзакционный коннект закрыт");
                    }
                    return state.getConnection();
                }
            }
        }

        if (!wantTransactional) {
            for (ConnectionState state : pool) {
                if (state.getOwnerThreadId() == -1 && !state.isTransactional() && state.isValid()) {
                    state.setOwnerThreadId(currentThreadId);
                    return state.getConnection();
                }
            }
        }

        DataSource ds = switch (dbKey) {
            case "AUTH_DB" -> authDataSource;
            case "REVIEW_DB" -> reviewDataSource;
            case "ANALYSIS_DB" -> analysisDataSource;
            default -> throw new IllegalArgumentException("Неизвестная БД: " + dbKey);
        };

        Connection newConn = ds.getConnection();
        newConn.setAutoCommit(!wantTransactional);
        ConnectionState newState = new ConnectionState(newConn, wantTransactional, dbKey, currentThreadId);
        pool.add(newState);
        return newConn;
    }

    @Synchronized
    public void releaseConnection(String dbKey, Connection connection, boolean isRollback) throws SQLException {
        List<ConnectionState> pool = storage.get(dbKey);
        if (pool == null) return;

        for (ConnectionState state : pool) {
            if (state.getConnection() == connection) {
                if (state.isTransactional()) {
                    if (state.isValid()) {
                        if (isRollback) {
                            connection.rollback();
                        } else {
                            connection.commit();
                        }
                        connection.close();
                    }
                    pool.remove(state);
                } else {
                    state.setOwnerThreadId(-1); // Возвращаем в пул свободных
                }
                break;
            }
        }
    }

    @Synchronized
    public void closeAllPools() {
        storage.forEach((dbKey, pool) -> {
            for (ConnectionState state : pool) {
                try {
                    if (state.getConnection() != null && !state.getConnection().isClosed()) {
                        if (state.isTransactional()) {
                            state.getConnection().rollback();
                        }
                        state.getConnection().close();
                    }
                } catch (SQLException e) {
                    System.err.println("Ошибка закрытия: " + e.getMessage());
                }
            }
            pool.clear();
        });
    }
}
