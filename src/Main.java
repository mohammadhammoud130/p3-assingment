import com.formdev.flatlaf.FlatLightLaf;
import controller.*;
import view.*;
import model.*;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        JFrame frame = new JFrame("App");
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 12);
        UIManager.put("ProgressBar.arc", 12);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Button.pressedBackground", new Color(245, 245, 245));


        frame.setIconImage(new ImageIcon("assets/32-logo.jpg").getImage());
        frame.setSize(1000, 600);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(filesUpdater());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        Login.login(frame);
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
}
