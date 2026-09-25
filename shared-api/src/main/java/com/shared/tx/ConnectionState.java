package com.shared.tx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.SQLException;

@Data
@AllArgsConstructor
public class ConnectionState {
    private final Connection connection;
    private final boolean isTransactional;
    private final String dbKey;
    private volatile long ownerThreadId;

    public boolean isValid() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}