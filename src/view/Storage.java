package view;

import controller.ItemController;
import controller.ProductController;
import model.Item;
import model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class Storage {

    private static final Color industrialBlue = new Color(0x024971);
    private static final Color activeOrange = new Color(0xF7941D);
    private static final Color cleanWhite = Color.WHITE;

    // --- Custom Components ---

    static class BackgroundPanel extends JPanel {
        private final Image background;
        private final int arc = 30;
        private Color overlayColor = new Color(0, 0, 0, 140); // Default dark overlay

        public BackgroundPanel(ImageIcon icon) {
            this.background = (icon != null) ? icon.getImage() : null;
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(15, 20, 15, 20)); // Padding inside the card
        }

        // Allow changing overlay color (e.g. for Add button)
        public void setOverlayColor(Color c) {
            this.overlayColor = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Clip to rounded shape
            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setClip(roundedRect);

            if (background != null) {
                // Draw Image
                g2.drawImage(background, 0, 0, getWidth(), getHeight(), this);

                // Draw Overlay INSIDE the clip
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

    private static JPanel ProductView(JPanel container) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel productsGrid = new JPanel(new GridLayout(0, 3, 30, 40));
        productsGrid.setOpaque(false);
        productsGrid.setBorder(new EmptyBorder(30, 30, 30, 30));

        if (ProductController.getProducts() != null) {
            for (Product product : ProductController.getProducts().values()) {
                productsGrid.add(createProductPanel(product, productsGrid));
            }
        }

        // Add Card
        BackgroundPanel addCard = new BackgroundPanel(new ImageIcon("assets/product.png"));
        addCard.setPreferredSize(new Dimension(400, 255));
        addCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addCard.setOverlayColor(new Color(0, 0, 0, 80)); // Lighter overlay for add card if desired

        // Use GridBag to center the plus icon
        addCard.setLayout(new GridBagLayout());
        addCard.add(new JLabel(new ImageIcon("assets/add-orange.png")));

        addCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                AddProduct.show();
            }
        });
        productsGrid.add(addCard);

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(productsGrid, BorderLayout.NORTH);

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

    private static JPanel ItemView(JPanel container) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel itemsGrid = new JPanel(new GridLayout(0, 3, 30, 40));
        itemsGrid.setOpaque(false);
        itemsGrid.setBorder(new EmptyBorder(30, 30, 30, 30));

        if (ItemController.getItems() != null) {
            for (Item item : ItemController.getItems().values()) {
                itemsGrid.add(createItemPanel(item, itemsGrid));
            }
        }

        BackgroundPanel addCard = new BackgroundPanel(new ImageIcon("assets/default-item.png"));
        addCard.setPreferredSize(new Dimension(400, 255));
        addCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addCard.setLayout(new GridBagLayout());
        addCard.add(new JLabel(new ImageIcon("assets/add-orange.png")));

        addCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                AddItem.show();
            }
        });
        itemsGrid.add(addCard);

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(itemsGrid, BorderLayout.NORTH);

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

    private static JButton createBackButton(JPanel container) {
        JButton backBtn = new JButton(new ImageIcon("assets/back.png"));
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> ((CardLayout) container.getLayout()).show(container, "MENU"));
        return backBtn;
    }

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

        // Remove the inner overlay panel, use the BackgroundPanel itself
        panel.setLayout(new BorderLayout());

        // --- Top: Delete Button ---
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

        // --- Center: Info ---
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

        // --- Bottom: Edit Button ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setOpaque(false);
        JButton editBtn = new JButton(new ImageIcon("assets/edit.png"));
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

        // --- Top: Delete Button ---
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

        // --- Center: Info ---
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

        JLabel timeLabel = new JLabel("Est. Time: " + product.getEstimatedTime() + "min");
        timeLabel.setFont(valFont);
        timeLabel.setForeground(cleanWhite);


        centerPanel.add(nameLabel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(idLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(timeLabel);

        // --- Bottom: Requirements Button ---
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
        content.setLayout(new GridBagLayout());

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
}
