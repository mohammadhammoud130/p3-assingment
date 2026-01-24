package controller;

import model.*;
import java.io.*;
import java.util.*;

public class ProductLineController {
    private static HashMap<Integer, ProductLine> productLines = new HashMap<>();
    private static final String productLinesFilePath = "data/ProductLines.csv";

    public static HashMap<Integer, ProductLine> productLinesLoader() {
        HashMap<Integer, ProductLine> loadedLines = new HashMap<>();
        File file = new File(productLinesFilePath);
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split(",");
                    if (fields.length < 3) continue;

                    int id = Integer.parseInt(fields[0].trim());
                    String name = fields[1].trim();
                    Status status = Status.valueOf(fields[2].trim());

                    ProductLine lineObj = new ProductLine(id, name, status, new ArrayList<>());
                    loadedLines.put(id, lineObj);

                    if (id > maxId) maxId = id;

                } catch (Exception e) {
                    ErrorLogger.logWarning("Error parsing ProductLine: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("ProductLines file not found: " + productLinesFilePath);
            ErrorLogger.logWarning("ProductLines file not found: " + productLinesFilePath);
        } catch (Exception e) {
            System.err.println("Error reading ProductLines file: " + e.getMessage());
            ErrorLogger.logWarning("Error reading ProductLines file: " + e.getMessage());
        }

        ProductLine.resetIdCounter(maxId + 1);
        setProductLines(loadedLines);
        return loadedLines;
    }

    public static void updateProductLinesFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(productLinesFilePath))) {
            writer.write("id,name,status");
            writer.newLine();

            for (ProductLine line : productLines.values()) {
                writer.write(line.getId() + "," + line.getName() + "," + line.getStatus());
                writer.newLine();
            }
        } catch (FileNotFoundException e) {
            System.err.println("ProductLines file not found when writing: " + productLinesFilePath);
            ErrorLogger.logWarning("ProductLines file not found when writing: " + productLinesFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to write ProductLines file: " + productLinesFilePath);
            ErrorLogger.logWarning("No permission to write ProductLines file: " + productLinesFilePath);
        } catch (IOException e) {
            System.err.println("Error writing ProductLines file: " + e.getMessage());
            ErrorLogger.logWarning("Error writing ProductLines file: " + e.getMessage());
        }
    }

    public static void addProductLine(ProductLine line) {
        if (line == null) {
            String msg = "Cannot add null ProductLine.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalArgumentException(msg);
        }

        productLines.put(line.getId(), line);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(productLinesFilePath, true))) {
            String record = line.getId() + "," +
                    line.getName() + "," +
                    line.getStatus().name();

            writer.write(record);
            writer.newLine();
        } catch (FileNotFoundException e) {
            System.err.println("ProductLines file not found when adding line: " + productLinesFilePath);
            ErrorLogger.logWarning("ProductLines file not found when adding line: " + productLinesFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to write to ProductLines file: " + productLinesFilePath);
            ErrorLogger.logWarning("No permission to write to ProductLines file: " + productLinesFilePath);
        } catch (IOException e) {
            System.err.println("Error adding ProductLine: " + e.getMessage());
            ErrorLogger.logWarning("Error adding ProductLine: " + e.getMessage());
        }
    }

    public static void updateLineStatus(int lineId, Status newStatus) {
        if (productLines == null || productLines.isEmpty()) {
            String msg = "ProductLines list is empty! Cannot update status.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }

        ProductLine line = productLines.get(lineId);
        if (line == null) {
            String msg = "ProductLine with ID " + lineId + " not found.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalArgumentException(msg);
        }

        line.setStatus(newStatus);
        updateProductLinesFile();
    }

    public static void deleteProductLine(int lineId) {
        if (productLines == null || productLines.isEmpty()) {
            String msg = "ProductLines list is empty! Cannot delete line.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }

        ProductLine line = productLines.get(lineId);
        if (line == null) {
            String msg = "ProductLine with ID " + lineId + " not found.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalArgumentException(msg);
        }

        if (line.getTasks() != null) {
            List<Task> tasksToDelete = new ArrayList<>(line.getTasks());
            for (Task task : tasksToDelete) {
                TaskController.deleteTask(task.getId());
            }
        }

        productLines.remove(lineId);
        updateProductLinesFile();
    }

    public static void addTaskToLine(int lineId, Task task) {
        if (productLines == null || productLines.isEmpty()) {
            String msg = "ProductLines list is empty! Cannot add task to line.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }

        ProductLine line = productLines.get(lineId);
        if (line == null) {
            String msg = "ProductLine with ID " + lineId + " not found.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalArgumentException(msg);
        }

        if (line.getTasks() == null) line.setTasks(new ArrayList<>());
        if (!line.getTasks().contains(task)) line.getTasks().add(task);
    }

    public static HashMap<Integer, ProductLine> getProductLines() {
        return productLines;
    }

    public static void setProductLines(HashMap<Integer, ProductLine> productLines) {
        ProductLineController.productLines = productLines;
    }
}
