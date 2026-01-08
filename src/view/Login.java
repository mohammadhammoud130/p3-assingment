package view;

import com.formdev.flatlaf.FlatLightLaf;
import controller.UserController;
import exception.UserAlreadyLoggedInException;
import model.Rule;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Login extends JPanel {

    // Custom panel that paints a background image
    static class BackgroundPanel extends JPanel {
        private final Image background;

        public BackgroundPanel(ImageIcon icon) {
            this.background = icon.getImage();
            setLayout(new GridBagLayout()); // keep layout for centering card
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void login(JFrame frame) {
        FlatLightLaf.setup();

        // Brand colors
        Color industrialBlue = new Color(0x024971);
        Color activeOrange = new Color(0xF7941D);
        Color cleanWhite = Color.WHITE;

        UIManager.put("Button.arc", 20);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("Button.background", activeOrange);
        UIManager.put("Button.foreground", cleanWhite);
        UIManager.put("Label.foreground", industrialBlue);

        // Main layout
        JPanel panel = new JPanel(new BorderLayout());

        // LEFT PANEL (static)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(industrialBlue);

        // Enforce exact width of 350px
        leftPanel.setPreferredSize(new Dimension(350, 0));
        leftPanel.setMinimumSize(new Dimension(350, 0));
        leftPanel.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));

        // Centered logo
        ImageIcon icon = new ImageIcon("assets/300px-logo.png");
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconLabel.setVerticalAlignment(JLabel.CENTER);
        leftPanel.add(iconLabel, BorderLayout.CENTER);

        // RIGHT PANEL with background image
        ImageIcon loginBackground = new ImageIcon("assets/login-background.jpg");
        BackgroundPanel rightPanel = new BackgroundPanel(loginBackground);

        // CARD PANEL (rounded container)
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(true);
        card.setBackground(cleanWhite);
        card.setPreferredSize(new Dimension(400, 450));
        card.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title
        JLabel title = new JLabel("LOGIN");
        title.setFont(new Font("serif", Font.BOLD, 40));
        title.setForeground(industrialBlue);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(300, 40));
        usernameField.setOpaque(false); // makes background transparent
        usernameField.setForeground(industrialBlue); // text color
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));



        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setOpaque(false); // transparent background
        passwordField.setForeground(industrialBlue);
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));


        // Role checkboxes
        JCheckBox managerCheck = new JCheckBox("Manager");
        JCheckBox supervisorCheck = new JCheckBox("Production Supervisor");
        managerCheck.setOpaque(false);
        supervisorCheck.setOpaque(false);

        managerCheck.addActionListener(e -> {
            if (managerCheck.isSelected()) supervisorCheck.setSelected(false);
        });
        supervisorCheck.addActionListener(e -> {
            if (supervisorCheck.isSelected()) managerCheck.setSelected(false);
        });

        JPanel checkboxRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        checkboxRow.setOpaque(false);
        checkboxRow.add(managerCheck);
        checkboxRow.add(supervisorCheck);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(200, 120));
        loginButton.setBackground(industrialBlue);


        // Add components to card
        card.add(title);
        card.add(Box.createVerticalStrut(50));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(20));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(50));
        card.add(checkboxRow);
        card.add(Box.createVerticalStrut(30));
        card.add(loginButton);

        // Center card in right panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(card, gbc);

        // Assemble main panel
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);

        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();

        // Login action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            String role = null;
            if (managerCheck.isSelected()) role = "MANAGER";
            if (supervisorCheck.isSelected()) role = "PRODUCTION_SUPERVISOR";

            if (role == null) {
                JOptionPane.showMessageDialog(frame, "Please select a role.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

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
