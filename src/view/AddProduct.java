package view;

import controller.ItemController;
import controller.ProductController;
import controller.MainController;
import model.Item;
import model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;

public class AddProduct {

    private static final Color INDUSTRIAL_BLUE = new Color(0, 74, 124);
    private static final Color ACTIVE_ORANGE = new Color(247, 148, 29);
    private static final Color CLEAN_WHITE = Color.WHITE;

    public static void show() {
        JDialog dialog = new JDialog((Frame) null, "Create New Product", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // --- LEFT PANEL (Form) ---
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBackground(new Color(225, 238, 245));

        JPanel header = new JPanel(null);
        header.setBackground(INDUSTRIAL_BLUE);
        header.setBounds(0, 0, 400, 80);

        JLabel title = new JLabel("CREATE NEW PRODUCT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(CLEAN_WHITE);
        title.setBounds(20, 20, 350, 40);
        header.add(title);
        leftPanel.add(header);

        JLabel lblName = new JLabel("Product Name:");
        lblName.setFont(new Font("Arial", Font.BOLD, 14));
        lblName.setBounds(40, 150, 150, 20);
        leftPanel.add(lblName);

        JTextField nameField = new JTextField();
        nameField.setBounds(40, 175, 300, 30);
        leftPanel.add(nameField);

        JLabel lblTime = new JLabel("Estimated Time (Min):");
        lblTime.setFont(new Font("Arial", Font.BOLD, 14));
        lblTime.setBounds(40, 220, 200, 20);
        leftPanel.add(lblTime);

        JTextField timeField = new JTextField();
        timeField.setBounds(40, 245, 300, 30);
        leftPanel.add(timeField);

        JButton btnAdd = new JButton("NEXT: SELECT MATERIALS");
        btnAdd.setBackground(ACTIVE_ORANGE);
        btnAdd.setForeground(CLEAN_WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        btnAdd.setFocusPainted(false);
        btnAdd.setBounds(40, 350, 300, 40);
        leftPanel.add(btnAdd);

        JButton btnCancel = new JButton("CANCEL");
        btnCancel.setBackground(Color.GRAY);
        btnCancel.setForeground(CLEAN_WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setBounds(115, 410, 150, 30);
        leftPanel.add(btnCancel);

        // --- RIGHT PANEL (Image) ---
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(CLEAN_WHITE);

        JPanel headerRight = new JPanel(null);
        headerRight.setBackground(INDUSTRIAL_BLUE);
        headerRight.setPreferredSize(new Dimension(400, 80));
        imagePanel.add(headerRight, BorderLayout.NORTH);

        JLabel productLabel = new JLabel();
        productLabel.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon icon = new ImageIcon("assets/products.jpg");
        Image img = icon.getImage().getScaledInstance(300, 300, Image.SCALE_DEFAULT);
        productLabel.setIcon(new ImageIcon(img));
        imagePanel.add(productLabel, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            String name = nameField.getText().trim();
            String timeStr = timeField.getText().trim();

            if (name.isEmpty() || timeStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double time = Double.parseDouble(timeStr);
                if (time <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Time must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                showMaterialSelection(dialog, name, time);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter numeric values for Time.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        mainPanel.add(leftPanel);
        mainPanel.add(imagePanel);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private static void showMaterialSelection(JDialog parentDialog, String name, double time) {
        JDialog matDialog = new JDialog(parentDialog, "Select Required Materials", true);
        matDialog.setSize(600, 500);
        matDialog.setLocationRelativeTo(parentDialog);
        matDialog.setLayout(new BorderLayout(15, 15));

        JLabel infoLabel = new JLabel("Select raw materials required to produce ONE unit of " + name, JLabel.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        matDialog.add(infoLabel, BorderLayout.NORTH);

        // Main container for list items (BoxLayout Y_AXIS)
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(CLEAN_WHITE);

        if (ItemController.getItems() != null && !ItemController.getItems().isEmpty()) {
            for (Item item : ItemController.getItems().values()) {
                // Row Panel (FlowLayout Left)
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
                row.setBackground(CLEAN_WHITE);
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

                // Important: Set Maximum Size to prevent BoxLayout from squishing it vertically
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

                JCheckBox cb = new JCheckBox(item.getName());
                cb.setFont(new Font("Arial", Font.PLAIN, 14));
                cb.setOpaque(false);
                cb.setPreferredSize(new Dimension(200, 30)); // Give checkbox explicit width

                JTextField qtyInput = new JTextField(5);
                qtyInput.setEnabled(false);
                qtyInput.setText("1");

                cb.addActionListener(e -> qtyInput.setEnabled(cb.isSelected()));

                JLabel lblAmount = new JLabel("Amount Needed:");
                lblAmount.setFont(new Font("Arial", Font.PLAIN, 12));

                row.add(cb);
                row.add(lblAmount);
                row.add(qtyInput);

                // Store references
                cb.putClientProperty("itemRef", item);
                cb.putClientProperty("qtyField", qtyInput);

                listPanel.add(row);
            }
        } else {
            JLabel emptyLabel = new JLabel("No items found in storage to select.");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalGlue());
            listPanel.add(emptyLabel);
            listPanel.add(Box.createVerticalGlue());
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        matDialog.add(scrollPane, BorderLayout.CENTER);

        JButton btnSave = new JButton("CONFIRM & SAVE PRODUCT");
        btnSave.setBackground(INDUSTRIAL_BLUE);
        btnSave.setForeground(CLEAN_WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(200, 50));

        btnSave.addActionListener(e -> {
            HashMap<Item, Integer> selectedItems = new HashMap<>();

            for (Component comp : listPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    // Check if it's a valid row with components
                    JPanel row = (JPanel) comp;
                    if (row.getComponentCount() > 0 && row.getComponent(0) instanceof JCheckBox) {
                        JCheckBox cb = (JCheckBox) row.getComponent(0);
                        if (cb.isSelected()) {
                            Item item = (Item) cb.getClientProperty("itemRef");
                            JTextField qField = (JTextField) cb.getClientProperty("qtyField");

                            try {
                                int qty = Integer.parseInt(qField.getText().trim());
                                if (qty <= 0) throw new NumberFormatException();
                                selectedItems.put(item, qty);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(matDialog, "Invalid quantity for material: " + item.getName());
                                return;
                            }
                        }
                    }
                }
            }

            if (selectedItems.isEmpty()) {
                JOptionPane.showMessageDialog(matDialog, "Please select at least one material.");
                return;
            }

            Product newProduct = new Product(name, selectedItems, time);
            ProductController.addProduct(newProduct);

            JOptionPane.showMessageDialog(matDialog, "Product created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            matDialog.dispose();
            parentDialog.dispose();

            JFrame mainFrame = MainController.getFrame();
            if (mainFrame != null) {
                Storage.storage(mainFrame, DashBoard.getLeftPanel());
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnSave);
        matDialog.add(btnPanel, BorderLayout.SOUTH);

        matDialog.setVisible(true);
    }
}
