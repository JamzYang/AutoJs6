package org.ys.game.core.database;

public interface TransactionCallback {

    void handleEvent(Transaction transaction);

}
