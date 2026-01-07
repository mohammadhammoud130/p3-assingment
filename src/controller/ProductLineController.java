package controller;

import model.*;
import java.io.*;
import java.time.LocalDate;
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
                    if (fields.length < 4) {
                        System.err.println("Invalid ProductLine line: " + line);
                        ErrorLogger.logWarning("Invalid ProductLine line: " + line);
                        continue;
                    }

                    int id = Integer.parseInt(fields[0].trim());
                    String name = fields[1].trim();
                    Status status = Status.valueOf(fields[2].trim());

                    ArrayList<Task> tasks = new ArrayList<>();
                    String[] taskIds = fields[3].split(";");

                    for (String tid : taskIds) {
                        if (tid.isEmpty()) continue;

                        try {
                            int taskId = Integer.parseInt(tid.trim());
                            Task task = TaskController.getTasks().get(taskId);

                            if (task == null) {
                                String msg = "Referenced task ID " + taskId + " not found for ProductLine " + id;
                                System.err.println(msg);
                                ErrorLogger.logWarning(msg);
                                continue;
                            }

                            tasks.add(task);

                        } catch (Exception e) {
                            System.err.println("Invalid task ID in ProductLine: " + tid);
                            ErrorLogger.logWarning("Invalid task ID in ProductLine: " + tid);
                        }
                    }

                    ProductLine lineObj = new ProductLine(id, name, status, tasks);
                    loadedLines.put(id, lineObj);

                    if (id > maxId) maxId = id;

                } catch (NumberFormatException e) {
                    System.err.println("Number format error in ProductLine: " + line);
                    ErrorLogger.logWarning("Number format error in ProductLine: " + line);
                } catch (NullPointerException e) {
                    System.err.println("Null value found in ProductLine: " + line);
                    ErrorLogger.logWarning("Null value found in ProductLine: " + line);
                } catch (Exception e) {
                    System.err.println("Unexpected error parsing ProductLine: " + line);
                    ErrorLogger.logWarning("Unexpected error parsing ProductLine: " + line);
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("ProductLines file not found: " + productLinesFilePath);
            ErrorLogger.logWarning("ProductLines file not found: " + productLinesFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to read ProductLines file: " + productLinesFilePath);
            ErrorLogger.logWarning("No permission to read ProductLines file: " + productLinesFilePath);
        } catch (IOException e) {
            System.err.println("Error reading ProductLines file: " + e.getMessage());
            ErrorLogger.logWarning("Error reading ProductLines file: " + e.getMessage());
        }

        ProductLine.resetIdCounter(maxId + 1);
        return loadedLines;
    }

    public static void updateProductLinesFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(productLinesFilePath))) {

            writer.write("id,name,status,tasks");
            writer.newLine();

            for (ProductLine line : productLines.values()) {
                if (line == null) continue;

                StringBuilder taskIds = new StringBuilder();
                for (Task task : line.getTasks()) {
                    taskIds.append(task.getId()).append(";");
                }

                writer.write(line.getId() + "," +
                        line.getName() + "," +
                        line.getStatus() + "," +
                        taskIds);
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
        productLines.put(line.getId(), line);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(productLinesFilePath, true))) {

            StringBuilder taskIds = new StringBuilder();
            for (Task task : line.getTasks()) {
                taskIds.append(task.getId()).append(";");
            }

            writer.write(line.getId() + "," +
                    line.getName() + "," +
                    line.getStatus() + "," +
                    taskIds + "\n");

        } catch (FileNotFoundException e) {
            System.err.println("ProductLines file not found when adding line: " + productLinesFilePath);
            ErrorLogger.logWarning("ProductLines file not found when adding line: " + productLinesFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to write ProductLines file when adding line: " + productLinesFilePath);
            ErrorLogger.logWarning("No permission to write ProductLines file when adding line: " + productLinesFilePath);
        } catch (IOException e) {
            System.err.println("Error adding ProductLine: " + e.getMessage());
            ErrorLogger.logWarning("Error adding ProductLine: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error adding ProductLine: " + e.getMessage());
            ErrorLogger.logWarning("Unexpected error adding ProductLine: " + e.getMessage());
        }
    }

    public void displayProducts(int id) {
        ProductLine productLine = productLines.get(id);

        if (productLine == null) {
            String msg = "ProductLine with ID " + id + " not found.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            return;
        }

        if (productLine.getTasks() == null) {
            String msg = "Task list is not initialized for ProductLine " + id;
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }

        if (productLine.getTasks().isEmpty()) {
            System.out.println("No tasks found for ProductLine " + productLine.getName() + "[" + id + "]");
            return;
        }

        System.out.println("Products made by " + productLine.getName() + "[" + id + "]:");

        for (Task task : productLine.getTasks()) {
            if (task == null) {
                ErrorLogger.logWarning("Null task encountered in ProductLine " + id);
                continue;
            }

            if (task.getRequiresdProduct() == null) {
                ErrorLogger.logWarning("Task " + task.getId() + " has null required product.");
                continue;
            }

            System.out.println(task.getRequiresdProduct());
        }
    }

    public Product mostRequiredProductAtSomTime(LocalDate startDate, LocalDate endDate) {
        HashMap<Integer, Task> tasks = TaskController.getTasks();

        if (tasks == null) {
            String msg = "Task list is not initialized.";
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }

        HashMap<Product, Integer> productCounts = new HashMap<>();

        for (Task task : tasks.values()) {
            if (task == null || task.getRequiresdProduct() == null || task.getStartDate() == null) {
                ErrorLogger.logWarning("Invalid task encountered while counting products.");
                continue;
            }

            LocalDate taskDate = task.getStartDate();

            if ((taskDate.isEqual(startDate) || taskDate.isAfter(startDate)) &&
                    (taskDate.isEqual(endDate) || taskDate.isBefore(endDate))) {

                Product product = task.getRequiresdProduct();
                productCounts.put(product, productCounts.getOrDefault(product, 0) + 1);
            }
        }

        Product mostRequired = null;
        int maxCount = 0;

        for (Map.Entry<Product, Integer> entry : productCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostRequired = entry.getKey();
            }
        }

        return mostRequired;
    }

    public void addTask(int productLineID, Task task) {
        ProductLine line = productLines.get(productLineID);

        if (line == null) {
            String msg = "ProductLine with ID " + productLineID + " not found.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            return;
        }

        try {
            line.getTasks().add(task);
            TaskController.addTask(productLineID, task);
        } catch (Exception e) {
            System.err.println("Error adding task to ProductLine " + productLineID + ": " + e.getMessage());
            ErrorLogger.logWarning("Error adding task to ProductLine " + productLineID + ": " + e.getMessage());
        }
    }

    public void showTaskForLine(int productLineID) {
        ProductLine productLine = productLines.get(productLineID);

        if (productLine == null) {
            String msg = "ProductLine with ID " + productLineID + " not found.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            return;
        }

        if (productLine.getTasks().isEmpty()) {
            System.out.println("No tasks for this line | " + productLine.getName());
            return;
        }

        System.out.println("Tasks related to ProductLine | " + productLine.getName());

        for (Task task : productLine.getTasks()) {
            if (task == null) {
                ErrorLogger.logWarning("Null task encountered in ProductLine " + productLineID);
                continue;
            }

            System.out.println("ID: " + task.getId());
            System.out.println("requiredProduct: " + task.getRequiresdProduct().getName());
            System.out.println("requiredQuantity: " + task.getRequiredQuantity());
            System.out.println("client: " + task.getClient());
            System.out.println("startDate: " + task.getStartDate());
            System.out.println("deliveryDate: " + task.getDeliveryDate());
        }
    }

    public static HashMap<Integer, ProductLine> getProductLines() {
        return productLines;
    }

    public static void setProductLines(HashMap<Integer, ProductLine> productLines) {
        ProductLineController.productLines = productLines;
    }
}
