package controller;
import model.Product;

import model.Item;

import java.io.*;
import java.util.*;

public class ProductController {
    private static HashMap<Integer, Product> products = new HashMap<>();
    private static final String productsFilePath = "data/Products.csv";

    public static HashMap<Integer, Product> productsLoader() {
        HashMap<Integer, Product> loadedProducts = new HashMap<>();
        File file = new File(productsFilePath);
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split(",");
                    if (fields.length < 4) {
                        System.err.println("Invalid product line: " + line);
                        ErrorLogger.logWarning("Invalid product line: " + line);
                        continue;
                    }

                    int id = Integer.parseInt(fields[0].trim());
                    String name = fields[1].trim();
                    double estimatedTime = Double.parseDouble(fields[2].trim());

                    HashMap<Item, Integer> requiredItems = new HashMap<>();
                    String[] itemPairs = fields[3].split(";");

                    for (String pair : itemPairs) {
                        if (pair.isEmpty()) continue;

                        try {
                            String[] parts = pair.split(":");
                            int itemId = Integer.parseInt(parts[0].trim());
                            int qty = Integer.parseInt(parts[1].trim());

                            Item item = ItemController.getItems().get(itemId);
                            if (item == null) {
                                String msg = "Referenced item ID " + itemId + " not found for product ID " + id;
                                System.err.println(msg);
                                ErrorLogger.logWarning(msg);
                                continue;
                            }

                            requiredItems.put(item, qty);

                        } catch (Exception e) {
                            System.err.println("Invalid requiredItem pair in product line: " + pair);
                            ErrorLogger.logWarning("Invalid requiredItem pair in product line: " + pair);
                        }
                    }

                    Product product = new Product(id, requiredItems, estimatedTime);
                    product.setName(name);
                    loadedProducts.put(id, product);

                    if (id > maxId) maxId = id;

                } catch (NumberFormatException e) {
                    System.err.println("Number format error in product line: " + line);
                    ErrorLogger.logWarning("Number format error in product line: " + line);
                } catch (NullPointerException e) {
                    System.err.println("Null value found in product line: " + line);
                    ErrorLogger.logWarning("Null value found in product line: " + line);
                } catch (Exception e) {
                    System.err.println("Unexpected error parsing product line: " + line);
                    ErrorLogger.logWarning("Unexpected error parsing product line: " + line);
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("Products file not found: " + productsFilePath);
            ErrorLogger.logWarning("Products file not found: " + productsFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to read Products file: " + productsFilePath);
            ErrorLogger.logWarning("No permission to read Products file: " + productsFilePath);
        } catch (IOException e) {
            System.err.println("Error reading Products file: " + e.getMessage());
            ErrorLogger.logWarning("Error reading Products file: " + e.getMessage());
        }

        Product.resetIdCounter(maxId + 1);
        return loadedProducts;
    }

    public static void updateProductsFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(productsFilePath))) {

            writer.write("id,name,estimatedTime,requiredItems");
            writer.newLine();

            for (Product product : products.values()) {
                if (product == null) continue;

                StringBuilder requiredItemsStr = new StringBuilder();
                for (Map.Entry<Item, Integer> entry : product.getRequiredItems().entrySet()) {
                    requiredItemsStr.append(entry.getKey().getId())
                            .append(":")
                            .append(entry.getValue())
                            .append(";");
                }

                writer.write(product.getId() + "," +
                        product.getName() + "," +
                        product.getEstimatedTime() + "," +
                        requiredItemsStr);
                writer.newLine();
            }

        } catch (FileNotFoundException e) {
            System.err.println("Products file not found when writing: " + productsFilePath);
            ErrorLogger.logWarning("Products file not found when writing: " + productsFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to write Products file: " + productsFilePath);
            ErrorLogger.logWarning("No permission to write Products file: " + productsFilePath);
        } catch (IOException e) {
            System.err.println("Error writing Products file: " + e.getMessage());
            ErrorLogger.logWarning("Error writing Products file: " + e.getMessage());
        }
    }

    public static void addProduct(Product product) {
        products.put(product.getId(), product);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(productsFilePath, true))) {

            StringBuilder requiredItemsStr = new StringBuilder();
            for (Map.Entry<Item, Integer> entry : product.getRequiredItems().entrySet()) {
                requiredItemsStr.append(entry.getKey().getId())
                        .append(":")
                        .append(entry.getValue())
                        .append(";");
            }

            writer.write(product.getId() + "," +
                    product.getName() + "," +
                    product.getEstimatedTime() + "," +
                    requiredItemsStr + "\n");

        } catch (FileNotFoundException e) {
            System.err.println("Products file not found when adding product: " + productsFilePath);
            ErrorLogger.logWarning("Products file not found when adding product: " + productsFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to write Products file when adding product: " + productsFilePath);
            ErrorLogger.logWarning("No permission to write Products file when adding product: " + productsFilePath);
        } catch (IOException e) {
            System.err.println("Error adding product: " + e.getMessage());
            ErrorLogger.logWarning("Error adding product: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error adding product: " + e.getMessage());
            ErrorLogger.logWarning("Unexpected error adding product: " + e.getMessage());
        }
    }

    public static HashMap<Integer, Product> getProducts() {
        return products;
    }

    public static void setProducts(HashMap<Integer, Product> products) {
        ProductController.products = products;
    }
}
