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

    static class BackgroundPanel extends JPanel {
        private final Image background;
        private final int arc = 30;

        public BackgroundPanel(ImageIcon icon) {
            this.background = icon.getImage();
            setLayout(null);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setClip(roundedRect);
            g2.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void userInfo(JFrame frame, JPanel leftPanel, String username) {
        FlatLightLaf.setup();

        Color industrialBlue = new Color(0x024971);
        Color activeOrange = new Color(0xF7941D);
        Color cleanWhite = Color.WHITE;

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
            nameLabel.setFont(new Font("Serif", Font.BOLD, 22));
            nameLabel.setForeground(activeOrange);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel roleLabel = new JLabel(user.getRule().toString());
            roleLabel.setFont(new Font("Serif", Font.PLAIN, 14));
            roleLabel.setForeground(activeOrange);
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
        JPanel addCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Do NOT call super.paintComponent(g) here if you want full control
                // This prevents the default square background from painting

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded grey shape
                g2.setColor(activeOrange);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2.dispose();
            }
        };
        addCard.setOpaque(false); // <--- CRITICAL: ensures no square background behind rounded corners
        addCard.setPreferredSize(new Dimension(400, 225));
        addCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add the PNG Icon instead of Text
        // Ensure "assets/add.png" exists in your folder
        JLabel plusLabel = new JLabel(new ImageIcon("assets/add.png"));
        plusLabel.setBackground(activeOrange);
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

    public static void editUser(User user) {
        System.out.println("Editing user: " + user.getUserName());
        JOptionPane.showMessageDialog(null, "Edit functionality for: " + user.getUserName());
    }
}
