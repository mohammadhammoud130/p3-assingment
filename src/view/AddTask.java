package view;

import controller.ProductLineController;
import controller.TaskController;
import controller.ProductController;
import controller.MainController;
import model.Product;
import model.Task;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddTask {

    private static final Color INDUSTRIAL_BLUE = new Color(0, 74, 124);
    private static final Color ACTIVE_ORANGE = new Color(247, 148, 29);
    private static final Color NEUTRAL_GREY = new Color(122, 139, 153);
    private static final Color CLEAN_WHITE = Color.WHITE;

    public static void show(int productionLineId) {
        JDialog dialog = new JDialog((Frame) null, "Create New Task", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // --- LEFT PANEL ---
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBackground(new Color(225, 238, 245));

        JPanel header = new JPanel(null);
        header.setBackground(INDUSTRIAL_BLUE);
        header.setBounds(0, 0, 400, 80);

        JLabel title = new JLabel("CREATE NEW TASK");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(CLEAN_WHITE);
        title.setBounds(20, 20, 300, 40);
        header.add(title);
        leftPanel.add(header);

        // Client
        JLabel lblClient = new JLabel("Client Name:");
        lblClient.setFont(new Font("Arial", Font.BOLD, 14));
        lblClient.setBounds(40, 120, 100, 20);
        leftPanel.add(lblClient);

        JTextField clientField = new JTextField();
        clientField.setBounds(40, 145, 300, 30);
        leftPanel.add(clientField);

        // Product (Custom Renderer)
        JLabel lblProduct = new JLabel("Required Product:");
        lblProduct.setFont(new Font("Arial", Font.BOLD, 14));
        lblProduct.setBounds(40, 190, 150, 20);
        leftPanel.add(lblProduct);

        JComboBox<Product> productCombo = new JComboBox<>();
        // Set Custom Renderer to show ONLY the name
        productCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    setText(((Product) value).getName());
                }
                return this;
            }
        });

        if (ProductController.getProducts() != null) {
            for (Product p : ProductController.getProducts().values()) {
                productCombo.addItem(p);
            }
        }
        productCombo.setBounds(40, 215, 300, 30);
        leftPanel.add(productCombo);

        // Quantity
        JLabel lblQty = new JLabel("Quantity:");
        lblQty.setFont(new Font("Arial", Font.BOLD, 14));
        lblQty.setBounds(40, 260, 100, 20);
        leftPanel.add(lblQty);

        JTextField qtyField = new JTextField();
        qtyField.setBounds(40, 285, 300, 30);
        leftPanel.add(qtyField);

        // Delivery Date (Start Date Removed)
        JLabel lblEnd = new JLabel("Delivery Date (YYYY-MM-DD):");
        lblEnd.setFont(new Font("Arial", Font.BOLD, 14));
        lblEnd.setBounds(40, 330, 250, 20);
        leftPanel.add(lblEnd);

        JTextField endField = new JTextField();
        endField.setBounds(40, 355, 300, 30);
        leftPanel.add(endField);

        // Buttons
        JButton btnAdd = new JButton("ADD TASK");
        btnAdd.setBackground(ACTIVE_ORANGE);
        btnAdd.setForeground(CLEAN_WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
        btnAdd.setFocusPainted(false);
        btnAdd.setBounds(40, 420, 140, 40);
        leftPanel.add(btnAdd);

        JButton btnCancel = new JButton("CANCEL");
        btnCancel.setBackground(NEUTRAL_GREY);
        btnCancel.setForeground(CLEAN_WHITE);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancel.setFocusPainted(false);
        btnCancel.setBounds(200, 420, 140, 40);
        leftPanel.add(btnCancel);

        // --- RIGHT PANEL ---
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(CLEAN_WHITE);

        JPanel headerRight = new JPanel(null);
        headerRight.setBackground(INDUSTRIAL_BLUE);
        headerRight.setPreferredSize(new Dimension(400, 80));
        imagePanel.add(headerRight, BorderLayout.NORTH);

        JLabel machineLabel = new JLabel();
        machineLabel.setHorizontalAlignment(JLabel.CENTER);

        ImageIcon icon = new ImageIcon("assets/cloths.jpg");
        Image img = icon.getImage().getScaledInstance(300, 200, Image.SCALE_DEFAULT);
        machineLabel.setIcon(new ImageIcon(img));
        imagePanel.add(machineLabel, BorderLayout.CENTER);

        // --- Logic ---
        btnAdd.addActionListener(e -> {
            try {
                String client = clientField.getText().trim();
                if (client.isEmpty()) throw new IllegalArgumentException("Client name is required.");

                Product selectedProduct = (Product) productCombo.getSelectedItem();
                if (selectedProduct == null) throw new IllegalArgumentException("Please select a product.");

                int quantity = Integer.parseInt(qtyField.getText().trim());
                if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive.");

                // Start Date is AUTOMATICALLY set to NOW
                LocalDate startDate = LocalDate.now();
                LocalDate endDate = LocalDate.parse(endField.getText().trim());

                if (endDate.isBefore(startDate)) throw new IllegalArgumentException("Delivery date cannot be in the past.");

                Task newTask = new Task(selectedProduct, quantity, client, startDate, endDate, "ACTIVE", ProductLineController.getProductLines().get(productionLineId));

                TaskController.addTask(productionLineId, newTask);

                JOptionPane.showMessageDialog(dialog, "Task added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

                JFrame mainFrame = MainController.getFrame();
                if (mainFrame != null) {
                    model.ProductLine updatedLine = controller.ProductLineController.getProductLines().get(productionLineId);
                    if (updatedLine != null) {
                        TaskView.showTasksForLine(mainFrame, DashBoard.getLeftPanel(), updatedLine);
                    }
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number format for Quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        mainPanel.add(leftPanel);
        mainPanel.add(imagePanel);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}
