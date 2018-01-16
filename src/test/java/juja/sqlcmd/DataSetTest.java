package juja.sqlcmd;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class DataSetTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private PrintStream originalOut;
    private PrintStream originalErr;

    @Before
    public void setUpStreams() {
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertValueWithNegativeIndex() {
        DataSet dataSet = new DataSet(1);
        dataSet.insertValue(-1, "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertValueWithIndexEqualToSize() {
        int dataSetSize = 1;
        DataSet dataSet = new DataSet(dataSetSize);
        dataSet.insertValue(dataSetSize, "test");
    }
}
