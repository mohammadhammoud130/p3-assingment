package controller;

import com.formdev.flatlaf.FlatLightLaf;
import view.Login;
import view.ManagerDashBoard;

import javax.swing.*;
import java.awt.*;

public class MainController {
   private static JFrame frame = new JFrame("Storage and Production Management");


     public static void login() {
         try {
             UIManager.setLookAndFeel(new FlatLightLaf());
         } catch (UnsupportedLookAndFeelException e) {
             throw new RuntimeException(e);
         }
         UIManager.put("Button.arc", 12);
         UIManager.put("Component.arc", 12);
         UIManager.put("ProgressBar.arc", 12);
         UIManager.put("TextComponent.arc", 10);
         UIManager.put("Button.pressedBackground", new Color(255, 255, 255));


         frame.setIconImage(new ImageIcon("assets/32-logo.jpg").getImage());
         frame.setSize(1600, 900);
         frame.setResizable(true);
         frame.setDefaultCloseOperation(filesUpdater());
         frame.setLocationRelativeTo(null);
         frame.setVisible(true);
         Login.login(frame);
//         ManagerDashBoard.managerDashBoard(frame);
     }

    static {
        UserController.setUsers(UserController.loadUsers());
        ItemController.setItems(ItemController.itemsLoader());
        ProductController.setProducts(ProductController.productsLoader());
        TaskController.setTasks(TaskController.tasksLoader());
        ProductLineController.setProductLines(ProductLineController.productLinesLoader());
    }

    public static int filesUpdater() {
        UserController.updateUsersFile();
        ItemController.updateItemsFile();
        ProductController.updateProductsFile();
        TaskController.updateTasksFile();
        ProductLineController.updateProductLinesFile();
        return JFrame.EXIT_ON_CLOSE;
    }
    public static JFrame getFrame() {
      return MainController.frame;
    }
}
