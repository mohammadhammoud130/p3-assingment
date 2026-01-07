package controller;

import model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

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
                try {
                    String[] fields = line.split(",");
                    if (fields.length < 8) {
                        System.err.println("Invalid Task line: " + line);
                        ErrorLogger.logWarning("Invalid Task line: " + line);
                        continue;
                    }

                    int id = Integer.parseInt(fields[0].trim());
                    int productId = Integer.parseInt(fields[1].trim());
                    int requiredQuantity = Integer.parseInt(fields[2].trim());
                    String client = fields[3].trim();
                    LocalDate startDate = LocalDate.parse(fields[4].trim());
                    LocalDate deliveryDate = LocalDate.parse(fields[5].trim());
                    Status status = Status.valueOf(fields[6].trim());
                    double progress = Double.parseDouble(fields[7].trim());

                    Product requiredProduct = ProductController.getProducts().get(productId);
                    if (requiredProduct == null) {
                        String msg = "Referenced product ID " + productId + " not found for Task " + id;
                        System.err.println(msg);
                        ErrorLogger.logWarning(msg);
                        continue;
                    }

                    Task task = new Task(requiredProduct, requiredQuantity, client,
                            startDate, deliveryDate, status, progress, null);

                    loadedTasks.put(id, task);
                    if (id > maxId) maxId = id;

                } catch (NumberFormatException e) {
                    System.err.println("Number format error in Task line: " + line);
                    ErrorLogger.logWarning("Number format error in Task line: " + line);
                } catch (NullPointerException e) {
                    System.err.println("Null value found in Task line: " + line);
                    ErrorLogger.logWarning("Null value found in Task line: " + line);
                } catch (Exception e) {
                    System.err.println("Unexpected error parsing Task line: " + line);
                    ErrorLogger.logWarning("Unexpected error parsing Task line: " + line);
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
        return loadedTasks;
    }

    public static void updateTasksFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tasksFilePath))) {

            writer.write("id,productId,requiredQuantity,client,startDate,deliveryDate,status,progress");
            writer.newLine();

            for (Task task : tasks.values()) {
                if (task == null) continue;

                writer.write(task.getId() + "," +
                        task.getRequiresdProduct().getId() + "," +
                        task.getRequiredQuantity() + "," +
                        task.getClient() + "," +
                        task.getStartDate() + "," +
                        task.getDeliveryDate() + "," +
                        task.getStatus() + "," +
                        task.getProgress());
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
        try {
            boolean itemAvailable = task.checkMaterials();

            if (!itemAvailable) {
                String msg = "Failed to add task: insufficient materials.";
                System.err.println(msg);
                ErrorLogger.logWarning(msg);
                return;
            }

            tasks.put(task.getId(), task);
            task.setStatus(Status.ACTIVE);

            ProductLine line = ProductLineController.getProductLines().get(productLineID);
            if (line == null) {
                String msg = "ProductLine with ID " + productLineID + " not found.";
                System.err.println(msg);
                ErrorLogger.logWarning(msg);
                return;
            }

            task.setAssignedLine(line);

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

            System.out.println("The task has been added successfully.");

        } catch (Exception e) {
            System.err.println("Error adding task: " + e.getMessage());
            ErrorLogger.logWarning("Error adding task: " + e.getMessage());
        }
    }

    public ArrayList<Task> activeTasks() {
        ArrayList<Task> result = new ArrayList<>();

        if (tasks == null) {
            String msg = "Task list is not initialized.";
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }

        for (Task task : tasks.values()) {
            if (task == null) {
                ErrorLogger.logWarning("Null task encountered in activeTasks.");
                continue;
            }

            Status status = task.getStatus();
            if (status == null) {
                ErrorLogger.logWarning("Task " + task.getId() + " has null status.");
                continue;
            }

            if (status == Status.ACTIVE) result.add(task);
        }

        return result;
    }

    public ArrayList<Task> pausedTasks() {
        ArrayList<Task> result = new ArrayList<>();

        if (tasks == null) {
            String msg = "Task list is not initialized.";
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }

        for (Task task : tasks.values()) {
            if (task == null) {
                ErrorLogger.logWarning("Null task encountered in pausedTasks.");
                continue;
            }

            Status status = task.getStatus();
            if (status == null) {
                ErrorLogger.logWarning("Task " + task.getId() + " has null status.");
                continue;
            }

            if (status == Status.PAUSED) result.add(task);
        }

        return result;
    }

    public ArrayList<Task> finishedTasks() {
        ArrayList<Task> result = new ArrayList<>();

        if (tasks == null) {
            String msg = "Task list is not initialized.";
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }

        for (Task task : tasks.values()) {
            if (task == null) {
                ErrorLogger.logWarning("Null task encountered in finishedTasks.");
                continue;
            }

            Status status = task.getStatus();
            if (status == null) {
                ErrorLogger.logWarning("Task " + task.getId() + " has null status.");
                continue;
            }

            if (status == Status.FINISHED) result.add(task);
        }

        return result;
    }

    public void cancelTask(int taskId) {
        if (!tasks.containsKey(taskId)) {
            String msg = "Task with ID " + taskId + " not found.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            return;
        }

        Task task = tasks.get(taskId);

        if (task.getProgress() == 100.0) {
            System.out.println("Cannot cancel a completed task.");
            return;
        }

        try {
            for (Map.Entry<Item, Integer> entry : task.getRequiresdProduct().getRequiredItems().entrySet()) {
                int totalQty = entry.getValue() * task.getRequiredQuantity();
                ItemController.updateItemQTY(entry.getKey().getId(), totalQty, true);
            }

            tasks.remove(taskId);
            System.out.println("Task cancelled successfully.");

        } catch (Exception e) {
            System.err.println("Error cancelling task: " + e.getMessage());
            ErrorLogger.logWarning("Error cancelling task: " + e.getMessage());
        }
    }

    public void showTaskForProd(String prodName, ArrayList<ProductLine> lines) {
        if (prodName == null || prodName.trim().isEmpty()) {
            String msg = "Product name cannot be null or empty.";
            ErrorLogger.logWarning(msg);
            throw new IllegalArgumentException(msg);
        }

        if (lines == null) {
            String msg = "Product lines list is not initialized.";
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }

        if (lines.isEmpty()) {
            System.out.println("No product lines available.");
            return;
        }

        boolean found = false;

        for (ProductLine line : lines) {
            if (line == null || line.getTasks() == null) {
                ErrorLogger.logWarning("Null ProductLine or task list encountered.");
                continue;
            }

            for (Task task : line.getTasks()) {
                if (task == null || task.getRequiresdProduct() == null) {
                    ErrorLogger.logWarning("Null task or required product encountered.");
                    continue;
                }

                if (prodName.equalsIgnoreCase(task.getRequiresdProduct().getName())) {
                    if (!found) {
                        System.out.println("Tasks for product \"" + prodName + "\":");
                        found = true;
                    }

                    System.out.println("assignedLine: " + line.getName());
                    System.out.println("ID: " + task.getId());
                    System.out.println("requiredQuantity: " + task.getRequiredQuantity());
                    System.out.println("client: " + task.getClient());
                    System.out.println("progress: " + task.getProgress());
                    System.out.println("startDate: " + task.getStartDate());
                    System.out.println("deliveryDate: " + task.getDeliveryDate());
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

    public static void setTasks(HashMap<Integer, Task> tasks) {
        TaskController.tasks = tasks;
    }
}
