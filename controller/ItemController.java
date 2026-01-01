package controller;

import model.Item;

import java.io.*;
import java.util.*;

public class ItemController {
    private static HashMap<Integer, Item> items = new HashMap<>();
    private static final String itemsFilePath = "/data/Items.csv";

    static { items = itemsLoader(); }

    private static HashMap<Integer, Item> itemsLoader() {
        HashMap<Integer, Item> inventory = new HashMap<>();
        File file = new File(itemsFilePath);
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split(",");
                    if (fields.length < 6) continue;

                    int id = Integer.parseInt(fields[0]);
                    String name = fields[1];
                    String category = fields[2];
                    double price = Double.parseDouble(fields[3]);
                    int quantity = Integer.parseInt(fields[4]);
                    int minThreshold = Integer.parseInt(fields[5]);

                    Item item = new Item(id, name, category, price, quantity, minThreshold);
                    inventory.put(id, item);

                    if (id > maxId) maxId = id;
                } catch (Exception e) {
                    System.err.println("Error parsing item line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading inventory file: " + e.getMessage());
        }

        // EDITED: reset counter after loading
        Item.resetIdCounter(maxId + 1);

        return inventory;
    }

    public static void updateInventoryFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(itemsFilePath))) {
            writer.write("id,name,category,price,quantity,minThreshold");
            writer.newLine();

            for (Item item : items.values()) {
                if (item == null) continue;
                writer.write(item.getId() + "," + item.getName() + "," +
                        item.getCategory() + "," + item.getPrice() + "," +
                        item.getQuantity() + "," + item.getMinThreshold());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing inventory file: " + e.getMessage());
        }
    }

    public static void updateStock(int id, int value, boolean add) {
        int quantity= items.get(id).getQuantity();
        if (value <= 0) {
            throw new IllegalArgumentException("Update value must be positive.");
        }

        if (add) {
            int newQuantity = quantity + value;
            items.get(id).setQuantity(newQuantity);
        } else {
            if (quantity < value) {
                throw new IllegalArgumentException("Not enough stock to reduce for item: " + items.get(id).getName());
            }
           items.get(id).setQuantity(quantity - value);
        }
    }

    public Map<Integer, Item> itemsUnderMinThreshold() {
        Map<Integer, Item> itemsUnderMinThreshold = new HashMap<>();

        if (items == null) {
            ErrorLogger.logWarning("Inventory is not initialized!");
            throw new IllegalStateException("Inventory is not initialized!");
        }
        if (items.isEmpty()) {
            ErrorLogger.logWarning("Inventory is empty. No items to check.");
            return itemsUnderMinThreshold;
        }

        for (Map.Entry<Integer, Item> entry : items.entrySet()) {
            Item item = entry.getValue();
            if (item == null) {
                ErrorLogger.logWarning("Null item found in inventory for ID " + entry.getKey());
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
            ErrorLogger.logWarning("Inventory is not initialized!");
            throw new IllegalStateException("Inventory is not initialized!");
        }
        if (items.isEmpty()) {
            ErrorLogger.logWarning("Inventory is empty. No items to check.");
            return itemsOutOfStock;
        }

        for (Map.Entry<Integer, Item> entry : items.entrySet()) {
            Item item = entry.getValue();
            if (item == null) {
                ErrorLogger.logWarning("Null item found in inventory for ID " + entry.getKey());
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
            ErrorLogger.logWarning("Inventory is not initialized!");
            throw new IllegalStateException("Inventory is not initialized!");
        }
        if (items.isEmpty()) {
            ErrorLogger.logWarning("Inventory is empty. No items to check.");
            return availableItems;
        }

        for (Map.Entry<Integer, Item> entry : items.entrySet()) {
            Item item = entry.getValue();
            if (item == null) {
                ErrorLogger.logWarning("Null item found in inventory for ID " + entry.getKey());
                continue;
            }
            if (!item.isUnderMinThreshold() && item.getQuantity() > 0) {
                availableItems.put(entry.getKey(), item);
            }
        }
        return availableItems;
    }

    public void addItem (Item item) {
        items.put(item.getId(), item);

        try(BufferedWriter itemAdder = new BufferedWriter(new FileWriter(itemsFilePath,true))){
            itemAdder.write(item.getId()+","
                    +item.getName()+","
                    +item.getCategory()+","
                    +item.getPrice()+","
                    +item.getQuantity()+","
                    +item.getMinThreshold()+"\n");


        }catch(FileNotFoundException e){
            System.err.println("File not found: " + itemsFilePath);
        }catch(IOException e){
            System.err.println("Error opening file: " + itemsFilePath);
        }catch(SecurityException e){
            System.err.println("No permission to open file: " + itemsFilePath);
        }
    }

    public void deleteItemById(int id) {
        if(items.containsKey(id)){
            items.remove(id);
            System.out.println(" Deleted Successfully ");;}
        else {
            System.out.println("Deletion Failed ");}
    }

    public void displayItems() {
        if(items.isEmpty()){
            System.out.println("THE STORAGE IS EMPTY!");
            return;
        }
        else {
            for (Item item:items.values()){
                System.out.println(item);}     //item.toString()تلقائي
        }
    }

    public void searchItemByName(String name) {
        for (Item item : items.values()) {
            if (item.getName().equalsIgnoreCase(name)) {
                System.out.println(item);
                return;
            }
        }
        System.out.println(" SEARCH FAILED! ");
    }

    public void searchItemByCategory(String category){
        boolean found = false;
        for (Item item : items.values())
        {
            if (item.getCategory().equalsIgnoreCase(category)) {
                System.out.println(item);
                found = true;
            }
        }
        if(!found) {
            System.out.println("SEARCH FAILED!");
        }}

    public static void updateItemQTY(int id, int updateValue, boolean add) {
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("Inventory is empty! Cannot update item quantity.");
        }
        Item item = items.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Item with ID " + id + " not found in inventory.");
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
                    int currentId = Integer.parseInt(fields[0]);
                    if (currentId == id) {
                        String updatedLine = item.getId() + ","
                                + item.getName() + ","
                                + item.getCategory() + ","
                                + item.getPrice() + ","
                                + item.getQuantity() + ","
                                + item.getMinThreshold();
                        lines.add(updatedLine);
                    } else {
                        lines.add(line);
                    }
                } else {
                    lines.add(line);
                }
            }
        }catch (FileNotFoundException e) {
            System.err.println("File not found: " + itemsFilePath);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("No permission to read file: " + itemsFilePath);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(itemsFilePath))) {
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + itemsFilePath);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("No permission to write file: " + itemsFilePath);
        }

        System.out.println("Quantity updated for item: " + item.getName() + " → New quantity: " + item.getQuantity());
    }

    public static HashMap<Integer, Item> getItems() {return items;}

}
