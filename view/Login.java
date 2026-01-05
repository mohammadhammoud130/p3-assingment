package view;

import javax.swing.*;
import java.awt.*;

public class Login extends JPanel {
    public static void login(JFrame frame){
        JPanel panel=new JPanel(new GridLayout(1,2));
        JPanel leftPanel =new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(216,219,227));
        ImageIcon icon=new ImageIcon(Login.class.getResource("view/logg.jpg"));
        JLabel iconlabel=new JLabel(icon,JLabel.CENTER);
        leftPanel.add(iconlabel,BorderLayout.CENTER);
        JPanel rightPanel=new JPanel();
        rightPanel.setBackground(new Color(230,198,198));
        rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
        JLabel title=new JLabel("LOGIN");
        title.setFont(new Font("serif",Font.BOLD,40));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField usernameField=new JTextField();
        usernameField.setMaximumSize(new Dimension(250,35));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));
        JPasswordField passwordField=new JPasswordField();
        passwordField.setMaximumSize(new Dimension(250,35));
        passwordField.setBorder(BorderFactory.createTitledBorder("Passowrd"));
        String []roles={"Manager","Production Supervisor"};
        JComboBox <String> roleBox=new JComboBox<>(roles);
        roleBox.setMaximumSize(new Dimension(250,40));
        roleBox.setBorder(BorderFactory.createTitledBorder("Roles"));
        JButton loginButton=new JButton("Login");
        loginButton.setFocusable(false);
        loginButton.setBackground(new Color(250,80,120));
        loginButton.setForeground(Color.WHITE);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(100,40));
        rightPanel.add(Box.createVerticalStrut(200));
        rightPanel.add(title);
        rightPanel.add(Box.createVerticalStrut(45));
        rightPanel.add(passwordField);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(usernameField);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(roleBox);
        rightPanel.add(Box.createVerticalStrut(25));
        rightPanel.add(loginButton);
        panel.add(leftPanel);
        panel.add(rightPanel);



    }
        }



