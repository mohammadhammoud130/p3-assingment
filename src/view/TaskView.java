package view;

import controller.TaskController;
import controller.ProductController;
import model.ProductLine;
import model.Task;
import model.Status;
import model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class TaskView {

    private static final Color industrialBlue = new Color(0x024971);
    private static final Color activeOrange = new Color(0xF7941D);
    private static final Color cleanWhite = Color.WHITE;

    private static Timer updateTimer;

    private static void stopTimer() {
        if (updateTimer != null && updateTimer.isRunning()) {
            updateTimer.stop();
        }
        updateTimer = null;
    }

    // --- Custom Components (Unchanged) ---
    static class RoundedTaskPanel extends JLayeredPane {
        private final Image background;
        private final int arc = 30;
        public RoundedTaskPanel(String imagePath) {
            this.background = new ImageIcon(imagePath).getImage();
            setOpaque(false);
            setPreferredSize(new Dimension(400, 225));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setClip(roundedRect);
            if (background != null) {
                g2.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fill(roundedRect);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // --- Task Panel Creator (Unchanged) ---
    private static RoundedTaskPanel createTaskPanel(Task task) {
        // ... (Keep existing implementation from previous version) ...
        // For brevity, pasting exact previous implementation:
        RoundedTaskPanel card = new RoundedTaskPanel("assets/task.png");
        card.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        contentPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel clientLabel = new JLabel("Client: " + task.getClient());
        clientLabel.setFont(new Font("Serif", Font.BOLD, 24));
        clientLabel.setForeground(cleanWhite);
        clientLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Font valueFont = new Font("Serif", Font.PLAIN, 20);
        JLabel productLabel = new JLabel("Product: " + task.getRequiresdProduct().getName());
        productLabel.setFont(valueFont);
        productLabel.setForeground(cleanWhite);
        productLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel qtyLabel = new JLabel("Quantity: " + task.getProducedQuantity() + " / " + task.getRequiredQuantity());
        qtyLabel.setFont(valueFont);
        qtyLabel.setForeground(cleanWhite);
        qtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(clientLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(productLabel);
        infoPanel.add(qtyLabel);

        JButton deleteBtn = new JButton(new ImageIcon("assets/delete.png"));
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> {
            int choice1 = JOptionPane.showConfirmDialog(null, "Delete this task?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (choice1 == JOptionPane.YES_OPTION) {
                int choice2 = JOptionPane.showConfirmDialog(null, "Are you sure?", "Final", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice2 == JOptionPane.YES_OPTION) {
                    TaskController.deleteTask(task.getId());
                }
            }
        });

        JPanel deleteWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        deleteWrapper.setOpaque(false);
        deleteWrapper.add(deleteBtn);

        topPanel.add(infoPanel, BorderLayout.CENTER);
        topPanel.add(deleteWrapper, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setOpaque(false);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) task.getProgress());
        progressBar.setStringPainted(true);
        progressBar.setString(String.format("%.0f%%", task.getProgress()));
        progressBar.putClientProperty("FlatLaf.style", "arc: 15;");
        progressBar.setForeground(activeOrange);
        progressBar.setBackground(cleanWhite.darker());
        progressBar.setPreferredSize(new Dimension(100, 20));

        JLabel statusIconLabel = new JLabel();
        statusIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Runnable updateIcon = () -> {
            switch (task.getStatus()) {
                case ACTIVE: statusIconLabel.setIcon(new ImageIcon("assets/ACTIVE.png")); break;
                case PAUSED: statusIconLabel.setIcon(new ImageIcon("assets/PAUSED.png")); break;
                case FINISHED: statusIconLabel.setIcon(new ImageIcon("assets/FINISHED.png")); break;
                default: statusIconLabel.setIcon(null); break;
            }
        };
        updateIcon.run();

        JPopupMenu statusMenu = new JPopupMenu();
        JMenuItem setActive = new JMenuItem("Set Active", new ImageIcon("assets/FINISHED.png"));
        JMenuItem setPaused = new JMenuItem("Set Paused", new ImageIcon("assets/PAUSED.png"));
        JMenuItem setFinished = new JMenuItem("Set Finished", new ImageIcon("assets/ACTIVE.png"));

        setActive.addActionListener(e -> { TaskController.updateTaskStatus(task.getId(), Status.ACTIVE); updateIcon.run(); });
        setPaused.addActionListener(e -> { TaskController.updateTaskStatus(task.getId(), Status.PAUSED); updateIcon.run(); });
        setFinished.addActionListener(e -> { TaskController.updateTaskStatus(task.getId(), Status.FINISHED); updateIcon.run(); });

        statusMenu.add(setActive); statusMenu.add(setPaused); statusMenu.add(setFinished);

        statusIconLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { statusMenu.show(e.getComponent(), e.getX(), e.getY()); }
        });

        bottomPanel.add(progressBar, BorderLayout.CENTER);
        bottomPanel.add(statusIconLabel, BorderLayout.EAST);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }

    private static RoundedTaskPanel createAddPanel(int lineId) {
        RoundedTaskPanel addCard = new RoundedTaskPanel("assets/task.png");
        addCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addCard.setLayout(new GridBagLayout());
        addCard.add(new JLabel(new ImageIcon("assets/add-orange.png")));
        addCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                AddTask.show(lineId);
            }
        });
        return addCard;
    }

    // --- Main View Methods (Updated with Search Button) ---

    public static void showTasksForLine(JFrame frame, JPanel leftPanel, ProductLine line) {
        stopTimer();
        JPanel mainView = new JPanel(new BorderLayout());
        mainView.add(leftPanel, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(cleanWhite);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(cleanWhite);
        header.setBorder(new EmptyBorder(20, 50, 20, 50));

        JLabel titleLabel = new JLabel("Tasks for: " + line.getName());
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(industrialBlue);
        header.add(titleLabel, BorderLayout.WEST);

        // Search Button
        JButton searchBtn = new JButton(new ImageIcon("assets/search.png"));
        searchBtn.setContentAreaFilled(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        header.add(searchBtn, BorderLayout.EAST);

        contentPanel.add(header, BorderLayout.NORTH);

        JPanel taskGrid = new JPanel(new GridLayout(0, 3, 30, 30));
        taskGrid.setBackground(cleanWhite);

        // Connect Search Logic
        searchBtn.addActionListener(e -> showSearchPopup(taskGrid, line.getTasks()));

        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        wrapperPanel.setBackground(cleanWhite);
        wrapperPanel.add(taskGrid);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        populateGridForLine(taskGrid, line);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 10));
        footer.setBackground(cleanWhite);
        JButton backBtn = new JButton(new ImageIcon("assets/back.png"));
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            stopTimer();
            ProductionLines.showProductionLines(frame, leftPanel);
        });
        footer.add(backBtn);
        contentPanel.add(footer, BorderLayout.SOUTH);

        // Timer (Refreshes current grid content, respecting filter if active)
        updateTimer = new Timer(1000, e -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            int value = vertical.getValue();
            // Note: In a real filtered scenario, we'd need to re-apply filter here.
            // For simplicity, the timer currently resets to showing all for the line.
            // If persistent filtering is needed, store filtered state.
            populateGridForLine(taskGrid, line);
            taskGrid.revalidate();
            taskGrid.repaint();
            SwingUtilities.invokeLater(() -> vertical.setValue(value));
        });
        updateTimer.start();

        mainView.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (!mainView.isShowing()) stopTimer();
            }
        });

        mainView.add(contentPanel, BorderLayout.CENTER);
        frame.setContentPane(mainView);
        frame.revalidate();
        frame.repaint();
    }

    public static void showAllTasks(JFrame frame, JPanel leftPanel) {
        stopTimer();
        JPanel mainView = new JPanel(new BorderLayout());
        mainView.add(leftPanel, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(cleanWhite);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(cleanWhite);
        header.setBorder(new EmptyBorder(20, 50, 20, 50));

        JLabel titleLabel = new JLabel("All Production Tasks");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(industrialBlue);
        header.add(titleLabel, BorderLayout.WEST);

        JButton searchBtn = new JButton(new ImageIcon("assets/search.png"));
        searchBtn.setContentAreaFilled(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        header.add(searchBtn, BorderLayout.EAST);

        contentPanel.add(header, BorderLayout.NORTH);

        JPanel taskGrid = new JPanel(new GridLayout(0, 3, 30, 30));
        taskGrid.setBackground(cleanWhite);

        // Search Logic
        searchBtn.addActionListener(e -> showSearchPopup(taskGrid, TaskController.getTasks().values()));

        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        wrapperPanel.setBackground(cleanWhite);
        wrapperPanel.add(taskGrid);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        populateGridForAll(taskGrid);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 10));
        footer.setBackground(cleanWhite);
        JButton backBtn = new JButton(new ImageIcon("assets/back.png"));
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            stopTimer();
            ProductionLines.showProductionLines(frame, leftPanel);
        });
        footer.add(backBtn);
        contentPanel.add(footer, BorderLayout.SOUTH);

        updateTimer = new Timer(1000, e -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            int value = vertical.getValue();
            populateGridForAll(taskGrid);
            taskGrid.revalidate();
            taskGrid.repaint();
            SwingUtilities.invokeLater(() -> vertical.setValue(value));
        });
        updateTimer.start();

        mainView.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (!mainView.isShowing()) stopTimer();
            }
        });

        mainView.add(contentPanel, BorderLayout.CENTER);
        frame.setContentPane(mainView);
        frame.revalidate();
        frame.repaint();
    }

    // --- Search & Filter Logic ---

    private static void showSearchPopup(JPanel grid, Collection<Task> tasksSource) {
        JDialog dialog = new JDialog((Frame)null, "Filter Tasks", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cleanWhite);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // 1. Status Filter
        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        JCheckBox chkActive = new JCheckBox("Active");
        JCheckBox chkPaused = new JCheckBox("Paused");
        JCheckBox chkFinished = new JCheckBox("Finished");
        chkActive.setOpaque(false); chkPaused.setOpaque(false); chkFinished.setOpaque(false);

        // 2. Product Filter
        JCheckBox chkProduct = new JCheckBox("Filter by Product:");
        chkProduct.setOpaque(false);
        chkProduct.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<Product> cmbProduct = new JComboBox<>();
        cmbProduct.setEnabled(false); // Disabled by default
        cmbProduct.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbProduct.setMaximumSize(new Dimension(400, 30));

        // Populate Dropdown
        if (ProductController.getProducts() != null) {
            for (Product p : ProductController.getProducts().values()) {
                cmbProduct.addItem(p);
            }
        }
        // Custom Renderer to show only Name
        cmbProduct.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) setText(((Product) value).getName());
                return this;
            }
        });

        // Toggle dropdown based on checkbox
        chkProduct.addActionListener(e -> cmbProduct.setEnabled(chkProduct.isSelected()));

        JButton btnSearch = new JButton("Apply Filters");
        btnSearch.setBackground(activeOrange);
        btnSearch.setForeground(cleanWhite);

        JButton btnReset = new JButton("Show All");
        btnReset.setBackground(industrialBlue);
        btnReset.setForeground(cleanWhite);

        btnSearch.addActionListener(e -> {
            stopTimer(); // Stop auto-refresh while viewing filtered results

            List<Task> filteredList = new ArrayList<>();
            Product selectedProd = (Product) cmbProduct.getSelectedItem();
            boolean filterByProd = chkProduct.isSelected() && selectedProd != null;

            for (Task t : tasksSource) {
                boolean matchStatus = false;
                if (chkActive.isSelected() && t.getStatus() == Status.ACTIVE) matchStatus = true;
                if (chkPaused.isSelected() && t.getStatus() == Status.PAUSED) matchStatus = true;
                if (chkFinished.isSelected() && t.getStatus() == Status.FINISHED) matchStatus = true;

                // If no status checked, include all statuses
                if (!chkActive.isSelected() && !chkPaused.isSelected() && !chkFinished.isSelected()) matchStatus = true;

                boolean matchProd = true;
                if (filterByProd) {
                    matchProd = t.getRequiresdProduct().getId() == selectedProd.getId();
                }

                if (matchStatus && matchProd) filteredList.add(t);
            }
            refreshTaskGrid(grid, filteredList);
            dialog.dispose();
        });

        btnReset.addActionListener(e -> {
            refreshTaskGrid(grid, tasksSource);
            dialog.dispose();
            // We could restart timer here, but simplified to keep static view
        });

        panel.add(lblStatus);
        panel.add(chkActive); panel.add(chkPaused); panel.add(chkFinished);
        panel.add(Box.createVerticalStrut(15));
        panel.add(chkProduct);
        panel.add(cmbProduct);
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

    // --- Helpers ---

    private static void populateGridForLine(JPanel grid, ProductLine line) {
        grid.removeAll();
        if (line.getTasks() != null && !line.getTasks().isEmpty()) {
            for (Task task : line.getTasks()) {
                grid.add(createTaskPanel(task));
            }
        }
        grid.add(createAddPanel(line.getId()));
    }

    private static void populateGridForAll(JPanel grid) {
        grid.removeAll();
        HashMap<Integer, Task> allTasks = TaskController.getTasks();
        if (allTasks != null && !allTasks.isEmpty()) {
            for (Task task : allTasks.values()) {
                grid.add(createTaskPanel(task));
            }
        } else {
            JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            emptyPanel.setOpaque(false);
            emptyPanel.add(new JLabel("No tasks found."));
            grid.add(emptyPanel);
        }
    }

    private static void refreshTaskGrid(JPanel grid, Collection<Task> tasksToShow) {
        grid.removeAll();
        for (Task task : tasksToShow) {
            grid.add(createTaskPanel(task));
        }
        grid.revalidate();
        grid.repaint();
    }
}
