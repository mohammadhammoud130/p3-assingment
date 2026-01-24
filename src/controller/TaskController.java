package controller;

import model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;

public class TaskController {

    private static HashMap<Integer, Task> tasks = new HashMap<>();
    private static final String tasksFilePath = "data/Tasks.csv";

    public static HashMap<Integer, Task> tasksLoader() {
        HashMap<Integer, Task> loadedTasks = new HashMap<>();
        File file = new File(tasksFilePath);
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] fields = line.split(",");
                if (fields.length < 9) continue;

                try {
                    int id = Integer.parseInt(fields[0].trim());
                    int productId = Integer.parseInt(fields[1].trim());
                    int requiredQuantity = Integer.parseInt(fields[2].trim());
                    int producedQuantity = Integer.parseInt(fields[3].trim());
                    String client = fields[4].trim().replace("\"", "");
                    LocalDate startDate = LocalDate.parse(fields[5].trim());
                    LocalDate deliveryDate = LocalDate.parse(fields[6].trim());
                    String status = fields[7].trim().toUpperCase();
                    int assignedLineId = Integer.parseInt(fields[8].trim());

                    Product requiredProduct = ProductController.getProducts().get(productId);
                    ProductLine assignedLine = ProductLineController.getProductLines().get(assignedLineId);

                    Task task = new Task(id, requiredProduct, requiredQuantity, producedQuantity, client,
                            startDate, deliveryDate, status, assignedLine);

                    // Note: This logic was preserved from your input
                    ProductLineController.addTaskToLine(assignedLineId, task);
                    loadedTasks.put(id, task);

                    if (assignedLine != null) {
                        ProductLineController.addTaskToLine(assignedLineId, task);
                    }

                    if (id > maxId) maxId = id;

                } catch (Exception e) {
                    ErrorLogger.logWarning("Failed to parse task line: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Tasks file not found: " + tasksFilePath);
            ErrorLogger.logWarning("Tasks file not found: " + tasksFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to read Tasks file: " + tasksFilePath);
            ErrorLogger.logWarning("No permission to read Tasks file: " + tasksFilePath);
        } catch (IOException e) {
            System.err.println("Error reading Tasks file: " + e.getMessage());
            ErrorLogger.logWarning("Error reading Tasks file: " + e.getMessage());
        }

        Task.resetIdCounter(maxId + 1);
        setTasks(loadedTasks);
        return loadedTasks;
    }

    public static void updateTasksFile() {
        String header = "id,productId,requiredQuantity,producedQuantity,client,startDate,deliveryDate,status,assignedLine";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tasksFilePath))) {
            writer.write(header);
            writer.newLine();

            for (Task task : tasks.values()) {
                if (task == null) continue;

                int lineId = (task.getAssignedLine() != null) ? task.getAssignedLine().getId() : -1;

                String line = String.join(",",
                        String.valueOf(task.getId()),
                        String.valueOf(task.getRequiresdProduct().getId()),
                        String.valueOf(task.getRequiredQuantity()),
                        String.valueOf(task.getProducedQuantity()),
                        "\"" + task.getClient() + "\"",
                        task.getStartDate().toString(),
                        task.getDeliveryDate().toString(),
                        task.getStatus().name(),
                        String.valueOf(lineId)
                );

                writer.write(line);
                writer.newLine();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Tasks file not found when writing: " + tasksFilePath);
            ErrorLogger.logWarning("Tasks file not found when writing: " + tasksFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to write Tasks file: " + tasksFilePath);
            ErrorLogger.logWarning("No permission to write Tasks file: " + tasksFilePath);
        } catch (IOException e) {
            System.err.println("Error writing Tasks file: " + e.getMessage());
            ErrorLogger.logWarning("Error writing Tasks file: " + e.getMessage());
        }
    }

    public static void addTask(int productLineID, Task task) {
        if (task == null) {
            String msg = "Cannot add null Task.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalArgumentException(msg);
        }

        try {
            if (!task.checkMaterials()) {
                System.out.println("Insufficient materials.");
                return;
            }

            ProductLine line = ProductLineController.getProductLines().get(productLineID);
            if (line == null) {
                String msg = "ProductLine with ID " + productLineID + " not found.";
                System.err.println(msg);
                ErrorLogger.logWarning(msg);
                return;
            }

            task.setStatus(Status.ACTIVE);
            task.setAssignedLine(line);
            tasks.put(task.getId(), task);

            ProductLineController.addTaskToLine(productLineID, task);
            updateTasksFile();

            System.out.println("The task has been added successfully.");

        } catch (Exception e) {
            System.err.println("Error adding task: " + e.getMessage());
            ErrorLogger.logWarning("Error adding task: " + e.getMessage());
        }
    }

    public static void updateTaskStatus(int taskId, Status newStatus) {
        if (tasks == null || tasks.isEmpty()) {
            String msg = "Task list is empty! Cannot update status.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }

        Task task = tasks.get(taskId);
        if (task != null) {
            task.setStatus(newStatus);
            updateTasksFile();
        } else {
            String msg = "Task with ID " + taskId + " not found.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    public static void deleteTask(int taskId) {
        if (tasks == null || tasks.isEmpty()) {
            String msg = "Task list is empty! Cannot delete task.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }

        Task task = tasks.get(taskId);
        if (task == null) {
            String msg = "Task with ID " + taskId + " not found.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalArgumentException(msg);
        }

        // Refund unused materials
        int remaining = task.getRequiredQuantity() - task.getProducedQuantity();
        if (remaining > 0) {
            for (java.util.Map.Entry<Item, Integer> entry : task.getRequiresdProduct().getRequiredItems().entrySet()) {
                ItemController.updateItemQTY(entry.getKey().getId(), entry.getValue() * remaining, true);
            }
        }

        if (task.getAssignedLine() != null && task.getAssignedLine().getTasks() != null) {
            task.getAssignedLine().getTasks().remove(task);
        }

        tasks.remove(taskId);
        updateTasksFile();
    }

    public static HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public static void setTasks(HashMap<Integer, Task> tasks) {
        TaskController.tasks = tasks;
    }
}
