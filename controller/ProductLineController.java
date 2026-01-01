package controller;
import  model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ProductLineController {
    private static HashMap<Integer, ProductLine> productLines = new HashMap<>();
    private static final String productLinesFilePath = "/data/ProductLines.csv";

    static { productLines = productLinesLoader(); }

    private static HashMap<Integer, ProductLine> productLinesLoader() {
        HashMap<Integer, ProductLine> loadedLines = new HashMap<>();
        File file = new File(productLinesFilePath);
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split(",");
                    if (fields.length < 4) continue;

                    int id = Integer.parseInt(fields[0]);
                    String name = fields[1];
                    String status = fields[2];

                    // EDITED: parse task IDs and fetch real Task objects
                    ArrayList<Task> tasks = new ArrayList<>();
                    String[] taskIds = fields[3].split(";");
                    for (String tid : taskIds) {
                        if (tid.isEmpty()) continue;
                        int taskId = Integer.parseInt(tid);
                        Task task = TaskController.getTasks().get(taskId);
                        if (task != null) tasks.add(task);
                    }

                    ProductLine lineObj = new ProductLine(id, name, status, tasks);
                    loadedLines.put(id, lineObj);

                    if (id > maxId) maxId = id;
                } catch (Exception e) {
                    System.err.println("Error parsing ProductLine: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading ProductLines file: " + e.getMessage());
        }

        // EDITED: reset counter after loading
        ProductLine.resetIdCounter(maxId + 1);

        return loadedLines;
    }

    public static void updateProductLinesFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(productLinesFilePath))) {
            writer.write("id,name,status,tasks");
            writer.newLine();

            for (ProductLine line : productLines.values()) {
                if (line == null) continue;

                // EDITED: write task IDs only
                StringBuilder taskIds = new StringBuilder();
                for (Task task : line.getTasks()) {
                    taskIds.append(task.getId()).append(";");
                }

                writer.write(line.getId() + "," + line.getName() + "," +
                        line.getStatus() + "," + taskIds);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing ProductLines file: " + e.getMessage());
        }
    }

    // EDITED: add method for ProductLine
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
        } catch (Exception e) {
            System.err.println("Error adding product line: " + e.getMessage());
        }
    }

    public void displayProducts(int id) {
        ProductLine productLine =  productLines.get(id);

        if (productLine.getTasks() == null) {
            ErrorLogger.logWarning("Task list is not initialized for product \"" + productLine.getName() + "[" + id + "]\".");
            throw new IllegalStateException("Task list is not initialized!");
        }

        if (productLine.getTasks().isEmpty()) {
            System.out.println("No tasks found for product \"" + productLine.getName() + "[" + id + "]\".");
            return;
        }

        System.out.println("Product made by \"" + productLine.getName() + "[" + id + "]\" are:");

        for (Task task : productLine.getTasks()) {
            if (task == null) {
                ErrorLogger.logWarning("Null task encountered in product \"" + productLine.getName() + "[" + id + "]\".");
                continue;
            }

            if (task.getRequiresdProduct() == null) {
                ErrorLogger.logWarning("Task with ID " + task.getId() + " has null required product.");
                continue;
            }

            System.out.println(task.getRequiresdProduct().toString());
        }
    }

    public Product mostRequiredProductAtSomTime(LocalDate startDate, LocalDate endDate) {
        HashMap<Integer,Task> tasks = TaskController.getTasks();
        if (tasks == null) {
            ErrorLogger.logWarning("Task list is not initialized!");
            throw new IllegalStateException("Task list is not initialized!");
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

    public void addTask(int productLinesID, Task task){
        productLines.get(productLinesID).getTasks().add(task);
        TaskController.addTask(productLinesID,task);
    }

    public void showTaskForLine(int productLineID){
        ProductLine productLine = productLines.get(productLineID);
        if(productLine.getTasks().isEmpty()){
            System.out.println(" No tasks for this line "+" | " + productLine.getName());
            return;
        }
        System.out.println("Tasks related to the product line : "+" | " + productLine.getName());
        for(Task task:productLine.getTasks()){
            System.out.println("ID: "+task.getId());
            System.out.println("requiredProduct: "+task.getRequiresdProduct().getName());
            System.out.println("requiredQuantity: "+task.getRequiredQuantity());
            System.out.println("client: "+task.getClient());
            System.out.println(" startDate: "+task.getStartDate());
            System.out.println("deliveryDate: "+task.getDeliveryDate());
        }

    }

    public static HashMap<Integer, ProductLine> getProductLines() {
        return productLines;
    }
}
