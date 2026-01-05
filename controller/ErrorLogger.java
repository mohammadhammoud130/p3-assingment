package controller;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLogger {

    public static void logWarning(String message) {
        // Format the current time
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Combine timestamp with the warning message
        String logEntry = "[" + timestamp + "] " + message;

        // Write to error.txt (append mode)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/data/error.txt", true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: error.txt");
        } catch (SecurityException e) {
            System.err.println("No permission to write to error.txt");
        } catch (IOException e) {
            System.err.println("Error writing to error.txt: " + e.getMessage());
        }
    }

}
