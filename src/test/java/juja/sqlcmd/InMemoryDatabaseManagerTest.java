package juja.sqlcmd;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class InMemoryDatabaseManagerTest extends AbstractDatabaseManagerTest {
    private InMemoryDatabaseManager inMemoryDatabaseManager;

    @Before
    public void setUp() {
        super.init(new InMemoryDatabaseManager());
    }

    @Test
    public void connect() {
        assertTrue(databaseManager.connect(null, null, null));
    }

    @Override
    void createTestTableWithIdAndName(String tableName) {
        inMemoryDatabaseManager = (InMemoryDatabaseManager) databaseManager;
        inMemoryDatabaseManager.createTable(tableName, 2);
    }
}
