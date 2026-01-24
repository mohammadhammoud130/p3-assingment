package view;

import controller.MainController;
import controller.ProductLineController;
import model.ProductLine;
import model.Status;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AddProductionLine {

    // --- Main Entry Point ---

    public static void show() {
        JDialog dialog = new JDialog((Frame) null, "Add New Production Line", true);
        dialog.setSize(800, 550);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // --- LEFT PANEL (Form) ---
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBackground(new Color(225, 238, 245));

        JPanel header = new JPanel(null);
        header.setBackground(new Color(15, 70, 120));
        header.setBounds(0, 0, 400, 80);

        JLabel lblTitle = new JLabel("New Production Line", JLabel.LEFT);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.white);
        lblTitle.setBounds(30, 20, 350, 40);
        header.add(lblTitle);
        leftPanel.add(header);

        JLabel lblName = new JLabel("Line Name");
        lblName.setFont(new Font("Arial", Font.PLAIN, 20));
        lblName.setBounds(50, 150, 200, 25);
        leftPanel.add(lblName);

        JTextField txtName = new JTextField();
        txtName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtName.setBounds(50, 180, 300, 35);
        leftPanel.add(txtName);

        JLabel lblStatus = new JLabel("Status");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 20));
        lblStatus.setBounds(50, 240, 100, 25);
        leftPanel.add(lblStatus);

        JCheckBox chkActive = new JCheckBox("Active");
        chkActive.setFont(new Font("Arial", Font.PLAIN, 18));
        chkActive.setBounds(50, 270, 100, 25);
        chkActive.setOpaque(false);

        JCheckBox chkPaused = new JCheckBox("Paused");
        chkPaused.setFont(new Font("Arial", Font.PLAIN, 18));
        chkPaused.setBounds(150, 270, 100, 25);
        chkPaused.setOpaque(false);

        JCheckBox chkMaint = new JCheckBox("Maintenance");
        chkMaint.setFont(new Font("Arial", Font.PLAIN, 18));
        chkMaint.setBounds(250, 270, 140, 25);
        chkMaint.setOpaque(false);

        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(chkActive);
        statusGroup.add(chkPaused);
        statusGroup.add(chkMaint);

        leftPanel.add(chkActive);
        leftPanel.add(chkPaused);
        leftPanel.add(chkMaint);

        JButton buttonAddLine = new JButton("Add Production Line");
        buttonAddLine.setFont(new Font("Arial", Font.BOLD, 15));
        buttonAddLine.setBounds(50, 350, 300, 45);
        buttonAddLine.setForeground(Color.white);
        buttonAddLine.setFocusable(false);
        buttonAddLine.setBackground(new Color(0xF7941D));
        leftPanel.add(buttonAddLine);

        // --- RIGHT PANEL (Image) ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.white);

        JPanel headerRight = new JPanel(null);
        headerRight.setBackground(new Color(15, 70, 120));
        headerRight.setPreferredSize(new Dimension(400, 80));
        rightPanel.add(headerRight, BorderLayout.NORTH);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon originalIcon = new ImageIcon("assets/proudctionline.jpg");
        Image scaled = originalIcon.getImage().getScaledInstance(300, 300, Image.SCALE_DEFAULT);
        imageLabel.setIcon(new ImageIcon(scaled));
        rightPanel.add(imageLabel, BorderLayout.CENTER);

        // --- Logic ---
        buttonAddLine.addActionListener(e -> {
            String name = txtName.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a line name.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Status status = null;
            if (chkActive.isSelected()) status = Status.ACTIVE;
            else if (chkPaused.isSelected()) status = Status.PAUSED;
            else if (chkMaint.isSelected()) status = Status.MAINTENANCE;

            if (status == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a status.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ProductLine newLine = new ProductLine(name, status, new ArrayList<>());
            ProductLineController.addProductLine(newLine);

            JOptionPane.showMessageDialog(dialog, "Production Line added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();

            // Dynamic Refresh: Find the Main Frame and refresh the current view
            JFrame mainFrame = MainController.getFrame();
            if (mainFrame != null) {
                // Refresh the ProductionLines view using the stored leftPanel
                ProductionLines.showProductionLines(mainFrame, DashBoard.getLeftPanel());
            }
        });

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        dialog.add(mainPanel);

        dialog.setVisible(true);
    }
}
