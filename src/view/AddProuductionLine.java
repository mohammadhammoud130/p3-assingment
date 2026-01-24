package view;

import controller.ProductLineController;
import model.ProductLine;
import model.Status;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AddProuductionLine extends JPanel {

        public AddProuductionLine() {

            setLayout(new BorderLayout());
            JPanel mainPanel = new JPanel(new GridLayout(1, 2));

            // left part
            JPanel leftPanel = new JPanel(null);
            leftPanel.setBackground(new Color(225, 238, 245));

            JLabel lblTitle = new JLabel("  New Production Line", JLabel.LEFT);
            lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
            lblTitle.setForeground(Color.white);
            lblTitle.setBounds(30, 20, 400, 40);

            JPanel up = new JPanel(null);
            up.setBackground(new Color(15, 70, 120));
            up.setBounds(0, 0, 800, 80);
            up.add(lblTitle);
            leftPanel.add(up);

            // field name
            JLabel lblName = new JLabel("Line Name");
            lblName.setFont(new Font("Arial",Font.PLAIN,20));
            lblName.setBounds(160, 260, 200, 20);//150 180 100 20
            leftPanel.add(lblName);

            JTextField txtName = createField();
            txtName.setBounds(160, 290, 300, 30);//150 205 300 35// 100 130
            leftPanel.add(txtName);

            // Status
            JLabel lblStatus = new JLabel("Status");
            lblStatus.setFont(new Font("Arial",Font.PLAIN,20));
            lblStatus.setBounds(160, 336, 100, 20);
            leftPanel.add(lblStatus);

            JCheckBox chkActive = new JCheckBox("Active");
            chkActive.setFont(new Font("Arial",Font.PLAIN,18));
            chkActive.setBounds(160, 375, 100, 25);

            JCheckBox chkStopped = new JCheckBox("Stopped");
            chkStopped.setBounds(275, 375, 100, 25);
            chkStopped.setFont(new Font("Arial",Font.PLAIN,18));

            JCheckBox chkPassed = new JCheckBox("Passed");
            chkPassed.setBounds(400, 375, 100, 25);//270
            chkPassed.setFont(new Font("Arial",Font.PLAIN,18));
            ButtonGroup statusGroup = new ButtonGroup();
            statusGroup.add(chkActive);
            statusGroup.add(chkStopped);
            statusGroup.add(chkPassed);

            leftPanel.add(chkActive);
            leftPanel.add(chkStopped);
            leftPanel.add(chkPassed);

            // Task Button
            JButton buttonAddLine = new JButton("Add Proudction Line  ");
            buttonAddLine.setFont(new Font("Arial", Font.BOLD, 15));
            buttonAddLine.setBounds(160, 450, 300, 45);
            buttonAddLine.setForeground(Color.white);
            buttonAddLine.setFocusable(false);
            buttonAddLine.setBackground(new Color(0xF7941D));
            leftPanel.add(buttonAddLine);

            buttonAddLine.addActionListener(e -> {
                String name = txtName.getText().trim();

                //Empity field
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please fill all required fields",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Status status;
                if (chkActive.isSelected()) status = Status.ACTIVE;
                else if (chkStopped.isSelected()) status = Status.FINISHED;
                else if (chkPassed.isSelected()) status = Status.PAUSED;
                else {
                    JOptionPane.showMessageDialog(this,
                            "Please select status",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                //object
                ProductLine newLine = new ProductLine(name, status, new ArrayList<>());
                ProductLineController.addProductLine(newLine);

                // id in pop
                JOptionPane.showMessageDialog(this,
                        "Production Line added successfully!\nGenerated ID: " + newLine.getId(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);


                txtName.setText("");
                statusGroup.clearSelection();
            });

            // right part
            JPanel up2 = new JPanel(null);
            up2.setBackground(new Color(15, 70, 120));
            up2.setBounds(0, 0, 800, 80);
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBackground(Color.white);
            rightPanel.add(up2);

          ImageIcon icon = new ImageIcon(
                  getClass().getResource("proudctionline.jpg")
         );
          JLabel label = new JLabel(icon);
           rightPanel.add(label, BorderLayout.CENTER);

            mainPanel.add(leftPanel);
            mainPanel.add(rightPanel);
            add(mainPanel, BorderLayout.CENTER);
        }

        private JTextField createField() {
            JTextField field = new JTextField();
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            return field;
        }

    }

