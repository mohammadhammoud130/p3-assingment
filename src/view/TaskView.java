package view;

import controller.TaskController;
import model.ProductLine;
import model.Task;
import model.Status;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;

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

    private static RoundedTaskPanel createTaskPanel(Task task) {
        RoundedTaskPanel card = new RoundedTaskPanel("assets/task.png");
        card.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        contentPanel.setLayout(new BorderLayout());

        // --- TOP Panel (Info Left, Delete Right) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel clientLabel = new JLabel("Client: " + task.getClient());
        clientLabel.setFont(new Font("Serif", Font.BOLD, 16));
        clientLabel.setForeground(cleanWhite);
        clientLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Font valueFont = new Font("Serif", Font.PLAIN, 14);
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
            int choice1 = JOptionPane.showConfirmDialog(null,
                    "Delete this task? Unused materials will be returned.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (choice1 == JOptionPane.YES_OPTION) {
                TaskController.deleteTask(task.getId());
            }
        });

        JPanel deleteWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        deleteWrapper.setOpaque(false);
        deleteWrapper.add(deleteBtn);

        topPanel.add(infoPanel, BorderLayout.CENTER);
        topPanel.add(deleteWrapper, BorderLayout.EAST);

        // --- BOTTOM Panel (Progress Left, Status Right) ---
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
                case ACTIVE: statusIconLabel.setIcon(new ImageIcon("assets/status-active.png")); break;
                case PAUSED: statusIconLabel.setIcon(new ImageIcon("assets/status-paused.png")); break;
                case FINISHED: statusIconLabel.setIcon(new ImageIcon("assets/status-finished.png")); break;
                default: statusIconLabel.setIcon(null); break;
            }
        };
        updateIcon.run();

        JPopupMenu statusMenu = new JPopupMenu();
        JMenuItem setActive = new JMenuItem("Set Active", new ImageIcon("assets/status-active.png"));
        JMenuItem setPaused = new JMenuItem("Set Paused", new ImageIcon("assets/status-paused.png"));
        JMenuItem setFinished = new JMenuItem("Set Finished", new ImageIcon("assets/status-finished.png"));

        setActive.addActionListener(e -> { TaskController.updateTaskStatus(task.getId(), Status.ACTIVE); updateIcon.run(); });
        setPaused.addActionListener(e -> { TaskController.updateTaskStatus(task.getId(), Status.PAUSED); updateIcon.run(); });
        setFinished.addActionListener(e -> { TaskController.updateTaskStatus(task.getId(), Status.FINISHED); updateIcon.run(); });

        statusMenu.add(setActive);
        statusMenu.add(setPaused);
        statusMenu.add(setFinished);

        statusIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                statusMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        bottomPanel.add(progressBar, BorderLayout.CENTER);
        bottomPanel.add(statusIconLabel, BorderLayout.EAST);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }

    // Pass the Line ID so we know where to add the task
    private static RoundedTaskPanel createAddPanel(int lineId) {
        RoundedTaskPanel addCard = new RoundedTaskPanel("assets/task.png");
        addCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addCard.setLayout(new GridBagLayout());
        addCard.add(new JLabel(new ImageIcon("assets/add-orange.png")));

        addCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Call the AddTask popup with the specific line ID
                AddTask.show(lineId);
            }
        });
        return addCard;
    }

    public static void showTasksForLine(JFrame frame, JPanel leftPanel, ProductLine line) {
        stopTimer();
        JPanel mainView = new JPanel(new BorderLayout());
        mainView.add(leftPanel, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(cleanWhite);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 20));
        header.setBackground(cleanWhite);
        JLabel titleLabel = new JLabel("Tasks for: " + line.getName());
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(industrialBlue);
        header.add(titleLabel);
        contentPanel.add(header, BorderLayout.NORTH);

        JPanel taskGrid = new JPanel(new GridLayout(0, 3, 30, 30));
        taskGrid.setBackground(cleanWhite);

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

        updateTimer = new Timer(1000, e -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            int value = vertical.getValue();
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

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 20));
        header.setBackground(cleanWhite);
        JLabel titleLabel = new JLabel("All Production Tasks");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(industrialBlue);
        header.add(titleLabel);
        contentPanel.add(header, BorderLayout.NORTH);

        JPanel taskGrid = new JPanel(new GridLayout(0, 3, 30, 30));
        taskGrid.setBackground(cleanWhite);

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

    private static void populateGridForLine(JPanel grid, ProductLine line) {
        grid.removeAll();
        if (line.getTasks() != null && !line.getTasks().isEmpty()) {
            for (Task task : line.getTasks()) {
                grid.add(createTaskPanel(task));
            }
        }
        // Add panel WITH the current Line ID
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
            emptyPanel.add(new JLabel("No tasks found in the system."));
            grid.add(emptyPanel);
        }
        // Note: No add panel here, as requested
    }
}
