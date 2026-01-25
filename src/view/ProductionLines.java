package view;

import controller.ProductController;
import controller.ProductLineController;
import model.Product;
import model.ProductLine;
import model.Status;
import model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(cleanWhite);
        header.setBorder(new EmptyBorder(20, 50, 20, 50));

        JLabel titleLabel = new JLabel("Production Lines");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(industrialBlue);
        header.add(titleLabel, BorderLayout.WEST);

        JButton searchBtn = new JButton(new ImageIcon("assets/search.png"));
        searchBtn.setContentAreaFilled(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        header.add(searchBtn, BorderLayout.EAST);

        contentPanel.add(header, BorderLayout.NORTH);

        // --- Grid ---
        JPanel linesGrid = new JPanel(new GridLayout(0, 3, 30, 30));
        linesGrid.setBackground(cleanWhite);

        // Connect Search Logic
        searchBtn.addActionListener(e -> showSearchPopup(frame, leftPanel, linesGrid));

        // Initial Population
        if (ProductLineController.getProductLines() != null) {
            refreshGrid(frame, leftPanel, linesGrid, ProductLineController.getProductLines().values());
        }

        // Wrapper for FlowLayout centering
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperPanel.setBackground(cleanWhite);
        wrapperPanel.add(linesGrid);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainView.add(contentPanel, BorderLayout.CENTER);
        frame.setContentPane(mainView);
        frame.revalidate();
        frame.repaint();
    }

    public static void showProductionLinesForSupervisor(JFrame frame, JPanel leftPanel) {
        JPanel mainView = new JPanel(new BorderLayout());
        mainView.add(leftPanel, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(cleanWhite);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(cleanWhite);
        header.setBorder(new EmptyBorder(20, 50, 20, 50));

        JLabel titleLabel = new JLabel("Production Lines");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(industrialBlue);
        header.add(titleLabel, BorderLayout.WEST);

        JButton searchBtn = new JButton(new ImageIcon("assets/search.png"));
        searchBtn.setContentAreaFilled(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        header.add(searchBtn, BorderLayout.EAST);

        contentPanel.add(header, BorderLayout.NORTH);

        // Grid
        JPanel linesGrid = new JPanel(new GridLayout(0, 3, 30, 30));
        linesGrid.setBackground(cleanWhite);

        searchBtn.addActionListener(e -> showSearchPopup(frame, leftPanel, linesGrid));

        if (ProductLineController.getProductLines() != null) {
            // Use specific refresher for Supervisor
            refreshGridForSupervisor(frame, leftPanel, linesGrid, ProductLineController.getProductLines().values());
        }

        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperPanel.setBackground(cleanWhite);
        wrapperPanel.add(linesGrid);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainView.add(contentPanel, BorderLayout.CENTER);
        frame.setContentPane(mainView);
        frame.revalidate();
        frame.repaint();
    }


    // --- Search Logic ---

    private static void showSearchPopup(JFrame frame, JPanel leftPanel, JPanel grid) {
        JDialog dialog = new JDialog((Frame) null, "Filter by Product", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(cleanWhite);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblProd = new JLabel("Show lines producing:");
        lblProd.setFont(new Font("Arial", Font.BOLD, 14));

        JComboBox<Product> cmbProduct = new JComboBox<>();
        cmbProduct.addItem(null); // "All Products" option (null)

        if (ProductController.getProducts() != null) {
            for (Product p : ProductController.getProducts().values()) {
                cmbProduct.addItem(p);
            }
        }

        // Custom Renderer to show "All Products" or Product Name
        cmbProduct.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) setText("All Products");
                else if (value instanceof Product) setText(((Product) value).getName());
                return this;
            }
        });

        JButton btnFilter = new JButton("Filter");
        btnFilter.setBackground(activeOrange);
        btnFilter.setForeground(cleanWhite);
        btnFilter.setFont(new Font("Arial", Font.BOLD, 14));

        btnFilter.addActionListener(e -> {
            Product selectedProd = (Product) cmbProduct.getSelectedItem();
            List<ProductLine> filteredList = new ArrayList<>();

            if (selectedProd == null) {
                // Show All
                if (ProductLineController.getProductLines() != null) {
                    filteredList.addAll(ProductLineController.getProductLines().values());
                }
            } else {
                // Filter: Find lines that have a task for this product
                for (ProductLine line : ProductLineController.getProductLines().values()) {
                    boolean matches = false;
                    if (line.getTasks() != null) {
                        for (Task t : line.getTasks()) {
                            // Check if task matches product (and is essentially active/paused)
                            if (t.getRequiresdProduct().getId() == selectedProd.getId()
                                    && t.getStatus() != Status.FINISHED) {
                                matches = true;
                                break;
                            }
                        }
                    }
                    if (matches) filteredList.add(line);
                }
            }

            refreshGrid(frame, leftPanel, grid, filteredList);
            dialog.dispose();
        });

        panel.add(lblProd, BorderLayout.NORTH);
        panel.add(cmbProduct, BorderLayout.CENTER);
        panel.add(btnFilter, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private static void refreshGrid(JFrame frame, JPanel leftPanel, JPanel grid, Collection<ProductLine> lines) {
        grid.removeAll();
        for (ProductLine line : lines) {
            grid.add(createLineCard(frame, leftPanel, line));
        }
        grid.add(createAddCard()); // Always keep the add card
        grid.revalidate();
        grid.repaint();
    }

    // --- Components ---

    private static void refreshGridForSupervisor(JFrame frame, JPanel leftPanel, JPanel grid, Collection<ProductLine> lines) {
        grid.removeAll();
        for (ProductLine line : lines) {
            grid.add(createSupervisorLineCard(frame, leftPanel, line));
        }
        // NOTE: No Add Card here
        grid.revalidate();
        grid.repaint();
    }

    private static RoundedLinePanel createSupervisorLineCard(JFrame frame, JPanel leftPanel, ProductLine line) {
        RoundedLinePanel card = new RoundedLinePanel("assets/productLines.gif");
        card.setPreferredSize(new Dimension(400, 225));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setLayout(new BorderLayout());

        JPanel overlayContainer = new JPanel(new BorderLayout());
        overlayContainer.setOpaque(false);
        overlayContainer.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;

        JLabel nameLabel = new JLabel(line.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        nameLabel.setForeground(cleanWhite);

        JButton statusBtn = new JButton();
        updateButtonDisplay(statusBtn, line.getStatus());
        statusBtn.setContentAreaFilled(false);
        statusBtn.setBorderPainted(false);
        statusBtn.setFocusPainted(false);

        gbc.gridy = 0; centerPanel.add(nameLabel, gbc);
        gbc.gridy = 1; centerPanel.add(statusBtn, gbc);

        // --- BOTTOM: Note Button (Left) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomPanel.setOpaque(false);

        JButton noteBtn = new JButton(new ImageIcon("assets/notes.png"));
        noteBtn.setContentAreaFilled(false);
        noteBtn.setBorderPainted(false);
        noteBtn.setFocusPainted(false);
        noteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Call the READ-ONLY popup
        noteBtn.addActionListener(e -> showReadOnlyNotePopup(line));

        bottomPanel.add(noteBtn);

        overlayContainer.add(centerPanel, BorderLayout.CENTER);
        overlayContainer.add(bottomPanel, BorderLayout.SOUTH);

        overlayContainer.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                TaskView.showTasksForLine(frame, leftPanel, line);
            }
        });

        card.add(overlayContainer, BorderLayout.CENTER);
        return card;
    }


    static class RoundedLinePanel extends JLayeredPane {
        private final Image background;
        private final int arc = 30;
        private Color overlayColor = new Color(0, 0, 0, 80);

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
            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setClip(roundedRect);

            if (background != null) {
                g2.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                g2.setColor(overlayColor);
                g2.fill(roundedRect);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static void showReadOnlyNotePopup(ProductLine line) {
        JDialog dialog = new JDialog((Frame) null, "Notes for " + line.getName(), true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cleanWhite);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        displayArea.setBackground(new Color(245, 245, 245)); // Light gray background

        StringBuilder allNotes = new StringBuilder();
        if (line.getNotes() != null && !line.getNotes().isEmpty()) {
            for (String n : line.getNotes()) {
                allNotes.append("• ").append(n).append("\n");
            }
        } else {
            allNotes.append("(No notes available)");
            displayArea.setForeground(Color.GRAY);
        }
        displayArea.setText(allNotes.toString());

        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(null);

        // Add "Close" button at bottom
        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(industrialBlue);
        closeBtn.setForeground(cleanWhite);
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(cleanWhite);
        btnPanel.add(closeBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private static RoundedLinePanel createLineCard(JFrame frame, JPanel leftPanel, ProductLine line) {
        RoundedLinePanel card = new RoundedLinePanel("assets/productLines.gif");
        card.setPreferredSize(new Dimension(400, 225));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setLayout(new BorderLayout());

        JPanel overlayContainer = new JPanel(new BorderLayout());
        overlayContainer.setOpaque(false);
        overlayContainer.setBorder(new EmptyBorder(15, 20, 15, 20));

        // --- TOP: Delete Button ---
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
                ProductLineController.deleteProductLine(line.getId());
                showProductionLines(frame, leftPanel);
            }
        });
        topPanel.add(deleteBtn);

        // --- CENTER: Info & Status ---
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

        // --- BOTTOM: Note Button (Right) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setOpaque(false);

        // Ensure you have "assets/note.png"
        JButton noteBtn = new JButton(new ImageIcon("assets/notes.png"));
        noteBtn.setContentAreaFilled(false);
        noteBtn.setBorderPainted(false);
        noteBtn.setFocusPainted(false);
        noteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        noteBtn.addActionListener(e -> showAddNotePopup(line));

        bottomPanel.add(noteBtn);

        // --- ASSEMBLY ---
        overlayContainer.add(topPanel, BorderLayout.NORTH);
        overlayContainer.add(centerPanel, BorderLayout.CENTER);
        overlayContainer.add(bottomPanel, BorderLayout.SOUTH);

        // Click Action (Navigate to Tasks) - Add listener to overlay
        overlayContainer.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                TaskView.showTasksForLine(frame, leftPanel, line);
            }
        });

        card.add(overlayContainer, BorderLayout.CENTER);
        return card;
    }

    private static void showAddNotePopup(ProductLine line) {
        JDialog dialog = new JDialog((Frame) null, "Notes for " + line.getName(), true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(cleanWhite);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Display Existing Notes
        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        StringBuilder allNotes = new StringBuilder();
        if (line.getNotes() != null) {
            for (String n : line.getNotes()) {
                allNotes.append("• ").append(n).append("\n");
            }
        }
        displayArea.setText(allNotes.toString());

        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Existing Notes"));

        // 2. Input Field for New Note
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setOpaque(false);

        JTextField noteField = new JTextField();
        JButton saveBtn = new JButton("Add");
        saveBtn.setBackground(activeOrange);
        saveBtn.setForeground(cleanWhite);

        saveBtn.addActionListener(e -> {
            String newNote = noteField.getText().trim();
            if (!newNote.isEmpty()) {
                ProductLineController.addNoteToLine(line.getId(), newNote);
                displayArea.append("• " + newNote + "\n");
                noteField.setText("");
            }
        });

        inputPanel.add(noteField, BorderLayout.CENTER);
        inputPanel.add(saveBtn, BorderLayout.EAST);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private static RoundedLinePanel createAddCard() {
        RoundedLinePanel card = new RoundedLinePanel("assets/productLines.gif");
        card.setPreferredSize(new Dimension(400, 225));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setOverlayColor(new Color(2, 73, 113, 180));

        JPanel overlayContainer = new JPanel(new GridBagLayout());
        overlayContainer.setOpaque(false);
        overlayContainer.add(new JLabel(new ImageIcon("assets/add-orange.png")));

        card.setLayout(new BorderLayout());
        card.add(overlayContainer, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
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
        if (status == Status.ACTIVE) icon = new ImageIcon(new ImageIcon("assets/ACTIVE.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        else if (status == Status.PAUSED) icon = new ImageIcon(new ImageIcon("assets/PAUSED.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        else icon = new ImageIcon(new ImageIcon("assets/MAINTENANCE.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        btn.setIcon(icon);
    }

    private static void showStatusPopup(int lineId, JButton sourceBtn) {
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setSize(60, 150);

        Point p = sourceBtn.getLocationOnScreen();
        dialog.setLocation(p.x + (sourceBtn.getWidth() / 2) - 30, p.y + sourceBtn.getHeight());

        JPanel content = new JPanel(new GridLayout(3, 1, 5, 5));
        content.setBackground(new Color(255, 255, 255, 240));
        content.setBorder(new javax.swing.border.LineBorder(industrialBlue, 2));

        for (Status s : new Status[]{Status.ACTIVE, Status.PAUSED, Status.MAINTENANCE}) {
            JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            optionPanel.setOpaque(false);
            optionPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Hardcoded switch to get the icon
            String iconPath;
            switch (s) {
                case ACTIVE: iconPath = "assets/status-active.png"; break;
                case PAUSED: iconPath = "assets/status-paused.png"; break;
                default:     iconPath = "assets/status-maintenance.png"; break; // MAINTENANCE
            }

            ImageIcon icon = new ImageIcon(iconPath);
            // Optional: Scale if needed (e.g., 32x32)
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(img));

            optionPanel.add(iconLabel);

            optionPanel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { optionPanel.setOpaque(true); optionPanel.setBackground(new Color(230, 230, 230)); optionPanel.repaint(); }
                public void mouseExited(MouseEvent e) { optionPanel.setOpaque(false); optionPanel.repaint(); }
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
