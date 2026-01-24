package view;

import controller.UserController;
import controller.MainController;
import model.Rule;
import model.User;

import javax.swing.*;
import java.awt.*;

public class AddUser {

    public static void show() {
        // --- Dialog Setup ---
        JDialog dialog = new JDialog((Frame) null, "Add New User", true);
        dialog.setSize(800, 550);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // --- LEFT PANEL (Form) ---
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBackground(new Color(225, 238, 245));

        // Header
        JPanel header = new JPanel(null);
        header.setBackground(new Color(15, 70, 120));
        header.setBounds(0, 0, 400, 80);

        JLabel lblTitle = new JLabel("Add User Account", JLabel.LEFT);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.white);
        lblTitle.setBounds(30, 20, 350, 40);
        header.add(lblTitle);
        leftPanel.add(header);

        // Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(50, 120, 200, 25);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        leftPanel.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(50, 150, 300, 35);
        leftPanel.add(userField);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setBounds(50, 200, 200, 25);
        passLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        leftPanel.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(50, 230, 300, 35);
        leftPanel.add(passField);

        // Role
        JLabel roleLabel = new JLabel("Role");
        roleLabel.setBounds(50, 280, 200, 25);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        leftPanel.add(roleLabel);

        JRadioButton managerRb = new JRadioButton("Manager");
        managerRb.setBounds(50, 310, 120, 30);
        managerRb.setFont(new Font("Arial", Font.PLAIN, 18));
        managerRb.setOpaque(false);

        JRadioButton supervisorRb = new JRadioButton("Production Supervisor");
        supervisorRb.setBounds(180, 310, 220, 30);
        supervisorRb.setFont(new Font("Arial", Font.PLAIN, 18));
        supervisorRb.setOpaque(false);

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(managerRb);
        roleGroup.add(supervisorRb);

        leftPanel.add(managerRb);
        leftPanel.add(supervisorRb);

        // Add Button
        JButton createBtn = new JButton("Add User");
        createBtn.setBackground(new Color(247, 148, 30));
        createBtn.setForeground(Color.WHITE);
        createBtn.setBounds(50, 380, 300, 45);
        createBtn.setFont(new Font("Arial", Font.BOLD, 16));
        createBtn.setFocusable(false);
        leftPanel.add(createBtn);

        createBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Rule role;
            if (managerRb.isSelected()) role = Rule.MANAGER;
            else if (supervisorRb.isSelected()) role = Rule.PRODUCTION_SUPERVISOR;
            else {
                JOptionPane.showMessageDialog(dialog, "Please select a role!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User newUser = new User(username, password, role);

            // CHECK THE RETURN VALUE HERE
            boolean success = UserController.addUser(newUser);

            if (success) {
                JOptionPane.showMessageDialog(dialog, "User added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

                JFrame mainFrame = MainController.getFrame();
                if (mainFrame != null) {
                    UserInfo.usersInfo(mainFrame, DashBoard.getLeftPanel());
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Username '" + username + "' is already taken. Please choose another.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- RIGHT PANEL (Image) ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        JPanel headerRight = new JPanel(null);
        headerRight.setBackground(new Color(15, 70, 120));
        headerRight.setPreferredSize(new Dimension(400, 80));
        rightPanel.add(headerRight, BorderLayout.NORTH);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon originalIcon = new ImageIcon("assets/adduser.jpg"); // Using generic asset since AddUser.jpg might be missing
        Image scaled = originalIcon.getImage().getScaledInstance(300, 300, Image.SCALE_DEFAULT);
        imageLabel.setIcon(new ImageIcon(scaled));
        rightPanel.add(imageLabel, BorderLayout.CENTER);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        dialog.add(mainPanel);

        dialog.setVisible(true);
    }
}
