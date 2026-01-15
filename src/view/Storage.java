package view;

import controller.ItemController;
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

    // ---------------- Custom Components ----------------

    // 1. Panel for Item Cards (with Background Image & Rounded Corners)
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

    // 2. Main Background GIF Panel
    static class GifPanel extends JPanel {
        private final Image gifImage;

        public GifPanel(String path) {
            this.gifImage = new ImageIcon(path).getImage();
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (gifImage != null) {
                g.drawImage(gifImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // 3. Menu Button Panel
    static class RoundedImagePanel extends JPanel {
        private final Image image;
        private final int arc = 40;

        public RoundedImagePanel(String imagePath) {
            this.image = new ImageIcon(imagePath).getImage();
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, w, h, arc, arc);
            g2.setClip(roundedRect);

            g2.drawImage(image, 0, 0, w, h, this);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---------------- Main Logic ----------------

    public static void storage(JFrame frame, JPanel leftPanel) {

        // 1. Root Container (GIF Background)
        GifPanel rootPanel = new GifPanel("assets/storage.gif");

        // 2. Left Menu (WEST)
        JPanel westContainer = new JPanel(new BorderLayout());
        westContainer.setOpaque(false);
        westContainer.add(leftPanel, BorderLayout.CENTER);
        rootPanel.add(westContainer, BorderLayout.WEST);

        // 3. Center Content (CardLayout)
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setOpaque(false);

        // ---------------- Views Setup ----------------

        // View A: The Menu (Two Buttons)
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 20);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        Dimension buttonSize = new Dimension(550, 850);

        RoundedImagePanel productBtn = new RoundedImagePanel("assets/Product-storage.jpg");
        productBtn.setPreferredSize(buttonSize);
        productBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CardLayout cl = (CardLayout) contentPanel.getLayout();
                cl.show(contentPanel, "PRODUCT_VIEW");
            }
        });

        RoundedImagePanel itemBtn = new RoundedImagePanel("assets/Items-storage.jpg");
        itemBtn.setPreferredSize(buttonSize);
        itemBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CardLayout cl = (CardLayout) contentPanel.getLayout();
                cl.show(contentPanel, "ITEM_VIEW");
            }
        });

        menuPanel.add(productBtn, gbc);
        menuPanel.add(itemBtn, gbc);

        // Add Views
        contentPanel.add(menuPanel, "MENU");
        contentPanel.add(ProductView(contentPanel), "PRODUCT_VIEW");
        contentPanel.add(ItemView(contentPanel), "ITEM_VIEW");

        rootPanel.add(contentPanel, BorderLayout.CENTER);

        frame.setContentPane(rootPanel);
        frame.revalidate();
        frame.repaint();
    }

    // ---------------- Sub-Window Generators ----------------

    private static JPanel ProductView(JPanel container) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // --- 1. Products Grid Container (Grid Layout 3 Columns) ---
        JPanel productsGrid = new JPanel(new GridLayout(0, 3, 30, 40));
        productsGrid.setOpaque(false);
        productsGrid.setBorder(new EmptyBorder(30, 30, 30, 30));

        // --- 2. Populate Grid from Controller ---
        // Assuming ProductController exists and has getProducts()
        if (controller.ProductController.getProducts() != null) {
            for (model.Product product : controller.ProductController.getProducts().values()) {
                productsGrid.add(createProductPanel(product));
            }
        }

        // --- 3. Add "Add Product" Card at the end ---
        BackgroundPanel addCard = new BackgroundPanel(new ImageIcon("assets/product.png"));
        addCard.setOpaque(false);
        addCard.setPreferredSize(new Dimension(400, 255));
        addCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addCard.setLayout(new GridBagLayout()); // Center the icon

        JLabel plusLabel = new JLabel(new ImageIcon("assets/add-orange.png"));
        plusLabel.setPreferredSize(new Dimension(32, 32));
        addCard.add(plusLabel);

        addCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "Add Product Functionality");
            }
        });

        productsGrid.add(addCard);

        // --- 4. Scroll Pane (Transparent) ---
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(productsGrid, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        // --- 5. Bottom Bar (Back Button) ---
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(false);
        JButton backBtn = createBackButton(container);
        bottomBar.add(backBtn);

        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    private static JPanel ItemView(JPanel container) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // --- 1. Items Grid Container (Grid Layout 3 Columns) ---
        // Using standard JPanel for grid, ensuring background is transparent to see GIF
        JPanel itemsGrid = new JPanel(new GridLayout(0, 3, 30, 40));
        itemsGrid.setOpaque(false); // Transparent
        itemsGrid.setBorder(new EmptyBorder(30, 30, 30, 30));

        // --- 2. Populate Grid from Controller ---
        if (ItemController.getItems() != null) {
            for (Item item : ItemController.getItems().values()) {
                itemsGrid.add(createItemPanel(item));
            }
        }

        // --- 3. Add "Add Item" Card at the end ---
        BackgroundPanel addCard = new BackgroundPanel(new ImageIcon("assets/default-item.png"));
        addCard.setOpaque(false);
        addCard.setPreferredSize(new Dimension(400, 255));
        addCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addCard.setLayout(new GridBagLayout()); // Center the icon

        JLabel plusLabel = new JLabel(new ImageIcon("assets/add-orange.png"));
        plusLabel.setPreferredSize(new Dimension(32, 32));
        addCard.add(plusLabel);

        addCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "Add Item Functionality");
            }
        });

        itemsGrid.add(addCard);

        // --- 4. Scroll Pane (Transparent) ---
        // We wrap the grid in a panel that aligns it to the top (North) to prevent vertical stretching
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(itemsGrid, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        // --- 5. Bottom Bar (Back Button) ---
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(false);
        JButton backBtn = createBackButton(container);
        bottomBar.add(backBtn);

        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    private static JButton createBackButton(JPanel container) {
        JButton backBtn = new JButton();
        backBtn.setPreferredSize(new Dimension(32, 32));
        backBtn.setIcon(new ImageIcon("assets/back.png"));
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        backBtn.addActionListener(e -> {
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "MENU");
        });
        return backBtn;
    }

    private static BackgroundPanel createItemPanel(Item item) {
        // --- Background Logic ---
        ImageIcon bg;
        if (item.getCategory() != null) {
            switch (item.getCategory().name().toLowerCase()) {
                case "fabric":  bg = new ImageIcon("assets/fabric.png"); break;
                case "thread":  bg = new ImageIcon("assets/threads.png"); break;
                case "leather": bg = new ImageIcon("assets/leather.png"); break;
                case "buttons", "zippers": bg = new ImageIcon("assets/bottons-zippers.png"); break;
                default:        bg = new ImageIcon("assets/default-item.png");
            }
        } else {
            bg = new ImageIcon("assets/default-item.png");
        }

        // --- Panel Setup ---
        BackgroundPanel panel = new BackgroundPanel(bg);
        panel.setLayout(new GridBagLayout()); // Use GridBag to position text

        Dimension cardSize = new Dimension(400, 255);
        panel.setPreferredSize(cardSize);
        panel.setMinimumSize(cardSize);
        panel.setMaximumSize(cardSize);

        // --- Labels (White Text for Contrast) ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel idLabel = new JLabel("ID: " + item.getId());
        JLabel nameLabel = new JLabel("Name: " + item.getName());
        JLabel qtyLabel = new JLabel("Qty: " + item.getQuantity());
        JLabel minLabel = new JLabel("Min: " + item.getMinThreshold());

        Font font = new Font("SansSerif", Font.BOLD, 14);
        Color textColor = Color.WHITE;

        idLabel.setFont(font); idLabel.setForeground(textColor);
        nameLabel.setFont(font); nameLabel.setForeground(textColor);
        qtyLabel.setFont(font); qtyLabel.setForeground(textColor);
        minLabel.setFont(font); minLabel.setForeground(textColor);

        gbc.gridy = 0; panel.add(idLabel, gbc);
        gbc.gridy = 1; panel.add(nameLabel, gbc);
        gbc.gridy = 2; panel.add(qtyLabel, gbc);
        gbc.gridy = 3; panel.add(minLabel, gbc);

        return panel;
    }

    private static BackgroundPanel createProductPanel(model.Product product) {
        // --- Panel Setup ---
        BackgroundPanel panel = new BackgroundPanel(new ImageIcon("assets/product.png"));
        panel.setLayout(new GridBagLayout());

        Dimension cardSize = new Dimension(400, 255);
        panel.setPreferredSize(cardSize);
        panel.setMinimumSize(cardSize);
        panel.setMaximumSize(cardSize);

        // --- Labels ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; // Column 0

        Font font = new Font("SansSerif", Font.BOLD, 14);
        Color textColor = Color.WHITE;

        JLabel idLabel = new JLabel("ID: " + product.getId());
        JLabel nameLabel = new JLabel("Name: " + product.getName());
        JLabel timeLabel = new JLabel("Est. Time: " + product.getEstimatedTime() + "h");

        idLabel.setFont(font); idLabel.setForeground(textColor);
        nameLabel.setFont(font); nameLabel.setForeground(textColor);
        timeLabel.setFont(font); timeLabel.setForeground(textColor);

        gbc.gridy = 0; panel.add(idLabel, gbc);
        gbc.gridy = 1; panel.add(nameLabel, gbc);
        gbc.gridy = 2; panel.add(timeLabel, gbc);

        // --- View Requirements Button ---
        JButton reqBtn = new JButton("View Requirements");
        reqBtn.setFocusPainted(false);
        reqBtn.setBackground(activeOrange); // Orange Theme
        reqBtn.setForeground(Color.WHITE);
        reqBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        reqBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        reqBtn.addActionListener(e -> showRequirementsPopup(product));

        gbc.gridy = 3;
        gbc.insets = new Insets(15, 10, 5, 10); // Extra top gap
        panel.add(reqBtn, gbc);

        return panel;
    }

    private static void showRequirementsPopup(model.Product product) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Requirements for: " + product.getName());
        dialog.setSize(600, 338);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null); // Center on screen
        dialog.setModal(true); // Block other windows until closed
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 45, 64));
        // Use standard BackgroundPanel with default item background
        BackgroundPanel content = new BackgroundPanel(new ImageIcon("assets/default-item.png"));
        content.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Title
        JLabel title = new JLabel("Required Materials:");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        content.add(title, gbc);
        gbc.gridy++;

        // List Items
        if (product.getRequiredItems().isEmpty()) {
            JLabel empty = new JLabel("(No requirements listed)");
            empty.setForeground(Color.WHITE);
            content.add(empty, gbc);
        } else {
            for (java.util.Map.Entry<model.Item, Integer> entry : product.getRequiredItems().entrySet()) {
                String itemName = entry.getKey().getName();
                Integer qty = entry.getValue();

                JLabel itemLabel = new JLabel("• " + itemName + ": " + qty);
                itemLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                itemLabel.setForeground(Color.WHITE);
                content.add(itemLabel, gbc);
                gbc.gridy++;
            }
        }
        panel.add(content);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }



}
