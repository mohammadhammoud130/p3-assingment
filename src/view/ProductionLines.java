package view;

import controller.ProductLineController;
import model.ProductLine;
import model.Status;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ProductionLines {

    private static final Color industrialBlue = new Color(0x024971);
    private static final Color activeOrange = new Color(0xF7941D);
    private static final Color cleanWhite = Color.WHITE;

    public static void showProductionLines(JFrame frame, JPanel leftPanel) {
        JPanel mainView = new JPanel(new BorderLayout());
        mainView.add(leftPanel, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(cleanWhite);

        // --- Header ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 20));
        header.setBackground(cleanWhite);
        JLabel titleLabel = new JLabel("Production Lines");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(industrialBlue);
        header.add(titleLabel);
        contentPanel.add(header, BorderLayout.NORTH);

        // --- Grid Logic (Fix for Fullscreen Cutoff) ---
        // 1. Force 3 columns using GridLayout
        JPanel linesGrid = new JPanel(new GridLayout(0, 3, 30, 30));
        linesGrid.setBackground(cleanWhite);

        if (ProductLineController.getProductLines() != null) {
            for (ProductLine line : ProductLineController.getProductLines().values()) {
                linesGrid.add(createLineCard(frame, leftPanel, line));
            }
        }
        linesGrid.add(createAddCard());

        // 2. Wrap the grid in a FlowLayout to center it and prevent stretching/cutoff
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperPanel.setBackground(cleanWhite);
        wrapperPanel.add(linesGrid);

        // 3. Scroll the wrapper
        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainView.add(contentPanel, BorderLayout.CENTER);
        frame.setContentPane(mainView);
        frame.revalidate();
        frame.repaint();
    }

    static class RoundedLinePanel extends JLayeredPane {
        private final Image background;
        private final int arc = 30;
        private Color overlayColor = new Color(0, 0, 0, 80); // Default overlay

        public RoundedLinePanel(String imagePath) {
            this.background = new ImageIcon(imagePath).getImage();
            setOpaque(false);
        }

        public void setOverlayColor(Color c) {
            this.overlayColor = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Create the Rounded Clip
            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setClip(roundedRect);

            if (background != null) {
                // Draw Image
                g2.drawImage(background, 0, 0, getWidth(), getHeight(), this);

                // Draw Overlay INSIDE the clip (Fixes the corner shadow issue)
                g2.setColor(overlayColor);
                g2.fill(roundedRect);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static RoundedLinePanel createLineCard(JFrame frame, JPanel leftPanel, ProductLine line) {
        RoundedLinePanel card = new RoundedLinePanel("assets/productLines.gif");
        card.setPreferredSize(new Dimension(400, 225));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setLayout(new BorderLayout());

        // Overlay Panel acts only as a container now (Painting moved to RoundedLinePanel)
        JPanel overlayContainer = new JPanel(new BorderLayout());
        overlayContainer.setOpaque(false);
        overlayContainer.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topPanel.setOpaque(false);
        JButton deleteBtn = new JButton(new ImageIcon("assets/delete.png"));
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> {
            int choice1 = JOptionPane.showConfirmDialog(null, "Delete this Production Line?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (choice1 == JOptionPane.YES_OPTION) {
                int choice2 = JOptionPane.showConfirmDialog(null, "Are you sure?", "Final", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice2 == JOptionPane.YES_OPTION) {
                    ProductLineController.deleteProductLine(line.getId());
                    showProductionLines(frame, leftPanel);
                }
            }
        });
        topPanel.add(deleteBtn);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;

        JLabel nameLabel = new JLabel(line.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        nameLabel.setForeground(cleanWhite);

        JButton statusBtn = createStatusButton(line.getId(), line.getStatus());

        gbc.gridy = 0; centerPanel.add(nameLabel, gbc);
        gbc.gridy = 1; centerPanel.add(statusBtn, gbc);

        overlayContainer.add(topPanel, BorderLayout.NORTH);
        overlayContainer.add(centerPanel, BorderLayout.CENTER);

        overlayContainer.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                TaskView.showTasksForLine(frame, leftPanel, line);
            }
        });

        card.add(overlayContainer, BorderLayout.CENTER);
        return card;
    }

    private static RoundedLinePanel createAddCard() {
        RoundedLinePanel card = new RoundedLinePanel("assets/productLines.gif");
        card.setPreferredSize(new Dimension(400, 225));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Set the Blue overlay color for the Add Card
        card.setOverlayColor(new Color(2, 73, 113, 180));

        JPanel overlayContainer = new JPanel(new GridBagLayout());
        overlayContainer.setOpaque(false);
        overlayContainer.add(new JLabel(new ImageIcon("assets/add-orange.png")));

        card.setLayout(new BorderLayout());
        card.add(overlayContainer, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AddProductionLine.show();
            }
        });
        return card;
    }

    private static JButton createStatusButton(int lineId, Status currentStatus) {
        JButton statusBtn = new JButton();
        updateButtonDisplay(statusBtn, currentStatus);
        statusBtn.setContentAreaFilled(false);
        statusBtn.setBorderPainted(false);
        statusBtn.setFocusPainted(false);
        statusBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        statusBtn.addActionListener(e -> showStatusPopup(lineId, statusBtn));
        return statusBtn;
    }

    private static void updateButtonDisplay(JButton btn, Status status) {
        ImageIcon icon = null;
        if (status == Status.ACTIVE) icon = new ImageIcon(new ImageIcon("assets/status-active.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        else if (status == Status.PAUSED) icon = new ImageIcon(new ImageIcon("assets/status-paused.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        else icon = new ImageIcon(new ImageIcon("assets/status-maintenance.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        btn.setIcon(icon);
    }

    private static void showStatusPopup(int lineId, JButton sourceBtn) {
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setSize(220, 150);
        Point p = sourceBtn.getLocationOnScreen();
        dialog.setLocation(p.x, p.y + sourceBtn.getHeight());

        JPanel content = new JPanel(new GridLayout(3, 1, 5, 5));
        content.setBackground(new Color(255, 255, 255, 240));
        content.setBorder(new javax.swing.border.LineBorder(industrialBlue, 2));

        for (Status s : new Status[]{Status.ACTIVE, Status.PAUSED, Status.MAINTENANCE}) {
            JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            optionPanel.setOpaque(false);
            optionPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel text = new JLabel(s.name());
            text.setFont(new Font("SansSerif", Font.BOLD, 12));
            text.setForeground(industrialBlue);

            optionPanel.add(text);
            optionPanel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    ProductLineController.updateLineStatus(lineId, s);
                    updateButtonDisplay(sourceBtn, s);
                    dialog.dispose();
                }
            });
            content.add(optionPanel);
        }
        dialog.setContentPane(content);
        dialog.setModal(true);
        dialog.addWindowFocusListener(new java.awt.event.WindowAdapter() {
            public void windowLostFocus(java.awt.event.WindowEvent e) { dialog.dispose(); }
        });
        dialog.setVisible(true);
    }
}
