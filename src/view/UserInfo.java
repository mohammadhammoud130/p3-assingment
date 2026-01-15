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
        JPanel cardsGrid = new JPanel(new GridLayout(0, 3, 30, 40));
        cardsGrid.setBackground(Color.WHITE);
        cardsGrid.setBorder(new EmptyBorder(30, 30, 30, 30));

        for (User user : UserController.getUsers().values()) {
            BackgroundPanel card = new BackgroundPanel(new ImageIcon("assets/cards-background.png"));
            Dimension cardSize = new Dimension(400, 225);
            card.setPreferredSize(cardSize);
            card.setMaximumSize(cardSize);
            card.setMinimumSize(cardSize);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            textPanel.setBounds(0, 0, 400, 225);

            JLabel nameLabel = new JLabel(user.getUserName());
            nameLabel.setFont(new Font("Serif", Font.BOLD, 16));
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel roleLabel = new JLabel(user.getRule().toString());
            roleLabel.setFont(new Font("Serif", Font.PLAIN, 12));
            roleLabel.setForeground(Color.WHITE);
            roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            textPanel.add(Box.createVerticalGlue());
            textPanel.add(nameLabel);
            textPanel.add(Box.createVerticalStrut(10));
            textPanel.add(roleLabel);
            textPanel.add(Box.createVerticalGlue());

            JButton editBtn = new JButton();
            ImageIcon icon = new ImageIcon("assets/edit-user.png");
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                editBtn.setIcon(new ImageIcon(img));
            } else {
                editBtn.setText("✎");
                editBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
                editBtn.setForeground(Color.WHITE);
            }
            editBtn.setContentAreaFilled(false);
            editBtn.setBorderPainted(false);
            editBtn.setFocusPainted(false);
            editBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            editBtn.setBounds(358, 183, 32, 32);

            editBtn.addActionListener(e -> editUser(user));

            card.add(editBtn);
            card.add(textPanel);

            cardsGrid.add(card);
        }

        // Create the card panel
        JPanel addCard = new BackgroundPanel(new ImageIcon("assets/cards-background.png"));
        addCard.setLayout(new GridBagLayout());
        addCard.setOpaque(false);
        addCard.setPreferredSize(new Dimension(400, 225));
        addCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add the PNG Icon instead of Text
        // Ensure "assets/add.png" exists in your folder
        JLabel plusLabel = new JLabel(new ImageIcon("assets/add-orange.png"));
        plusLabel.setOpaque(false);
        plusLabel.setPreferredSize(new Dimension(32,32));

        addCard.add(plusLabel);

        // Click Action
        addCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               // addUser();
            }
        });

        cardsGrid.add(addCard);



        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(Color.WHITE);
        gridWrapper.add(cardsGrid, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();
    }

    public static void userInfo(JFrame frame, JPanel leftPanel, String username) {
        // Setup Colors
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

            // INCREASED SIZE: 800x450 (Double the previous 400x225)
            // Still maintains 16:9 Aspect Ratio
            Dimension cardSize = new Dimension(800, 450);
            card.setPreferredSize(cardSize);
            card.setMinimumSize(cardSize);
            card.setMaximumSize(cardSize);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            textPanel.setBounds(0, 0, 800, 450); // Match new card size

            JLabel nameLabel = new JLabel(user.getUserName());
            nameLabel.setFont(new Font("Serif", Font.BOLD, 48)); // Doubled font size (22 -> 48)
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel roleLabel = new JLabel(user.getRule().toString());
            roleLabel.setFont(new Font("Serif", Font.PLAIN, 28)); // Doubled font size (14 -> 28)
            roleLabel.setForeground(Color.WHITE);
            roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            textPanel.add(Box.createVerticalGlue());
            textPanel.add(nameLabel);
            textPanel.add(Box.createVerticalStrut(20)); // Increased gap
            textPanel.add(roleLabel);
            textPanel.add(Box.createVerticalGlue());

            JButton editBtn = new JButton();
            ImageIcon icon = new ImageIcon("assets/edit-user.png");
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                // Scaled icon to 64x64 (Doubled size)
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

            // Reposition button for 800x450 card
            // x = 800 - 64 - 20 = 716
            // y = 450 - 64 - 20 = 366
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

    public static void editUser(User user) {
        System.out.println("Editing user: " + user.getUserName());
        JOptionPane.showMessageDialog(null, "Edit functionality for: " + user.getUserName());
    }
}
