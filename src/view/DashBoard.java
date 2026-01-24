package view;

import com.formdev.flatlaf.FlatLightLaf;
import controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashBoard extends JPanel{
  private static JPanel leftPanel;
  private static final Color industrialBlue = new Color(0x024971);
    private static final Color activeOrange = new Color(0xF7941D);
    private static final Color cleanWhite = Color.WHITE;



    public static void productionSupervisorDashBoard(JFrame frame, String userName){

        FlatLightLaf.setup();
        UIManager.put("Button.arc", 20);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("Button.background", activeOrange);
        UIManager.put("Button.foreground", cleanWhite);
        UIManager.put("Label.foreground", industrialBlue);

        JPanel panel = new JPanel(new BorderLayout());

        // LEFT PANEL (static)
        leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(industrialBlue);

        // Enforce exact width of 350px
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setMinimumSize(new Dimension(300, 0));
        leftPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        leftPanel.setLayout(new BorderLayout());

        // Logo Panel at the TOP
        JPanel logoBox = new JPanel(new BorderLayout());
        logoBox.setBackground(activeOrange);
        logoBox.setPreferredSize(new Dimension(300, 50));
        logoBox.setMaximumSize(new Dimension(300, 50));
        logoBox.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel textLabel = new JLabel("TWIZE FACTORY");
        textLabel.setHorizontalAlignment(SwingConstants.LEFT);
        textLabel.setVerticalAlignment(SwingConstants.CENTER);
        textLabel.setForeground(Color.WHITE);
        textLabel.putClientProperty("FlatLaf.style", "font: bold 16 serif;");
        logoBox.add(textLabel, BorderLayout.CENTER);

        JLabel iconLabel = new JLabel(new ImageIcon("assets/32px-logo.jpg"));
        iconLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        logoBox.add(iconLabel, BorderLayout.EAST);

        // Add logo to NORTH of left panel
        leftPanel.add(logoBox, BorderLayout.NORTH);

        JPanel buttonBox = new JPanel();
        buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.Y_AXIS));
        buttonBox.setOpaque(false);

        SideButton productionLines = new SideButton("PRODUCTION LINES", new ImageIcon("assets/productionlines.png"));
        buttonBox.add(productionLines);
        SideButton inventory = new SideButton("INVENTORY",new ImageIcon("assets/inventory.png"));
        buttonBox.add(inventory);
        buttonBox.add(Box.createVerticalGlue()); // pushes next button to bottom
        SideButton user = new SideButton("USER",new ImageIcon("assets/users-orange.png"));
        buttonBox.add(user);
        SideButton logout = new SideButton("LOGOUT",new ImageIcon("assets/user-logout.png"));
        buttonBox.add(logout); // pinned to bottom



        leftPanel.add(buttonBox, BorderLayout.CENTER);

        panel.add(leftPanel, BorderLayout.WEST);


        logout.addActionListener(e -> {
            UserController.logout(userName);
            JOptionPane.showMessageDialog(frame,"You logged out ! ","info",JOptionPane.INFORMATION_MESSAGE,new ImageIcon("assets/32px-logo.jpg"));
        });
        user.addActionListener(e ->{

            UserInfo.userInfo(frame,leftPanel,userName);
        });
        inventory.addActionListener(e ->{
            Storage.storage(frame , leftPanel);
        });
        productionLines.addActionListener(e->{
            ProductionLines.showProductionLines(frame,leftPanel);
        });

        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();

    }

    public static  void  managerDashBoard(JFrame frame,String userName){


        FlatLightLaf.setup();
          UIManager.put("Button.arc", 20);
          UIManager.put("TextComponent.arc", 10);
          UIManager.put("Component.focusWidth", 2);
          UIManager.put("Button.background", activeOrange);
          UIManager.put("Button.foreground", cleanWhite);
          UIManager.put("Label.foreground", industrialBlue);

        JPanel panel = new JPanel(new BorderLayout());

        // LEFT PANEL (static)
         leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(industrialBlue);

        // Enforce exact width of 350px
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setMinimumSize(new Dimension(300, 0));
        leftPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        leftPanel.setLayout(new BorderLayout());

        // Logo Panel at the TOP
        JPanel logoBox = new JPanel(new BorderLayout());
        logoBox.setBackground(activeOrange);
        logoBox.setPreferredSize(new Dimension(300, 50));
        logoBox.setMaximumSize(new Dimension(300, 50));
        logoBox.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel textLabel = new JLabel("TWIZE FACTORY");
        textLabel.setHorizontalAlignment(SwingConstants.LEFT);
        textLabel.setVerticalAlignment(SwingConstants.CENTER);
        textLabel.setForeground(Color.WHITE);
        textLabel.putClientProperty("FlatLaf.style", "font: bold 16 serif;");
        logoBox.add(textLabel, BorderLayout.CENTER);

        JLabel iconLabel = new JLabel(new ImageIcon("assets/32px-logo.jpg"));
        iconLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        logoBox.add(iconLabel, BorderLayout.EAST);

        // Add logo to NORTH of left panel
        leftPanel.add(logoBox, BorderLayout.NORTH);

        JPanel buttonBox = new JPanel();
        buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.Y_AXIS));
        buttonBox.setOpaque(false);

        SideButton productionLines = new SideButton("PRODUCTION LINES", new ImageIcon("assets/productionlines.png"));
        buttonBox.add(productionLines);
        SideButton orders = new SideButton("ORDERS",new ImageIcon("assets/orders.png"));
        buttonBox.add(orders);
        SideButton users = new SideButton("USERS",new ImageIcon("assets/users-orange.png"));
        buttonBox.add(users);
        SideButton inventory = new SideButton("INVENTORY",new ImageIcon("assets/inventory.png"));
        buttonBox.add(inventory);
        buttonBox.add(Box.createVerticalGlue()); // pushes next button to bottom
        SideButton logout = new SideButton("LOGOUT",new ImageIcon("assets/user-logout.png"));
        buttonBox.add(logout); // pinned to bottom



        leftPanel.add(buttonBox, BorderLayout.CENTER);

        panel.add(leftPanel, BorderLayout.WEST);


        logout.addActionListener(e -> {
            UserController.logout(userName);
            JOptionPane.showMessageDialog(frame,"You logged out ! ","info",JOptionPane.INFORMATION_MESSAGE,new ImageIcon("assets/32px-logo.jpg"));
        });
        users.addActionListener(e ->{

            UserInfo.usersInfo(frame,leftPanel);
        });
        inventory.addActionListener(e ->{
            Storage.storage(frame , leftPanel);
        });
        productionLines.addActionListener(e ->{
            ProductionLines.showProductionLines(frame,leftPanel);
        });
        orders.addActionListener(e->{
            TaskView.showAllTasks(frame,leftPanel);
                }
                );

        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }

    private static class SideButton extends JButton {
        public SideButton(String text) {
            this(text, null);
        }

        public SideButton(String text, Icon icon) {
            setPreferredSize(new Dimension(300, 50));
            setMaximumSize(new Dimension(300, 50));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(0, 10, 0, 10)); // padding
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel textLabel = new JLabel(text);
            textLabel.setHorizontalAlignment(SwingConstants.LEFT);
            textLabel.setVerticalAlignment(SwingConstants.CENTER);
            textLabel.setForeground(Color.WHITE);
            textLabel.putClientProperty("FlatLaf.style", "font: bold 16 serif;");
            add(textLabel, BorderLayout.CENTER);

            if (icon != null) {
                JLabel iconLabel = new JLabel(icon);
                iconLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                iconLabel.setVerticalAlignment(SwingConstants.CENTER);
                add(iconLabel, BorderLayout.EAST);
            }

            putClientProperty("FlatLaf.style", ""
                    + "background: #024971FF;"
                    + "hoverBackground: #002A40FF;"
                    + "pressedBackground: #F7941DFF;"
            );
        }
    }

    public static JPanel getLeftPanel(){
        return leftPanel;
    }

}
