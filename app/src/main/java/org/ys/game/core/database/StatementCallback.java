package org.ys.game.core.database;

public interface StatementCallback {

    void handleEvent(Transaction transaction, DatabaseResultSet resultSet);

}
