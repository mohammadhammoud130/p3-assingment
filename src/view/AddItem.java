package view;

import controller.ProductLineController;
import model.Category;
import model.Item;
import model.ProductLine;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AddItem extends JPanel {
    public AddItem() {

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        //left panel
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBackground(new Color(225, 238, 245));

        JPanel up = new JPanel(null);
        up.setBackground(new Color(15, 70, 120));
        up.setBounds(0, 0, 800, 80);

        JLabel lblTitle = new JLabel("  Add New Item");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 35));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(30, 20, 400, 40);

        up.add(lblTitle);
        leftPanel.add(up);

        // Item Name
        JLabel lblName = new JLabel("Item Name");
        lblName.setFont(new Font("Arial", Font.PLAIN, 20));
        lblName.setBounds(160, 150, 200, 25);
        leftPanel.add(lblName);

        JTextField txtName = createField();
        txtName.setBounds(160, 185, 300, 35);
        leftPanel.add(txtName);

        // Price
        JLabel lblPrice = new JLabel("Price");
        lblPrice.setFont(new Font("Arial", Font.PLAIN, 20));
        lblPrice.setBounds(160, 235, 200, 25);
        leftPanel.add(lblPrice);

        JTextField txtPrice = createField();
        txtPrice.setBounds(160, 270, 300, 35);
        leftPanel.add(txtPrice);

        // Quantity
        JLabel lblQty = new JLabel("Quantity");
        lblQty.setFont(new Font("Arial", Font.PLAIN, 20));
        lblQty.setBounds(160, 320, 200, 25);
        leftPanel.add(lblQty);

        JTextField txtQty = createField();
        txtQty.setBounds(160, 355, 300, 35);
        leftPanel.add(txtQty);

        // Category
        JLabel lblCategory = new JLabel("Category");
        lblCategory.setFont(new Font("Arial", Font.PLAIN, 20));
        lblCategory.setBounds(160, 405, 200, 25);
        leftPanel.add(lblCategory);

        JCheckBox chkFabric = new JCheckBox("Fabric");
        JCheckBox chkThread = new JCheckBox("Thread");
        JCheckBox chkButton = new JCheckBox("Button");
        JCheckBox chkZipper = new JCheckBox("Zipper");
        JCheckBox chkLeather = new JCheckBox("Leather");

        JCheckBox[] boxes = {chkFabric, chkThread, chkButton, chkZipper, chkLeather};
        int x = 160, y = 440;

        for (JCheckBox box : boxes) {
            box.setFont(new Font("Arial", Font.PLAIN, 18));
            box.setBounds(x, y, 120, 25);
            leftPanel.add(box);
            x += 140;
            if (x > 420) {
                x = 160;
                y += 35;
            }
        }

        ButtonGroup categoryGroup = new ButtonGroup();
        for (JCheckBox box : boxes) categoryGroup.add(box);

        // Add Button
        JButton btnAdd = new JButton("Add Item");
        btnAdd.setBounds(160, 550, 300, 45);
        btnAdd.setBackground(new Color(0xF7941D));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 16));
        btnAdd.setFocusable(false);
        leftPanel.add(btnAdd);

        //pop windows
        btnAdd.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                int quantity = Integer.parseInt(txtQty.getText().trim());

                if (name.isEmpty()) throw new IllegalArgumentException("Name is required");

                Category category;
                if (chkFabric.isSelected()) category = Category.FABRIC;
                else if (chkThread.isSelected()) category = Category.THREAD;
                else if (chkButton.isSelected()) category = Category.BUTTON;
                else if (chkZipper.isSelected()) category = Category.ZIPPER;
                else if (chkLeather.isSelected()) category = Category.LEATHER;
                else throw new IllegalArgumentException("Please select a category");

                JOptionPane.showMessageDialog(this,
                        "Item added successfully!\nCategory: " + category,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                txtName.setText("");
                txtPrice.setText("");
                txtQty.setText("");
                categoryGroup.clearSelection();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        //right pinal
        JPanel up2 = new JPanel(null);
        up2.setBackground(new Color(15, 70, 120));
        up2.setBounds(0, 0, 800, 80);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(up2);

       ImageIcon icon = new ImageIcon(getClass().getResource("itemadd.png"));
      JLabel imageLabel = new JLabel(icon);
       imageLabel.setHorizontalAlignment(JLabel.CENTER);
       rightPanel.add(imageLabel, BorderLayout.CENTER);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JTextField createField() {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
}



