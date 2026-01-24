package controller;

import model.Product;
import model.ProductLine;
import model.Status;
import model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TaskRunner {

    private static final Set<Integer> runningTaskIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static boolean isSystemRunning = false;

    public static void start() {
        if (isSystemRunning) return;
        isSystemRunning = true;

        Thread monitorThread = new Thread(() -> {
            while (isSystemRunning) {
                try {
                    checkAndStartTasks();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();
        System.out.println("Production System Started.");
    }

    private static void checkAndStartTasks() {
        ArrayList<Task> allTasks = new ArrayList<>(TaskController.getTasks().values());
        for (Task task : allTasks) {
            if (task.getStatus() == Status.ACTIVE && !runningTaskIds.contains(task.getId())) {
                startTaskThread(task);
            }
        }
    }

    private static void startTaskThread(Task task) {
        runningTaskIds.add(task.getId());

        Thread taskThread = new Thread(() -> {
            Product product = task.getRequiresdProduct();
            double estimatedMinutes = product.getEstimatedTime();
            long waitTimeMs = (long) ((estimatedMinutes / 5.0) * 1000);

            while (task.getStatus() == Status.ACTIVE && task.getProducedQuantity() < task.getRequiredQuantity()) {

                ProductLine line = task.getAssignedLine();
                if (line == null || line.getStatus() != Status.ACTIVE) {
                    break;
                }

                try {
                    Thread.sleep(waitTimeMs);
                } catch (InterruptedException e) {
                    break;
                }

                // 1. Update Task Progress
                int newQty = task.getProducedQuantity() + 1;
                task.setProducedQuantity(newQty);
                TaskController.updateTasksFile();

                if (newQty >= task.getRequiredQuantity()) {
                    TaskController.updateTaskStatus(task.getId(), Status.FINISHED);
                }
            }
            runningTaskIds.remove(task.getId());
        });

        taskThread.start();
    }
}
