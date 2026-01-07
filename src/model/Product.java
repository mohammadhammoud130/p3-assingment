package model;

import java.util.HashMap;
import java.util.Map;

public class Product {
    private static int nextId = 1;   // shared counter
    private final int id;            // unique per instance
    private String name;
    private Map<Item, Integer> requiredItems = new HashMap<>();
    private double estimatedTime;

    // Constructor with parameters
    public Product(String name, HashMap<Item, Integer> requiredItems, double estimatedTime) {
        this.id = nextId++;
        this.name = name;
        this.requiredItems = requiredItems;
        this.estimatedTime = estimatedTime;
    }

    // Constructor for reloading from CSV
    public Product(int id, HashMap<Item, Integer> requiredItems, double estimatedTime) {
        this.id = id;
        this.requiredItems = requiredItems;
        this.estimatedTime = estimatedTime;
        if (id >= nextId) {
            nextId = id + 1; // keep counter in sync
        }
    }

    public static void resetIdCounter(int newNextId) {
        nextId = newNextId;
    }

    @Override
    public String toString() {
        StringBuilder itemsBuilder = new StringBuilder("[");
        if (requiredItems != null && !requiredItems.isEmpty()) {
            int count = 0;
            for (Map.Entry<Item, Integer> entry : requiredItems.entrySet()) {
                String itemName = (entry.getKey() != null) ? entry.getKey().getName() : "null";
                itemsBuilder.append("{")
                        .append(itemName)
                        .append(" : ")
                        .append(entry.getValue())
                        .append("}");
                if (++count < requiredItems.size()) {
                    itemsBuilder.append(", ");
                }
            }
        }
        itemsBuilder.append("]");

        return "{ID : " + id
                + ", Name : " + name
                + ", Required items : " + itemsBuilder.toString()
                + ", Estimated time : " + estimatedTime
                + "}";
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Map<Item, Integer> getRequiredItems() { return requiredItems; }

    public void setRequiredItems(HashMap<Item, Integer> requiredItems) { this.requiredItems = requiredItems; }

    public double getEstimatedTime() { return estimatedTime; }

    public void setEstimatedTime(double estimatedTime) { this.estimatedTime = estimatedTime; }

}
