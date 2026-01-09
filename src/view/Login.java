package view;

import com.formdev.flatlaf.FlatLightLaf;
import controller.UserController;
import exception.UserAlreadyLoggedInException;
import model.Rule;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setMinimumSize(new Dimension(300, 0));
        leftPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

        JPanel logoBox = new JPanel();
        logoBox.setLayout(new BoxLayout(logoBox, BoxLayout.Y_AXIS));
        logoBox.setOpaque(false);

        JLabel iconLabel = new JLabel(new ImageIcon("assets/300px-logo.png"));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel factoryName = new JLabel("TWIZE FACTORY");
        factoryName.setAlignmentX(Component.CENTER_ALIGNMENT);
        factoryName.putClientProperty("FlatLaf.style", "font: bold 24 serif; foreground: #F7941D;");

        logoBox.add(Box.createVerticalGlue());
        logoBox.add(iconLabel);
        logoBox.add(Box.createVerticalStrut(20));
        logoBox.add(factoryName);
        logoBox.add(Box.createVerticalGlue());

        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(logoBox, BorderLayout.CENTER);




        // RIGHT PANEL with background image
        ImageIcon loginBackground = new ImageIcon("assets/login-background.jpg");
        BackgroundPanel rightPanel = new BackgroundPanel(loginBackground);

        // CARD PANEL (rounded container)
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
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
        TitledBorder usernameBorder = BorderFactory.createTitledBorder("Username");
        usernameBorder.setTitleFont(new Font("serif" ,Font.PLAIN,18));
        usernameBorder.setTitleColor(industrialBlue); // Set the title color
        usernameField.setBorder(usernameBorder);



        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setOpaque(false); // transparent background
        passwordField.setForeground(industrialBlue);
        TitledBorder passwordBorder = BorderFactory.createTitledBorder("Password");
        passwordBorder.setTitleFont(new Font("serif" ,Font.PLAIN,18));
        passwordBorder.setTitleColor(industrialBlue); // Set the title color
        passwordField.setBorder(passwordBorder);

        // Role checkboxes
        JCheckBox managerCheck = new JCheckBox("Manager");
        managerCheck.setFont(new Font("serif" ,Font.PLAIN,12));
        managerCheck.setForeground(industrialBlue);
        JCheckBox supervisorCheck = new JCheckBox("Production Supervisor");
        supervisorCheck.setFont(new Font("serif" ,Font.PLAIN,12));
        supervisorCheck.setForeground(industrialBlue);
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
        loginButton.setPreferredSize(new Dimension(200,35));
        loginButton.setMaximumSize(new Dimension(200, 35));
        loginButton.setMinimumSize(new Dimension(200,35));
        loginButton.setBackground(activeOrange);
        loginButton.putClientProperty("FlatLaf.style", ""
                + "background: #F7941D;"
                + "foreground: #FFFFFF;"
                + "hoverBackground: #D97F17;"
                + "pressedBackground: #B86612;"
                + "arc: 20;"
        );




        // Add components to card
        card.add(title);
        card.add(Box.createVerticalStrut(75));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(20));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(50));
        card.add(checkboxRow);
        card.add(Box.createVerticalStrut(50));
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
            ImageIcon warning = new ImageIcon("assets/warning-orange.png");
            String role = null;
            if (managerCheck.isSelected()) role = "MANAGER";
            if (supervisorCheck.isSelected()) role = "PRODUCTION_SUPERVISOR";

            if (role == null) {

                JOptionPane.showMessageDialog(frame, "Please select a role.", "Error", JOptionPane.ERROR_MESSAGE,warning);
                return;
            }

            try {
                UserController.login(username, password, Rule.valueOf(role));
            } catch (UserAlreadyLoggedInException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE,warning);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE,warning);
            }
        });
    }
}
