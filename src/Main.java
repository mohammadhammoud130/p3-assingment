import java.util.HashMap;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import model.*;
import controller.*;
import view.Login;

import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        System.out.println(UserController.getUsers().get("admin").getRule());
        JFrame frame=new JFrame();
        frame.setSize(800,750);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        Login.login(frame);
        frame.setVisible(true);

    }
    static {
        UserController.setUsers(UserController.loadUsers());
        ItemController.setItems(ItemController.itemsLoader());
        ProductController.setProducts(ProductController.productsLoader());
        TaskController.setTasks(TaskController.tasksLoader());
        ProductLineController.setProductLines(ProductLineController.productLinesLoader());
    }
    }