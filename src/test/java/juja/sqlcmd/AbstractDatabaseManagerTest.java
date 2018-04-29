package juja.sqlcmd;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public abstract class AbstractDatabaseManagerTest {
    protected static final String TEST_DB_NAME = "sqlcmd_test";
    protected static final String DB_USER_LOGIN = "sqlcmd";
    protected static final String DB_USER_PASSWORD = "sqlcmd";
    private static final String TEST_TABLE_NAME = "test_table";

    DatabaseManager databaseManager;

    public void init(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Test
    public void getTableNamesWithTwoTables() throws SQLException {
        createTestTableWithIdAndName("table1");
        createTestTableWithIdAndName("table2");
        String[] expectedArray = {"table1", "table2"};
        String[] actualArray = databaseManager.getTableNames();
        assertThat(actualArray, arrayContainingInAnyOrder(expectedArray));
    }

    @Test
    public void getTableNamesWhenDbHasNoTables() throws SQLException {
        String[] expectedArray = new String[]{};
        String[] actualArray = databaseManager.getTableNames();
        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    public void getTableDataWithEmptyTable() throws SQLException {
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        DataSet[] expectedArray = new DataSet[0];
        Assert.assertArrayEquals(expectedArray, databaseManager.getTableData(TEST_TABLE_NAME));
    }

    @Test
    public void getTableDataWithNotExistingTable() throws SQLException {
        DataSet[] expectedArray = new DataSet[0];
        Assert.assertArrayEquals(expectedArray, databaseManager.getTableData("WrongTableName"));
    }

    @Test
    public void getTableDataWithValidTableTwoRows() throws SQLException {
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        DataSet[] expectedArray = fillDataSetsWithIdAndName(2);
        insertDataSets(TEST_TABLE_NAME, expectedArray);
        DataSet[] actualArray = databaseManager.getTableData(TEST_TABLE_NAME);
        assertThat(actualArray, arrayContainingInAnyOrder(expectedArray));
    }

    @Test
    public void insertWithExistingTable() throws SQLException {
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        int rowsNumber = 1;
        DataSet[] expected = fillDataSetsWithIdAndName(rowsNumber);
        assertTrue(databaseManager.insert(TEST_TABLE_NAME, expected[rowsNumber - 1]));
    }

    @Test
    public void insertWithNotExistingTable() {
        int rowsNumber = 1;
        DataSet[] expected = fillDataSetsWithIdAndName(rowsNumber);
        assertFalse(databaseManager.insert("tableDoesNotExist", expected[rowsNumber - 1]));
    }

    @Test
    public void insertWithExtraParameters() throws SQLException {
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        DataSet row = new DataSet(3);
        row.insertValue(0, "1");
        row.insertValue(1, "name1");
        row.insertValue(2, "name2");
        assertFalse(databaseManager.insert(TEST_TABLE_NAME, row));
    }

    @Test
    public void insertIntoTableRowWithoutId() throws SQLException {
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        DataSet row = new DataSet(2);
        row.insertValue(1, "name1");
        assertFalse(databaseManager.insert(TEST_TABLE_NAME, row));
    }

    @Test
    public void deleteWithExistingTableAndValidData() throws SQLException {
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        DataSet[] expected = fillDataSetsWithIdAndName(2);
        insertDataSets(TEST_TABLE_NAME, expected);
        assertTrue(databaseManager.delete(TEST_TABLE_NAME, 1));
    }

    @Test
    public void deleteWithNotExistingTable() {
        assertFalse(databaseManager.delete("doesNotExist", 1));
    }

    @Test
    public void deleteWithNotExistingRowWithSuchId() throws SQLException {
        createTestTableWithIdAndName(TEST_TABLE_NAME);
        assertFalse(databaseManager.delete(TEST_TABLE_NAME, 1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void update() {
        databaseManager.update(TEST_TABLE_NAME, 1);
    }

    private DataSet[] fillDataSetsWithIdAndName(int rowsNumber) {
        DataSet[] insertedRows = new DataSet[rowsNumber];
        for (int i = 0; i < rowsNumber; i++) {
            int id = i + 1;
            DataSet row = new DataSet(2);
            row.insertValue(0, String.valueOf(id));
            row.insertValue(1, "name" + id);
            insertedRows[i] = row;
        }
        return insertedRows;
    }

    private void insertDataSets(String testTableName, DataSet[] rows) {
        for (DataSet row : rows) {
            databaseManager.insert(testTableName, row);
        }
    }

    abstract void createTestTableWithIdAndName(String tableName) throws SQLException;
}
