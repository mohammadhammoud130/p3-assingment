package model;

public class Item {
    private static int nextId = 1;
    private final int id;
    private String name;
    private Category category;
    private int quantity;
    private int minThreshold;
    private double price;

    public Item(String name, String category, double price, int quantity, int minThreshold) {
        this.id = nextId++;
        this.name = name;
        this.category = Category.valueOf(category);
        this.price = price;
        this.quantity = quantity;
        this.minThreshold = minThreshold;
    }
    public Item(int id, String name, String category, double price, int quantity, int minThreshold) {
        this.id = id;
        this.name = name;
        this.category = Category.valueOf(category);
        this.price = price;
        this.quantity = quantity;
        this.minThreshold = minThreshold;
    }

    public static void resetIdCounter(int newNextId) {
        nextId = newNextId;
    }

    public boolean isUnderMinThreshold() {
        return this.quantity < this.minThreshold;
    }


    public int getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty.");
        }
        this.name = name;
    }

    public Category getCategory() { return category; }

    public double getPrice() { return price; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    public int getMinThreshold() { return minThreshold; }

    @Override
    public String toString() {
        return "{ID :" + id +
                ", NAME :" + name +
                ", Category :" + category +
                ", Price :" + price +
                ", Quantity :" + quantity +
                ", minThreshold :" + minThreshold + "}";
    }
}
