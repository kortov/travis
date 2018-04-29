package juja.sqlcmd.view;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class ConsoleTest {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private InputStream originalIn;
    private View view;

    @Before
    public void setUp() {
        originalOut = System.out;
        originalIn = System.in;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        view = new Console();
    }

    @After
    public void cleanUpStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    public void writeEmptyLine() {
        String testLine = "";
        String expected = testLine + LINE_SEPARATOR;
        view.write(testLine);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    public void writeOneLine() {
        String testLine = "the first line";
        String expected = testLine + LINE_SEPARATOR;
        view.write(testLine);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    public void writeTwoLines() {
        String testLines = "the first line" + LINE_SEPARATOR +
                "the second line";
        String expected = testLines + LINE_SEPARATOR;
        view.write(testLines);
        assertEquals(expected, outputStream.toString());
    }

    @Test(expected = java.util.NoSuchElementException.class)
    public void readEmptyLine() {
        String expected = "";
        setInputStreamMessage(expected);
        assertEquals(expected, view.read());
    }

    @Test
    public void readOneLine() {
        String expected = "test input";
        setInputStreamMessage(expected);
        assertEquals(expected, view.read());
    }

    @Test
    public void readTwoLinesReadsOnlyFirstLine() {
        String firstInputLine = "the first line";
        String message = firstInputLine + LINE_SEPARATOR +
                "the second line";
        setInputStreamMessage(message);
        assertEquals(firstInputLine, view.read());
    }

    private void setInputStreamMessage(String message) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(message.getBytes());
        view = new Console(inputStream, outputStream);
        System.setIn(inputStream);
    }
}