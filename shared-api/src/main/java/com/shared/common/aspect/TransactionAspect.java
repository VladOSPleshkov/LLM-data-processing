package com.shared.common.aspect;

import com.shared.common.annotation.ManualTransactional;
import com.shared.tx.TransactionContext;
import lombok.RequiredArgsConstructor;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;
import com.shared.tx.ConnectionHolder;
import java.sql.Connection;
import java.sql.SQLException;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionAspect {
    private final ConnectionHolder connectionHolder;

    @Around("@annotation(manualTransactional)")
    public Object manageTransaction(ProceedingJoinPoint joinPoint, ManualTransactional manualTransaction) throws Throwable {
        String dbKey = manualTransaction.dbKey();
        Connection connection = connectionHolder.getConnection(dbKey,true);
        // Помечаем поток как "внутри транзакции" ДО вызова бизнес-логики,
        // чтобы вложенные DAO-вызовы не пытались закрыть этот же коннект сами.
        TransactionContext.begin(dbKey);
        boolean rollBack = false;
        try {
            return joinPoint.proceed();
        } catch (RuntimeException runtimeException) {
            rollBack = true;
            System.out.println("транзакция" + dbKey +"откатана из-за" + runtimeException);
            throw runtimeException;
        } catch (Throwable throwable) {
            throw throwable;

        } finally {
            try {
                connectionHolder.releaseConnection(dbKey, connection, rollBack);
            } catch (SQLException sqlException) {
                System.out.println("не удалось завершить транзакцию" + dbKey + sqlException);
            } finally {
                TransactionContext.end();
            }
        }
    }
    @PreDestroy
    public void closeAllConnectionOnShutdown() {
        System.out.println("[SHUTDOWN] Инициировано закрытие Spring-контекста. Очищаем все открытые коннекты...");
        connectionHolder.closeAllPools();
        System.out.println("[SHUTDOWN] Все пулы соединений ConnectionHolder успешно уничтожены.");
    }
}
