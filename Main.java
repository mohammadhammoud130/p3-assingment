import java.util.HashMap;
import model.*;
import controller.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {






    }
    static {ItemController.setItems(ItemController.itemsLoader());
        ProductController.setProducts(ProductController.productsLoader());
        TaskController.setTasks(TaskController.tasksLoader());
        ProductLineController.setProductLines(ProductLineController.productLinesLoader());
    }
    }