package com.shared.tx;

public final class TransactionContext {

    private static final ThreadLocal<String> ACTIVE_DB_KEY = new ThreadLocal<>();

    private TransactionContext() {}

    public static void begin(String dbKey) {
        ACTIVE_DB_KEY.set(dbKey);
    }

    public static void end() {
        ACTIVE_DB_KEY.remove();
    }

    public static boolean isActive(String dbKey) {
        return dbKey.equals(ACTIVE_DB_KEY.get());
    }
}