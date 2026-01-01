package model;

public class Item {
    private static int nextId = 1;
    private final int id;
    private String name;
    private String category;
    private int quantity;
    private int minThreshold;
    private double price;

    // Normal constructor (auto ID)
    public Item(String name, String category, double price, int quantity, int minThreshold) {
        this.id = nextId++;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.minThreshold = minThreshold;
    }
    // Constructor for reloading from CSV
    public Item(int id, String name, String category, double price, int quantity, int minThreshold) {
        this.id = id;
        this.name = name;
        this.category = category;
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

    public String getCategory() { return category; }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Item category cannot be null or empty.");
        }
        this.category = category;
    }

    public double getPrice() { return price; }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = price;
    }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    public int getMinThreshold() { return minThreshold; }

    public void setMinThreshold(int minThreshold) {
        if (minThreshold < 0) {
            throw new IllegalArgumentException("Minimum threshold cannot be negative.");
        }
        this.minThreshold = minThreshold;
    }

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
