package view;


import com.formdev.flatlaf.FlatLightLaf;
import controller.UserController;
import exception.UserAlreadyLoggedInException;
import model.Rule;

import javax.swing.*;
import java.awt.*;

public class Login extends JPanel {
    public static void login(JFrame frame) {
        FlatLightLaf.setup();
        UIManager.put("Panel.background", new Color(7, 255, 168));   // panel background
        UIManager.put("Button.background", new Color(80, 120, 250));   // button background
        UIManager.put("Button.foreground", Color.WHITE);               // button text
        UIManager.put("Label.foreground", new Color(30, 30, 30));      // label text
        UIManager.put("TextComponent.background", Color.WHITE);        // text fields
        UIManager.put("TextComponent.foreground", new Color(40, 40, 40));
        UIManager.put("TextComponent.selectionBackground", new Color(250, 200, 120));


        JPanel panel = new JPanel(new GridLayout(1, 2));

        // Left panel with image
        JPanel leftPanel = new JPanel(new BorderLayout());
        ImageIcon icon = new ImageIcon("Assets/logg.jpg");
        JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
        leftPanel.add(iconLabel, BorderLayout.CENTER);

        // Right panel with login form
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("LOGIN");
        title.setFont(new Font("serif", Font.BOLD, 40));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(250, 35));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(250, 35));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        String[] roles = {"MANAGER", "PRODUCTION_SUPERVISOR"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        roleBox.setMaximumSize(new Dimension(250, 40));
        roleBox.setBorder(BorderFactory.createTitledBorder("Roles"));

        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(100, 40));

        // Layout
        rightPanel.add(Box.createVerticalStrut(200));
        rightPanel.add(title);
        rightPanel.add(Box.createVerticalStrut(45));
        rightPanel.add(usernameField);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(passwordField);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(roleBox);
        rightPanel.add(Box.createVerticalStrut(25));
        rightPanel.add(loginButton);

        panel.add(leftPanel);
        panel.add(rightPanel);

        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();

        // Action listener
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = (String) roleBox.getSelectedItem();

            try {
                UserController.login(username, password, Rule.valueOf(role));
                JOptionPane.showMessageDialog(frame, "Login successful!");
            } catch (UserAlreadyLoggedInException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
