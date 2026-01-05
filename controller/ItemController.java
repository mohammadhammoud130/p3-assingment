package controller;

import model.Item;

import java.io.*;
import java.util.*;

public class ItemController {
    private static HashMap<Integer, Item> items = new HashMap<>();
    private static final String itemsFilePath = "/data/Items.csv";

    public static HashMap<Integer, Item> itemsLoader() {
        HashMap<Integer, Item> inventory = new HashMap<>();
        File file = new File(itemsFilePath);
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split(",");
                    if (fields.length < 6) {
                        System.err.println("Invalid item line (not enough fields): " + line);
                        ErrorLogger.logWarning("Invalid item line (not enough fields): " + line);
                        continue;
                    }

                    int id = Integer.parseInt(fields[0].trim());
                    String name = fields[1].trim();
                    String category = fields[2].trim();
                    double price = Double.parseDouble(fields[3].trim());
                    int quantity = Integer.parseInt(fields[4].trim());
                    int minThreshold = Integer.parseInt(fields[5].trim());

                    Item item = new Item(id, name, category, price, quantity, minThreshold);
                    inventory.put(id, item);

                    if (id > maxId) maxId = id;

                } catch (NumberFormatException e) {
                    System.err.println("Number format error in item line: " + line);
                    ErrorLogger.logWarning("Number format error in item line: " + line);
                } catch (NullPointerException e) {
                    System.err.println("Null value found in item line: " + line);
                    ErrorLogger.logWarning("Null value found in item line: " + line);
                } catch (Exception e) {
                    System.err.println("Unexpected error parsing item line: " + line);
                    ErrorLogger.logWarning("Unexpected error parsing item line: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Items file not found: " + itemsFilePath);
            ErrorLogger.logWarning("Items file not found: " + itemsFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to read items file: " + itemsFilePath);
            ErrorLogger.logWarning("No permission to read items file: " + itemsFilePath);
        } catch (IOException e) {
            System.err.println("Error reading items file: " + e.getMessage());
            ErrorLogger.logWarning("Error reading items file: " + e.getMessage());
        }

        Item.resetIdCounter(maxId + 1);
        return inventory;
    }

    public static void updateItemsFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(itemsFilePath))) {
            writer.write("id,name,category,price,quantity,minThreshold");
            writer.newLine();

            for (Item item : items.values()) {
                if (item == null) continue;
                writer.write(item.getId() + "," +
                        item.getName() + "," +
                        item.getCategory() + "," +
                        item.getPrice() + "," +
                        item.getQuantity() + "," +
                        item.getMinThreshold());
                writer.newLine();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Items file not found when writing: " + itemsFilePath);
            ErrorLogger.logWarning("Items file not found when writing: " + itemsFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to write items file: " + itemsFilePath);
            ErrorLogger.logWarning("No permission to write items file: " + itemsFilePath);
        } catch (IOException e) {
            System.err.println("Error writing inventory file: " + e.getMessage());
            ErrorLogger.logWarning("Error writing inventory file: " + e.getMessage());
        }
    }

    public static void updateStock(int id, int value, boolean add) {
        if (value <= 0) {
            String msg = "Update value must be positive. Given: " + value;
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalArgumentException(msg);
        }

        Item item = items.get(id);
        if (item == null) {
            String msg = "Item with ID " + id + " not found for stock update.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalArgumentException(msg);
        }

        int quantity = item.getQuantity();

        if (add) {
            item.setQuantity(quantity + value);
        } else {
            if (quantity < value) {
                String msg = "Not enough stock to reduce for item: " + item.getName();
                System.err.println(msg);
                ErrorLogger.logWarning(msg);
                throw new IllegalArgumentException(msg);
            }
            item.setQuantity(quantity - value);
        }
    }

    public void addItem(Item item) {
        items.put(item.getId(), item);

        try (BufferedWriter itemAdder = new BufferedWriter(new FileWriter(itemsFilePath, true))) {
            itemAdder.write(item.getId() + "," +
                    item.getName() + "," +
                    item.getCategory() + "," +
                    item.getPrice() + "," +
                    item.getQuantity() + "," +
                    item.getMinThreshold() + "\n");
        } catch (FileNotFoundException e) {
            System.err.println("File not found when adding item: " + itemsFilePath);
            ErrorLogger.logWarning("File not found when adding item: " + itemsFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to open file when adding item: " + itemsFilePath);
            ErrorLogger.logWarning("No permission to open file when adding item: " + itemsFilePath);
        } catch (IOException e) {
            System.err.println("Error opening file when adding item: " + itemsFilePath + " | " + e.getMessage());
            ErrorLogger.logWarning("Error opening file when adding item: " + itemsFilePath + " | " + e.getMessage());
        }
    }

    public Map<Integer, Item> itemsUnderMinThreshold() {
        Map<Integer, Item> itemsUnderMinThreshold = new HashMap<>();

        if (items == null) {
            String msg = "Inventory is not initialized.";
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }
        if (items.isEmpty()) {
            ErrorLogger.logWarning("Inventory is empty. No items to check for min threshold.");
            return itemsUnderMinThreshold;
        }

        for (Map.Entry<Integer, Item> entry : items.entrySet()) {
            Item item = entry.getValue();
            if (item == null) {
                String msg = "Null item found in inventory for ID " + entry.getKey();
                System.err.println(msg);
                ErrorLogger.logWarning(msg);
                continue;
            }
            if (item.isUnderMinThreshold()) {
                itemsUnderMinThreshold.put(entry.getKey(), item);
            }
        }
        return itemsUnderMinThreshold;
    }

    public Map<Integer, Item> itemsOutOfStock() {
        Map<Integer, Item> itemsOutOfStock = new HashMap<>();

        if (items == null) {
            String msg = "Inventory is not initialized.";
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }
        if (items.isEmpty()) {
            ErrorLogger.logWarning("Inventory is empty. No items to check for out of stock.");
            return itemsOutOfStock;
        }

        for (Map.Entry<Integer, Item> entry : items.entrySet()) {
            Item item = entry.getValue();
            if (item == null) {
                String msg = "Null item found in inventory for ID " + entry.getKey();
                System.err.println(msg);
                ErrorLogger.logWarning(msg);
                continue;
            }
            if (item.getQuantity() <= 0) {
                itemsOutOfStock.put(entry.getKey(), item);
            }
        }
        return itemsOutOfStock;
    }

    public Map<Integer, Item> availableItems() {
        HashMap<Integer, Item> availableItems = new HashMap<>();

        if (items == null) {
            String msg = "Inventory is not initialized.";
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }
        if (items.isEmpty()) {
            ErrorLogger.logWarning("Inventory is empty. No items to check for availability.");
            return availableItems;
        }

        for (Map.Entry<Integer, Item> entry : items.entrySet()) {
            Item item = entry.getValue();
            if (item == null) {
                String msg = "Null item found in inventory for ID " + entry.getKey();
                System.err.println(msg);
                ErrorLogger.logWarning(msg);
                continue;
            }
            if (!item.isUnderMinThreshold() && item.getQuantity() > 0) {
                availableItems.put(entry.getKey(), item);
            }
        }
        return availableItems;
    }

    public void deleteItemById(int id) {
        if (items.containsKey(id)) {
            items.remove(id);
            System.out.println("Deleted Successfully.");
        } else {
            String msg = "Deletion Failed: Item with ID " + id + " not found.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
        }
    }

    public void displayItems() {
        if (items.isEmpty()) {
            System.out.println("THE STORAGE IS EMPTY!");
            return;
        }
        for (Item item : items.values()) {
            System.out.println(item);
        }
    }

    public void searchItemByName(String name) {
        for (Item item : items.values()) {
            if (item.getName().equalsIgnoreCase(name)) {
                System.out.println(item);
                return;
            }
        }
        String msg = "SEARCH FAILED: No item with name \"" + name + "\".";
        System.err.println(msg);
        ErrorLogger.logWarning(msg);
    }

    public void searchItemByCategory(String category) {
        boolean found = false;
        for (Item item : items.values()) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                System.out.println(item);
                found = true;
            }
        }
        if (!found) {
            String msg = "SEARCH FAILED: No items in category \"" + category + "\".";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
        }
    }

    public static void updateItemQTY(int id, int updateValue, boolean add) {
        if (items == null || items.isEmpty()) {
            String msg = "Inventory is empty! Cannot update item quantity.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalStateException(msg);
        }

        Item item = items.get(id);
        if (item == null) {
            String msg = "Item with ID " + id + " not found in inventory.";
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalArgumentException(msg);
        }

        updateStock(id, updateValue, add);

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(itemsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("id,")) {
                    lines.add(line);
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length >= 6) {
                    int currentId = Integer.parseInt(fields[0].trim());
                    if (currentId == id) {
                        String updatedLine = item.getId() + "," +
                                item.getName() + "," +
                                item.getCategory() + "," +
                                item.getPrice() + "," +
                                item.getQuantity() + "," +
                                item.getMinThreshold();
                        lines.add(updatedLine);
                    } else {
                        lines.add(line);
                    }
                } else {
                    lines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found when updating item quantity: " + itemsFilePath);
            ErrorLogger.logWarning("File not found when updating item quantity: " + itemsFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to read file when updating item quantity: " + itemsFilePath);
            ErrorLogger.logWarning("No permission to read file when updating item quantity: " + itemsFilePath);
        } catch (IOException e) {
            System.err.println("Error reading file when updating item quantity: " + e.getMessage());
            ErrorLogger.logWarning("Error reading file when updating item quantity: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(itemsFilePath))) {
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found when writing updated items: " + itemsFilePath);
            ErrorLogger.logWarning("File not found when writing updated items: " + itemsFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to write file when updating item quantity: " + itemsFilePath);
            ErrorLogger.logWarning("No permission to write file when updating item quantity: " + itemsFilePath);
        } catch (IOException e) {
            System.err.println("Error writing file when updating item quantity: " + e.getMessage());
            ErrorLogger.logWarning("Error writing file when updating item quantity: " + e.getMessage());
        }

        System.out.println("Quantity updated for item: " + item.getName() + " → New quantity: " + item.getQuantity());
    }

    public static HashMap<Integer, Item> getItems() {
        return items;
    }

    public static void setItems(HashMap<Integer, Item> items) {
        ItemController.items = items;
    }
}
