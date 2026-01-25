package view;

import controller.ItemController;
import controller.ProductController;
import controller.ProductLineController;
import controller.TaskController;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Storage {

    private static final Color industrialBlue = new Color(0x024971);
    private static final Color activeOrange = new Color(0xF7941D);
    private static final Color cleanWhite = Color.WHITE;

    private static JPanel itemsGrid;

    // --- Custom Components ---

    static class BackgroundPanel extends JPanel {
        private final Image background;
        private final int arc = 30;
        private Color overlayColor = new Color(0, 0, 0, 140);

        public BackgroundPanel(ImageIcon icon) {
            this.background = (icon != null) ? icon.getImage() : null;
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(15, 20, 15, 20));
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
            } else {
                g2.setColor(industrialBlue);
                g2.fill(roundedRect);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class GifPanel extends JPanel {
        private final Image gifImage;
        public GifPanel(String path) {
            this.gifImage = new ImageIcon(path).getImage();
            setLayout(new BorderLayout());
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (gifImage != null) g.drawImage(gifImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    static class RoundedImagePanel extends JPanel {
        private final Image image;
        public RoundedImagePanel(String imagePath) {
            this.image = new ImageIcon(imagePath).getImage();
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 40, 40));
            g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // --- Main View ---

    public static void storage(JFrame frame, JPanel leftPanel) {
        GifPanel rootPanel = new GifPanel("assets/storage.gif");
        JPanel westContainer = new JPanel(new BorderLayout());
        westContainer.setOpaque(false);
        westContainer.add(leftPanel, BorderLayout.CENTER);
        rootPanel.add(westContainer, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setOpaque(false);

        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 20);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        RoundedImagePanel productBtn = new RoundedImagePanel("assets/Product-storage.jpg");
        productBtn.setPreferredSize(new Dimension(550, 850));
        productBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                ((CardLayout) contentPanel.getLayout()).show(contentPanel, "PRODUCT_VIEW");
            }
        });

        RoundedImagePanel itemBtn = new RoundedImagePanel("assets/Items-storage.jpg");
        itemBtn.setPreferredSize(new Dimension(550, 850));
        itemBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                ((CardLayout) contentPanel.getLayout()).show(contentPanel, "ITEM_VIEW");
                resetItemSearch();
            }
        });

        menuPanel.add(productBtn, gbc);
        menuPanel.add(itemBtn, gbc);

        contentPanel.add(menuPanel, "MENU");
        contentPanel.add(ProductView(contentPanel), "PRODUCT_VIEW");
        contentPanel.add(ItemView(contentPanel), "ITEM_VIEW");

        rootPanel.add(contentPanel, BorderLayout.CENTER);
        frame.setContentPane(rootPanel);
        frame.revalidate();
        frame.repaint();
    }

    private static void resetItemSearch() {
        if (itemsGrid != null && ItemController.getItems() != null) {
            refreshItemGrid(itemsGrid, ItemController.getItems().values());
        }
    }

    // --- Product View ---

    private static JPanel ProductView(JPanel container) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel productsGrid = new JPanel(new GridLayout(0, 3, 30, 40));
        productsGrid.setOpaque(false);

        // Header with Search Button
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(true);
        topBar.setBackground(industrialBlue);
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Products");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(cleanWhite);

        JButton searchBtn = new JButton(new ImageIcon("assets/search.png"));
        searchBtn.setContentAreaFilled(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> showProductSearchPopup(productsGrid));

        topBar.add(title, BorderLayout.WEST);
        topBar.add(searchBtn, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Content


        if (ProductController.getProducts() != null) {
            refreshProductGrid(productsGrid, ProductController.getProducts().values());
        }

        JPanel gridWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        gridWrapper.setOpaque(false);
        gridWrapper.add(productsGrid);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(true);
        bottomBar.setBackground(industrialBlue);
        bottomBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        bottomBar.add(createBackButton(container));

        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    // --- Item View ---

    private static JPanel ItemView(JPanel container) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Header
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(true);
        topBar.setBackground(industrialBlue);
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Inventory");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(cleanWhite);

        JButton searchBtn = new JButton(new ImageIcon("assets/search.png"));
        searchBtn.setContentAreaFilled(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        topBar.add(title, BorderLayout.WEST);
        topBar.add(searchBtn, BorderLayout.EAST);

        panel.add(topBar, BorderLayout.NORTH);

        // Footer
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(true);
        bottomBar.setBackground(industrialBlue);
        bottomBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        bottomBar.add(createBackButton(container));

        panel.add(bottomBar, BorderLayout.SOUTH);

        // Content
        itemsGrid = new JPanel(new GridLayout(0, 3, 30, 40));
        itemsGrid.setOpaque(false);

        if (ItemController.getItems() != null) {
            refreshItemGrid(itemsGrid, ItemController.getItems().values());
        }

        searchBtn.addActionListener(e -> showItemsSearchPopup(itemsGrid));

        // Use FlowLayout Wrapper
        JPanel gridWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        gridWrapper.setOpaque(false);
        gridWrapper.add(itemsGrid);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // --- Grid Helpers ---
    private static void refreshProductGrid(JPanel productsGrid, Collection<Product> productsToShow) {
        productsGrid.removeAll();
        for (Product product : productsToShow) {
            productsGrid.add(createProductPanel(product, productsGrid));
        }
        productsGrid.add(createAddCard("assets/product.png", "assets/add-orange.png", AddProduct::show));
        productsGrid.revalidate(); productsGrid.repaint();
    }

    private static void refreshItemGrid(JPanel itemsGrid, Collection<Item> itemsToShow) {
        itemsGrid.removeAll();

        for (Item item : itemsToShow) {
            itemsGrid.add(createItemPanel(item, itemsGrid));
        }

        itemsGrid.add(createAddCard("assets/default-item.png", "assets/add-orange.png", AddItem::show));

        itemsGrid.revalidate();
        itemsGrid.repaint();
    }

    private static BackgroundPanel createAddCard(String bgImage, String iconImage, Runnable action) {
        BackgroundPanel card = new BackgroundPanel(new ImageIcon(bgImage));
        card.setPreferredSize(new Dimension(400, 255));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        card.setLayout(new GridBagLayout());
        card.add(new JLabel(new ImageIcon(iconImage)));

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { action.run(); }
        });

        return card;
    }

    private static JButton createBackButton(JPanel container) {
        JButton backBtn = new JButton(new ImageIcon("assets/back.png"));
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> ((CardLayout) container.getLayout()).show(container, "MENU"));
        return backBtn;
    }

    // --- Panel Creators ---

    private static BackgroundPanel createItemPanel(Item item, JPanel parentGrid) {
        ImageIcon bg = new ImageIcon("assets/default-item.png");
        if (item.getCategory() != null) {
            String cat = item.getCategory().name().toLowerCase();
            if (cat.contains("fabric")) bg = new ImageIcon("assets/fabric.png");
            else if (cat.contains("thread")) bg = new ImageIcon("assets/threads.png");
            else if (cat.contains("leather")) bg = new ImageIcon("assets/leather.png");
            else if (cat.contains("button") || cat.contains("zipper")) bg = new ImageIcon("assets/bottons-zippers.png");
        }
        BackgroundPanel panel = new BackgroundPanel(bg);
        panel.setPreferredSize(new Dimension(400, 255));
        panel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topPanel.setOpaque(false);
        JButton deleteBtn = new JButton(new ImageIcon("assets/delete.png"));
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null, "Delete " + item.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                ItemController controller = new ItemController();
                controller.deleteItemById(item.getId());
                ItemController.updateItemsFile();
                parentGrid.remove(panel);
                parentGrid.revalidate();
                parentGrid.repaint();
            }
        });
        topPanel.add(deleteBtn);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Font labelFont = new Font("SansSerif", Font.BOLD, 18);
        Font valFont = new Font("SansSerif", Font.PLAIN, 16);

        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(activeOrange);

        JLabel idLabel = new JLabel("ID: " + item.getId());
        idLabel.setFont(valFont);
        idLabel.setForeground(cleanWhite);

        JLabel qtyLabel = new JLabel("Qty: " + item.getQuantity());
        qtyLabel.setFont(valFont);
        qtyLabel.setForeground(cleanWhite);

        JLabel minLabel = new JLabel("Min: " + item.getMinThreshold());
        minLabel.setFont(valFont);
        minLabel.setForeground(cleanWhite);

        centerPanel.add(nameLabel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(idLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(qtyLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(minLabel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setOpaque(false);
        JButton editBtn = new JButton(new ImageIcon("assets/edit-orange.png"));
        editBtn.setContentAreaFilled(false);
        editBtn.setBorderPainted(false);
        editBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editBtn.addActionListener(e -> showEditItemPopup(item, qtyLabel));
        bottomPanel.add(editBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static BackgroundPanel createProductPanel(model.Product product, JPanel parentGrid) {
        BackgroundPanel panel = new BackgroundPanel(new ImageIcon("assets/product.png"));
        panel.setPreferredSize(new Dimension(400, 255));
        panel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topPanel.setOpaque(false);
        JButton deleteBtn = new JButton(new ImageIcon("assets/delete.png"));
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null, "Delete " + product.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                ProductController.getProducts().remove(product.getId());
                ProductController.updateProductsFile();
                parentGrid.remove(panel);
                parentGrid.revalidate();
                parentGrid.repaint();
            }
        });
        topPanel.add(deleteBtn);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Font labelFont = new Font("SansSerif", Font.BOLD, 18);
        Font valFont = new Font("SansSerif", Font.PLAIN, 16);

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(activeOrange);

        JLabel idLabel = new JLabel("ID: " + product.getId());
        idLabel.setFont(valFont);
        idLabel.setForeground(cleanWhite);

        JLabel timeLabel = new JLabel("Est. Time: " + product.getEstimatedTime() + "h");
        timeLabel.setFont(valFont);
        timeLabel.setForeground(cleanWhite);

        centerPanel.add(nameLabel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(idLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(timeLabel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomPanel.setOpaque(false);
        JButton reqBtn = new JButton("View Requirements");
        reqBtn.setFocusPainted(false);
        reqBtn.setBackground(activeOrange);
        reqBtn.setForeground(cleanWhite);
        reqBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        reqBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        reqBtn.addActionListener(e -> showRequirementsPopup(product));
        bottomPanel.add(reqBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // --- Popups ---

    private static void showProductSearchPopup(JPanel grid) {
        JDialog dialog = new JDialog((Frame) null, "Filter Products", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(cleanWhite);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.weightx = 1.0;

        JRadioButton rbLine = new JRadioButton("Filter by Production Line (Non-Active Tasks)");
        JRadioButton rbDate = new JRadioButton("Most Required Product(s) (Date Range)");
        ButtonGroup bg = new ButtonGroup(); bg.add(rbLine); bg.add(rbDate);
        rbLine.setOpaque(false); rbDate.setOpaque(false);
        rbLine.setSelected(true);

        JComboBox<ProductLine> cmbLine = new JComboBox<>();
        if (ProductLineController.getProductLines() != null) {
            for (ProductLine line : ProductLineController.getProductLines().values()) cmbLine.addItem(line);
        }
        cmbLine.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProductLine) setText(((ProductLine)value).getName());
                return this;
            }
        });

        JTextField txtStart = new JTextField(LocalDate.now().minusDays(30).toString());
        JTextField txtEnd = new JTextField(LocalDate.now().toString());
        txtStart.setEnabled(false); txtEnd.setEnabled(false);

        rbLine.addActionListener(e -> { cmbLine.setEnabled(true); txtStart.setEnabled(false); txtEnd.setEnabled(false); });
        rbDate.addActionListener(e -> { cmbLine.setEnabled(false); txtStart.setEnabled(true); txtEnd.setEnabled(true); });

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panel.add(rbLine, gbc);
        gbc.gridy = 1; panel.add(cmbLine, gbc);
        gbc.gridy = 2; panel.add(rbDate, gbc);
        gbc.gridy = 3; gbc.gridwidth = 1; panel.add(new JLabel("Start (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; panel.add(txtStart, gbc);
        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("End (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; panel.add(txtEnd, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton btnReset = new JButton("Show All");
        JButton btnApply = new JButton("Apply");
        btnReset.setBackground(industrialBlue); btnReset.setForeground(cleanWhite);
        btnApply.setBackground(activeOrange); btnApply.setForeground(cleanWhite);

        btnReset.addActionListener(e -> {
            refreshProductGrid(grid, ProductController.getProducts().values());
            dialog.dispose();
        });

        btnApply.addActionListener(e -> {
            try {
                if (rbLine.isSelected()) {
                    ProductLine selectedLine = (ProductLine) cmbLine.getSelectedItem();
                    if (selectedLine == null) return;
                    List<Product> filtered = new ArrayList<>();
                    if (selectedLine.getTasks() != null) {
                        for (Task t : selectedLine.getTasks()) {
                            if (t.getStatus() != Status.ACTIVE) filtered.add(t.getRequiresdProduct());
                        }
                    }
                    filtered = filtered.stream().distinct().collect(Collectors.toList());
                    refreshProductGrid(grid, filtered);
                    dialog.dispose();

                } else {
                    // --- UPDATED LOGIC FOR MULTIPLE "MOST REQUIRED" ---
                    LocalDate start = LocalDate.parse(txtStart.getText().trim());
                    LocalDate end = LocalDate.parse(txtEnd.getText().trim());

                    Map<Product, Integer> demandMap = new HashMap<>();
                    for (Task t : TaskController.getTasks().values()) {
                        LocalDate tDate = t.getStartDate();
                        if ((tDate.isEqual(start) || tDate.isAfter(start)) &&
                                (tDate.isEqual(end) || tDate.isBefore(end))) {
                            Product p = t.getRequiresdProduct();
                            demandMap.put(p, demandMap.getOrDefault(p, 0) + t.getRequiredQuantity());
                        }
                    }

                    // 1. Find Max Value
                    int maxQty = -1;
                    for (int qty : demandMap.values()) {
                        if (qty > maxQty) maxQty = qty;
                    }

                    // 2. Collect ALL products with that Max Value
                    List<Product> topProducts = new ArrayList<>();
                    if (maxQty > 0) {
                        for (Map.Entry<Product, Integer> entry : demandMap.entrySet()) {
                            if (entry.getValue() == maxQty) {
                                topProducts.add(entry.getKey());
                            }
                        }
                        refreshProductGrid(grid, topProducts);
                        JOptionPane.showMessageDialog(dialog, "Found " + topProducts.size() + " product(s) with max demand: " + maxQty);
                    } else {
                        JOptionPane.showMessageDialog(dialog, "No tasks found in this range.");
                    }
                    dialog.dispose();
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format.");
            }
        });

        btnPanel.add(btnReset);
        btnPanel.add(btnApply);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private static void showEditItemPopup(Item item, JLabel qtyLabel) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Stock: " + item.getName());
        dialog.setSize(350, 220);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);

        JPanel content = new JPanel();
        content.setBackground(industrialBlue);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField qtyField = new JTextField();
        qtyField.setMaximumSize(new Dimension(300, 35));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        JButton addBtn = new JButton("Add (+)");
        JButton removeBtn = new JButton("Remove (-)");

        for(JButton b : new JButton[]{addBtn, removeBtn}) {
            b.setBackground(activeOrange);
            b.setForeground(cleanWhite);
            b.setFocusPainted(false);
            btnPanel.add(b);
        }

        addBtn.addActionListener(e -> updateQty(item, qtyField, qtyLabel, true, dialog));
        removeBtn.addActionListener(e -> updateQty(item, qtyField, qtyLabel, false, dialog));

        JLabel title = new JLabel("Update Stock Quantity");
        title.setForeground(cleanWhite);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(10));
        content.add(qtyField);
        content.add(Box.createVerticalStrut(10));
        content.add(btnPanel);
        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private static void updateQty(Item item, JTextField field, JLabel label, boolean add, JDialog dialog) {
        try {
            int val = Integer.parseInt(field.getText().trim());
            ItemController.updateItemQTY(item.getId(), val, add);
            label.setText("Qty: " + item.getQuantity());
            dialog.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Invalid number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void showRequirementsPopup(Product product) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Requirements for: " + product.getName());
        dialog.setSize(600, 338);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(industrialBlue);

        BackgroundPanel content = new BackgroundPanel(new ImageIcon("assets/default-item.png"));
        content.setLayout(new BorderLayout());

        JPanel glassLayer = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        glassLayer.setOpaque(false);
        glassLayer.setBorder(new EmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(5, 0, 5, 0);

        JLabel title = new JLabel("Required Materials:");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(activeOrange);
        glassLayer.add(title, gbc);

        gbc.gridy++;
        glassLayer.add(Box.createVerticalStrut(10), gbc);
        gbc.gridy++;

        for (java.util.Map.Entry<model.Item, Integer> entry : product.getRequiredItems().entrySet()) {
            JLabel itemLabel = new JLabel("• " + entry.getKey().getName() + ": " + entry.getValue());
            itemLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
            itemLabel.setForeground(cleanWhite);
            glassLayer.add(itemLabel, gbc);
            gbc.gridy++;
        }

        content.add(glassLayer);
        panel.add(content);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private static void showItemsSearchPopup(JPanel itemsGrid) {
        JDialog dialog = new JDialog((Frame)null, "Filter Items", true);
        dialog.setSize(400, 480);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cleanWhite);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblName = new JLabel("Search by Name:");
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField txtName = new JTextField();
        txtName.setMaximumSize(new Dimension(400, 30));
        txtName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCat = new JLabel("Category:");
        lblCat.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComboBox<String> cmbCat = new JComboBox<>();
        cmbCat.addItem("All Categories");
        for(model.Category c : model.Category.values()) {
            cmbCat.addItem(c.name());
        }
        cmbCat.setMaximumSize(new Dimension(400, 30));
        cmbCat.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblStatus = new JLabel("Status (Select to filter):");
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox chkAvailable = new JCheckBox("Available (Qty > Min)");
        JCheckBox chkUnder = new JCheckBox("Under Min Threshold");
        JCheckBox chkEmpty = new JCheckBox("Out of Stock (Empty)");

        chkAvailable.setOpaque(false);
        chkUnder.setOpaque(false);
        chkEmpty.setOpaque(false);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBackground(activeOrange);
        btnSearch.setForeground(cleanWhite);

        JButton btnReset = new JButton("Show All");
        btnReset.setBackground(industrialBlue);
        btnReset.setForeground(cleanWhite);

        btnSearch.addActionListener(e -> {
            String query = txtName.getText().toLowerCase().trim();
            String selectedCat = (String) cmbCat.getSelectedItem();
            List<Item> filteredList = new ArrayList<>();

            for (Item item : ItemController.getItems().values()) {
                if (!query.isEmpty() && !item.getName().toLowerCase().contains(query)) continue;
                if (!"All Categories".equals(selectedCat) && !item.getCategory().name().equals(selectedCat)) continue;

                boolean isAvailable = item.getQuantity() > item.getMinThreshold();
                boolean isUnder = item.isUnderMinThreshold();
                boolean isEmpty = item.getQuantity() == 0;
                boolean matchesStatus = false;

                if (chkAvailable.isSelected() && isAvailable) matchesStatus = true;
                if (chkUnder.isSelected() && isUnder) matchesStatus = true;
                if (chkEmpty.isSelected() && isEmpty) matchesStatus = true;
                if (!chkAvailable.isSelected() && !chkUnder.isSelected() && !chkEmpty.isSelected()) matchesStatus = true;

                if (matchesStatus) filteredList.add(item);
            }
            refreshItemGrid(itemsGrid, filteredList);
            dialog.dispose();
        });

        btnReset.addActionListener(e -> {
            refreshItemGrid(itemsGrid, ItemController.getItems().values());
            dialog.dispose();
        });

        panel.add(lblName);
        panel.add(txtName);
        panel.add(Box.createVerticalStrut(15));
        panel.add(lblCat);
        panel.add(cmbCat);
        panel.add(Box.createVerticalStrut(15));
        panel.add(lblStatus);
        panel.add(chkAvailable);
        panel.add(chkUnder);
        panel.add(chkEmpty);
        panel.add(Box.createVerticalStrut(20));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(btnReset);
        btnPanel.add(btnSearch);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(btnPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}
