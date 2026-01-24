package model;

import controller.ItemController;

import java.time.LocalDate;
import java.util.Map;

public class Task {
    private static int nextId = 1;   // shared counter
    private final int id;            // unique per instance
    private Product requiredProduct;
    private int requiredQuantity;
    private int producedQuantity;
    private String client;
    private LocalDate startDate;
    private LocalDate deliveryDate;
    private Status status;  // ["Active", "Finished", "Paused"]
    private double progress;
    private ProductLine assignedLine;

    // Default constructor

    public Task(int id, Product requiredProduct, int requiredQuantity, int producedQuantity,
                String client, LocalDate startDate, LocalDate deliveryDate,
                String status, ProductLine assignedLine) {
        this.id = id;
        this.requiredProduct = requiredProduct;
        this.requiredQuantity = requiredQuantity;
        this.producedQuantity = producedQuantity;
        this.client = client;
        this.startDate = startDate;
        this.deliveryDate = deliveryDate;
        this.status = Status.valueOf(status);
        this.progress = producedQuantity * 100.0 / requiredQuantity;
        this.assignedLine = assignedLine;
    }

    // Constructor with validation
    public Task(Product requiredProduct, int requiredQuantity, String client,
                LocalDate startDate, LocalDate deliveryDate,
                String status, ProductLine assignedLine) {

        if (requiredProduct == null) {
            throw new IllegalArgumentException("Required product cannot be null.");
        }
        if (requiredQuantity <= 0) {
            throw new IllegalArgumentException("Required quantity must be positive.");
        }
        if (client == null || client.trim().isEmpty()) {
            throw new IllegalArgumentException("Client cannot be null or empty.");
        }
        if (startDate == null || deliveryDate == null) {
            throw new IllegalArgumentException("Dates cannot be null.");
        }
        if (deliveryDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Delivery date cannot be before start date.");
        }

        this.id = nextId++;
        this.requiredProduct = requiredProduct;
        this.requiredQuantity = requiredQuantity;
        this.producedQuantity = 0;
        this.client = client;
        this.startDate = startDate;
        this.deliveryDate = deliveryDate;
        this.status = Status.valueOf(status);
        this.progress = 0;
        this.assignedLine = assignedLine;
    }

    public static void resetIdCounter(int newNextId) {
        nextId = newNextId;
    }

    public boolean checkMaterials() {
        if (ItemController.getItems() == null || ItemController.getItems().isEmpty()) {
            throw new IllegalStateException("Inventory in Storage is empty! Cannot check materials.");
        }
        if (requiredProduct == null || requiredProduct.getRequiredItems() == null) {
            throw new IllegalStateException("Required product or its items are not defined.");
        }

        Map<Item, Integer> requiredItems = requiredProduct.getRequiredItems();

        for (Map.Entry<Item, Integer> entry : requiredItems.entrySet()) {
            Item item = entry.getKey();
            int qtyPerUnit = entry.getValue();
            int totalNeed = qtyPerUnit * requiredQuantity;

            if (item == null) {
                throw new IllegalStateException("Null item in required product.");
            }

            if (item.getQuantity() < totalNeed) {
                System.out.println("Not enough quantity of the item: " + item.getName());
                System.out.println("Required: " + totalNeed + " | Available: " + item.getQuantity());
                return false;
            }

            if ((item.getQuantity() - totalNeed) < item.getMinThreshold()) {
                System.out.println("Warning: Stock of " + item.getName() + " will drop below minimum threshold!");
                return false;
            }
        }

        // Deduct materials
        for (Map.Entry<Item, Integer> entry : requiredItems.entrySet()) {
            int totalNeed = entry.getValue() * requiredQuantity;
            ItemController.updateItemQTY(entry.getKey().getId(), totalNeed, false);
        }

        System.out.println("All materials are available for the task!");
        return true;
    }

    public int getId() { return id; }

    public Product getRequiresdProduct() { return requiredProduct; }

    public void setRequiredProduct(Product requiresdProduct) { this.requiredProduct = requiresdProduct; }

    public int getRequiredQuantity() { return requiredQuantity; }

    public void setRequiredQuantity(int requiredQuantity) { this.requiredQuantity = requiredQuantity; }

    public String getClient() { return client; }

    public void setClient(String client) { this.client = client; }

    public LocalDate getStartDate() { return startDate; }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getDeliveryDate() { return deliveryDate; }

    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public double getProgress() { return progress; }

    public void setProgress(double progress) { this.progress = progress; }

    public ProductLine getAssignedLine() { return assignedLine; }

    public void setAssignedLine(ProductLine assignedLine) { this.assignedLine = assignedLine; }

    public int getProducedQuantity() {
        return producedQuantity;
    }

    public void setProducedQuantity(int producedQuantity) {
        this.producedQuantity = producedQuantity;
        setProgress(producedQuantity * 100.0 / requiredQuantity);
    }

    public Product getRequiredProduct() {
        return requiredProduct;
    }
}
