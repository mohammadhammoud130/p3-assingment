package controller;

import model.Product;
import model.Item;

import java.io.*;
import java.util.*;

public class ProductController {
    private static HashMap<Integer, Product> products = new HashMap<>();
    private static final String productsFilePath = "/data/Products.csv";

    static { products = productsLoader(); }

    private static HashMap<Integer, Product> productsLoader() {
        HashMap<Integer, Product> loadedProducts = new HashMap<>();
        File file = new File(productsFilePath);
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split(",");
                    if (fields.length < 4) continue;

                    int id = Integer.parseInt(fields[0]);
                    String name = fields[1];
                    double estimatedTime = Double.parseDouble(fields[2]);

                    // EDITED: parse requiredItems as itemId:qty pairs
                    HashMap<Item,Integer> requiredItems = new HashMap<>();
                    String[] itemPairs = fields[3].split(";");
                    for (String pair : itemPairs) {
                        if (pair.isEmpty()) continue;
                        String[] parts = pair.split(":");
                        int itemId = Integer.parseInt(parts[0]);
                        int qty = Integer.parseInt(parts[1]);
                        Item item = ItemController.getItems().get(itemId);
                        if (item != null) requiredItems.put(item, qty);
                    }

                    Product product = new Product(id, requiredItems, estimatedTime);
                    product.setName(name);
                    loadedProducts.put(id, product);

                    if (id > maxId) maxId = id;
                } catch (Exception e) {
                    System.err.println("Error parsing product line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Products file: " + e.getMessage());
        }

        // EDITED: reset counter after loading
        Product.resetIdCounter(maxId + 1);

        return loadedProducts;
    }

    public static void updateProductsFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(productsFilePath))) {
            writer.write("id,name,estimatedTime,requiredItems");
            writer.newLine();

            for (Product product : products.values()) {
                if (product == null) continue;

                // EDITED: write requiredItems as itemId:qty pairs
                StringBuilder requiredItemsStr = new StringBuilder();
                for (Map.Entry<Item,Integer> entry : product.getRequiredItems().entrySet()) {
                    requiredItemsStr.append(entry.getKey().getId())
                            .append(":")
                            .append(entry.getValue())
                            .append(";");
                }

                writer.write(product.getId() + "," + product.getName() + "," +
                        product.getEstimatedTime() + "," + requiredItemsStr);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing Products file: " + e.getMessage());
        }
    }

    // EDITED: add method for Product
    public static void addProduct(Product product) {
        products.put(product.getId(), product);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(productsFilePath, true))) {
            StringBuilder requiredItemsStr = new StringBuilder();
            for (Map.Entry<Item,Integer> entry : product.getRequiredItems().entrySet()) {
                requiredItemsStr.append(entry.getKey().getId())
                        .append(":")
                        .append(entry.getValue())
                        .append(";");
            }

            writer.write(product.getId() + "," +
                    product.getName() + "," +
                    product.getEstimatedTime() + "," +
                    requiredItemsStr + "\n");
        } catch (Exception e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
    }

    public static HashMap<Integer, Product> getProducts() { return products; }
}
