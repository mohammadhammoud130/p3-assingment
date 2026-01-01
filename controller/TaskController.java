package controller;

import model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class TaskController {
    private static HashMap<Integer, Task> tasks = new HashMap<>();
    private static final String tasksFilePath = "/data/Tasks.csv";

    static { tasks = tasksLoader(); }

    private static HashMap<Integer, Task> tasksLoader() {
        HashMap<Integer, Task> loadedTasks = new HashMap<>();
        File file = new File(tasksFilePath);
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split(",");
                    if (fields.length < 8) continue;

                    int id = Integer.parseInt(fields[0]);
                    int productId = Integer.parseInt(fields[1]);
                    int requiredQuantity = Integer.parseInt(fields[2]);
                    String client = fields[3];
                    LocalDate startDate = LocalDate.parse(fields[4]);
                    LocalDate deliveryDate = LocalDate.parse(fields[5]);
                    String status = fields[6];
                    double progress = Double.parseDouble(fields[7]);

                    // EDITED: fetch Product object by ID
                    Product requiredProduct = ProductController.getProducts().get(productId);

                    Task task = new Task(requiredProduct, requiredQuantity, client,
                            startDate, deliveryDate, status, progress, null);
                    loadedTasks.put(id, task);

                    if (id > maxId) maxId = id;
                } catch (Exception e) {
                    System.err.println("Error parsing Task line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Tasks file: " + e.getMessage());
        }

        // EDITED: reset counter after loading
        Task.resetIdCounter(maxId + 1);

        return loadedTasks;
    }

    // EDITED: write tasks back to CSV
    public static void updateTasksFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tasksFilePath))) {
            writer.write("id,productId,requiredQuantity,client,startDate,deliveryDate,status,progress");
            writer.newLine();

            for (Task task : tasks.values()) {
                if (task == null) continue;

                writer.write(task.getId() + "," +
                        task.getRequiresdProduct().getId() + "," +   // only productId
                        task.getRequiredQuantity() + "," +
                        task.getClient() + "," +
                        task.getStartDate() + "," +
                        task.getDeliveryDate() + "," +
                        task.getStatus() + "," +
                        task.getProgress());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing Tasks file: " + e.getMessage());
        }
    }

    public static void addTask(int productLineID, Task task) {
        try {
            boolean itemAvailable = task.checkMaterials();
            if (itemAvailable) {
                tasks.put(task.getId(), task);
                task.setStatus("Active");
                task.setAssignedLine(ProductLineController.getProductLines().get(productLineID));

                // EDITED: append to file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(tasksFilePath, true))) {
                    writer.write(task.getId() + "," +
                            task.getRequiresdProduct().getId() + "," +
                            task.getRequiredQuantity() + "," +
                            task.getClient() + "," +
                            task.getStartDate() + "," +
                            task.getDeliveryDate() + "," +
                            task.getStatus() + "," +
                            task.getProgress() + "\n");
                }
                System.out.println("The task has been added successfully!");
            } else {
                System.out.println("Failed to add task: item not available or minimum threshold check failed.");
            }
        } catch (Exception e) {
            System.err.println("Error adding task: " + e.getMessage());
        }
    }

    public ArrayList<Task> activeTasks() {
        ArrayList<Task> activeTasks = new ArrayList<>();

        if (tasks == null) {
            ErrorLogger.logWarning("Task list is not initialized!");
            throw new IllegalStateException("Task list is not initialized!");
        }

        for (Task task : tasks.values()) {
            if (task == null) {
                ErrorLogger.logWarning("Null task encountered in tasks list.");
                continue;
            }

            String status = task.getStatus();
            if (status == null) {
                ErrorLogger.logWarning("Task with ID " + task.getId() + " has null status.");
                continue;
            }

            if (status.trim().equalsIgnoreCase("Active")) {
                activeTasks.add(task);
            }
        }

        return activeTasks;
    }

    public ArrayList<Task> pausedTasks() {
        ArrayList<Task> pausedTasks = new ArrayList<>();
        if (tasks == null) {
            ErrorLogger.logWarning("Task list is not initialized!");
            throw new IllegalStateException("Task list is not initialized!");
        }
        for (Task task : tasks.values()) {
            if (task == null) {
                ErrorLogger.logWarning("Null task encountered in tasks list.");
                continue;
            }
            String status = task.getStatus();
            if (status == null) {
                ErrorLogger.logWarning("Task with ID " + task.getId() + " has null status.");
                continue;
            }
            if (status.trim().equalsIgnoreCase("Paused")) {
                pausedTasks.add(task);
            }
        }
        return pausedTasks;
    }

    public ArrayList<Task> finishedTasks() {
        ArrayList<Task> finishedTasks = new ArrayList<>();
        if (tasks == null) {
            ErrorLogger.logWarning("Task list is not initialized!");
            throw new IllegalStateException("Task list is not initialized!");
        }
        for (Task task : tasks.values()) {
            if (task == null) {
                ErrorLogger.logWarning("Null task encountered in tasks list.");
                continue;
            }
            String status = task.getStatus();
            if (status == null) {
                ErrorLogger.logWarning("Task with ID " + task.getId() + " has null status.");
                continue;
            }
            if (status.trim().equalsIgnoreCase("Finished")) {
                finishedTasks.add(task);
            }
        }
        return finishedTasks;
    }

    public void cancelTask(int taskId) {

        if(!tasks.containsKey(taskId)){
            System.out.println(" Task with Id "+taskId+" not found. ");
            return ;}
        if(tasks.get(taskId).getProgress()==100.0){
            System.out.println(" can’t cancel a completed task..! ");
        }
        else {
            Task requiredTask = tasks.get(taskId);
            for(Map.Entry<Item,Integer>entry :requiredTask.getRequiresdProduct().getRequiredItems().entrySet()){
                int totalQuantity =entry.getValue()*requiredTask.getRequiredQuantity();
                ItemController.updateItemQTY(entry.getKey().getId(),totalQuantity,true);
            }
            tasks.remove(taskId);
            System.out.println(" The task has been cancelled..! ");

        }

    }

    public void showTaskForProd(String prodName, ArrayList<ProductLine> lines) {
        if (prodName == null || prodName.trim().isEmpty()) {
            ErrorLogger.logWarning("Product name is null or empty.");
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }

        if (lines == null) {
            ErrorLogger.logWarning("Product lines list is not initialized.");
            throw new IllegalStateException("Product lines list is not initialized.");
        }

        if (lines.isEmpty()) {
            System.out.println("No product lines available.");
            return;
        }

        boolean found = false;

        for (ProductLine productLine : lines) {
            if (productLine == null || productLine.getTasks() == null) {
                ErrorLogger.logWarning("Null product line or tasks list encountered.");
                continue;
            }

            for (Task task : productLine.getTasks()) {
                if (task == null || task.getRequiresdProduct() == null) {
                    ErrorLogger.logWarning("Null task or required product encountered.");
                    continue;
                }

                String taskProdName = task.getRequiresdProduct().getName();
                if (taskProdName != null && taskProdName.equalsIgnoreCase(prodName)) {
                    if (!found) {
                        System.out.println("Tasks of product \"" + prodName + "\":");
                        found = true;
                    }

                    System.out.println(" assignedLine: " + productLine.getName());
                    System.out.println(" ID: " + task.getId());
                    System.out.println(" requiredQuantity: " + task.getRequiredQuantity());
                    System.out.println(" client: " + task.getClient());
                    System.out.println(" progress: " + task.getProgress());
                    System.out.println(" startDate: " + task.getStartDate());
                    System.out.println(" deliveryDate: " + task.getDeliveryDate());
                    System.out.println("-----------------------------------");
                }
            }
        }

        if (!found) {
            System.out.println("No task found for product \"" + prodName + "\".");
        }
    }

    public static HashMap<Integer, Task> getTasks() {
        return tasks;
    }

}
