package view;

import com.formdev.flatlaf.FlatLightLaf;
import controller.UserController;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class UserInfo extends JPanel {
    private static final Color industrialBlue = new Color(0x024971);
    private static final Color activeOrange = new Color(0xF7941D);
    private static final Color cleanWhite = Color.WHITE;

    static class BackgroundPanel extends JPanel {
        private final Image background;
        private final int arc = 30;

        public BackgroundPanel(ImageIcon icon) {
            this.background = (icon != null) ? icon.getImage() : null;
            setLayout(null); // Use NULL layout for custom positioning if needed, or GridBag
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Clip to rounded shape
            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setClip(roundedRect);

            if (background != null) {
                g2.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                // Dark Overlay for text readability
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fill(roundedRect);
            } else {
                // Fallback for solid color (used for Add Button if needed)
                g2.setColor(industrialBlue);
                g2.fill(roundedRect);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void usersInfo(JFrame frame, JPanel leftPanel) {
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 20);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(cleanWhite);

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 20));
        header.setBackground(cleanWhite);
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(industrialBlue);
        header.add(titleLabel);
        contentPanel.add(header, BorderLayout.NORTH);

        // Grid - STRICT 3 Columns
        JPanel cardsGrid = new JPanel(new GridLayout(0, 3, 30, 30));
        cardsGrid.setBackground(cleanWhite);

        for (User user : UserController.getUsers().values()) {
            BackgroundPanel card = new BackgroundPanel(new ImageIcon("assets/cards-background.png"));
            card.setPreferredSize(new Dimension(400, 225));
            card.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();

            // --- 1. User Info (Center) ---
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);

            JLabel nameLabel = new JLabel(user.getUserName());
            nameLabel.setFont(new Font("Serif", Font.BOLD, 24));
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel roleLabel = new JLabel(user.getRule().toString());
            roleLabel.setFont(new Font("Serif", Font.PLAIN, 16));
            roleLabel.setForeground(Color.WHITE);
            roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            textPanel.add(nameLabel);
            textPanel.add(Box.createVerticalStrut(10));
            textPanel.add(roleLabel);

            gbc.gridx = 0; gbc.gridy = 0;
            gbc.gridwidth = 2; gbc.gridheight = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            card.add(textPanel, gbc);

            // --- 2. Delete Button (Top Right) ---
            JButton deleteBtn = new JButton(new ImageIcon("assets/delete.png"));
            deleteBtn.setContentAreaFilled(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            deleteBtn.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(null, "Delete user '" + user.getUserName() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    UserController.deleteUser(user.getUserName());
                    usersInfo(frame, leftPanel);
                }
            });

            gbc = new GridBagConstraints();
            gbc.gridx = 1; gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.weightx = 1.0; gbc.weighty = 1.0;
            gbc.insets = new Insets(10, 0, 0, 10);
            card.add(deleteBtn, gbc);

            // --- 3. Edit Button (Bottom Right) ---
            JButton editBtn = new JButton(new ImageIcon("assets/edit-user.png"));
            editBtn.setContentAreaFilled(false);
            editBtn.setBorderPainted(false);
            editBtn.setFocusPainted(false);
            editBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            editBtn.addActionListener(e -> editUser(user));

            gbc = new GridBagConstraints();
            gbc.gridx = 1; gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.SOUTHEAST;
            gbc.weightx = 1.0; gbc.weighty = 1.0;
            gbc.insets = new Insets(0, 0, 10, 10);
            card.add(editBtn, gbc);

            cardsGrid.add(card);
        }

        // Add Card
        BackgroundPanel addCard = new BackgroundPanel(new ImageIcon("assets/cards-background.png"));
        addCard.setLayout(new GridBagLayout());
        addCard.setOpaque(false);
        addCard.setPreferredSize(new Dimension(400, 225));
        addCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addCard.add(new JLabel(new ImageIcon("assets/add-orange.png")));
        addCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AddUser.show();
            }
        });
        cardsGrid.add(addCard);

        // Wrapper for centering the grid (prevents stretching)
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        wrapperPanel.setBackground(cleanWhite);
        wrapperPanel.add(cardsGrid);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();
    }

    public static void editUser(User user) {
        JDialog dialog = new JDialog((Frame) null, "Change Password", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());

        // --- Header ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(industrialBlue);
        header.setPreferredSize(new Dimension(450, 60));

        JLabel title = new JLabel("CHANGE PASSWORD");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(cleanWhite);
        header.add(title);

        dialog.add(header, BorderLayout.NORTH);

        // --- Form Content ---
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(cleanWhite);
        content.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Label
        JLabel label = new JLabel("New Password for " + user.getUserName() + ":");
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(Color.DARK_GRAY);

        gbc.gridy = 0;
        content.add(label, gbc);

        // Password Field
        JPasswordField passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(200, 35));
        passField.putClientProperty("JComponent.roundRect", true); // FlatLaf styling

        gbc.gridy = 1;
        content.add(passField, gbc);

        // --- Buttons ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setPreferredSize(new Dimension(100, 35));

        JButton saveBtn = new JButton("Update");
        saveBtn.setBackground(activeOrange);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.setFocusPainted(false);
        saveBtn.setPreferredSize(new Dimension(120, 35));

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        content.add(btnPanel, gbc);

        // --- Logic ---
        saveBtn.addActionListener(e -> {
            String newPass = new String(passField.getPassword()).trim();
            if (!newPass.isEmpty()) {
                user.setPassword(newPass);
                UserController.updateUsersFile();
                JOptionPane.showMessageDialog(dialog, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    public static void userInfo(JFrame frame, JPanel leftPanel, String username) {
        Color activeOrange = new Color(0xF7941D);
        Color cleanWhite = Color.WHITE;

        FlatLightLaf.setup();
        UIManager.put("Button.arc", 20);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(cleanWhite);

        User user = UserController.getUsers().get(username);

        if (user != null) {
            BackgroundPanel card = new BackgroundPanel(new ImageIcon("assets/cards-background.png"));


            Dimension cardSize = new Dimension(800, 450);
            card.setPreferredSize(cardSize);
            card.setMinimumSize(cardSize);
            card.setMaximumSize(cardSize);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            textPanel.setBounds(0, 0, 800, 450);

            JLabel nameLabel = new JLabel(user.getUserName());
            nameLabel.setFont(new Font("Serif", Font.BOLD, 48));
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel roleLabel = new JLabel(user.getRule().toString());
            roleLabel.setFont(new Font("Serif", Font.PLAIN, 28));
            roleLabel.setForeground(Color.WHITE);
            roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            textPanel.add(Box.createVerticalGlue());
            textPanel.add(nameLabel);
            textPanel.add(Box.createVerticalStrut(20));
            textPanel.add(roleLabel);
            textPanel.add(Box.createVerticalGlue());

            JButton editBtn = new JButton();
            ImageIcon icon = new ImageIcon("assets/edit-user.png");
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                editBtn.setIcon(new ImageIcon(img));
            } else {
                editBtn.setText("✎");
                editBtn.setForeground(Color.WHITE);
            }
            editBtn.setContentAreaFilled(false);
            editBtn.setBorderPainted(false);
            editBtn.setFocusPainted(false);
            editBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


            editBtn.setBounds(716, 366, 64, 64);

            editBtn.addActionListener(e -> editUser(user));

            card.add(editBtn);
            card.add(textPanel);

            centerPanel.add(card);
        }

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();
    }

}
