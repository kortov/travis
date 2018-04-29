package juja.sqlcmd.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class Console implements View {
    private Scanner scanner;
    private OutputStream outputStream;

    public Console() {
        this(System.in, System.out);
    }

    public Console(InputStream inputStream, OutputStream outputStream) {
        this.scanner = new Scanner(inputStream);
        this.outputStream = outputStream;
    }

    @Override
    public void write(String message) {
        try {
            String modifiedToMimicUserInput = message + System.lineSeparator();
            outputStream.write(modifiedToMimicUserInput.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String read() {
        return scanner.nextLine();
    }
}