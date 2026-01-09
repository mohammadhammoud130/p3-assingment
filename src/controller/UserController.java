package controller;

import com.sun.tools.javac.Main;
import exception.UserAlreadyLoggedInException;
import model.Rule;
import model.User;
import view.Login;
import view.ManagerDashBoard;
import view.ProductionSupervisor;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UserController {
    private static HashMap<String,User> users = new HashMap<>();
    private static final String usersFilePath = "data/Users.csv";
    private static final Set<String> loggedInUsers = new HashSet<>();


    public static HashMap<String, User> loadUsers() {
        HashMap<String, User> users = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {

            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split(",");
                    if (fields.length < 3) {
                        System.err.println("Invalid user line: " + line);
                        ErrorLogger.logWarning("Invalid user line: " + line);
                        continue;
                    }

                    String userName = fields[0].trim();
                    String password = fields[1].trim();
                    Rule rule = Rule.valueOf(fields[2].trim().toUpperCase());
                    users.put(userName, new User(userName, password, rule));

                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid user data in line: " + line);
                    ErrorLogger.logWarning("Invalid user data in line: " + line);
                } catch (NullPointerException e) {
                    System.err.println("Null value found in user line: " + line);
                    ErrorLogger.logWarning("Null value found in user line: " + line);
                } catch (Exception e) {
                    System.err.println("Unexpected error parsing user line: " + line);
                    ErrorLogger.logWarning("Unexpected error parsing user line: " + line);
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("Users file not found: " + usersFilePath);
            ErrorLogger.logWarning("Users file not found: " + usersFilePath);
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
            ErrorLogger.logWarning("Error reading users file: " + e.getMessage());
        }

        return users;
    }

    public static void updateUsersFile(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath))){
            writer.write("userName,password,rule");
            writer.newLine();
            for (User user : users.values()){
                if(user == null) continue;
                writer.write(user.getUserName()+','+
                        user.getPassword()+',' +
                        user.getRule().name());
                writer.newLine();
            }
        }catch (FileNotFoundException e) {
            System.err.println("Users file not found when writing: " + usersFilePath);
            ErrorLogger.logWarning("Users file not found when writing: " + usersFilePath);
        } catch (SecurityException e) {
            System.err.println("No permission to write users file: " + usersFilePath);
            ErrorLogger.logWarning("No permission to write users file: " + usersFilePath);
        } catch (IOException e) {
            System.err.println("Error writing users file: " + e.getMessage());
            ErrorLogger.logWarning("Error writing users file: " + e.getMessage());
        }

    }

    public static void addUser(User user){
        if(users.containsKey(user.getUserName())){
            System.err.println(user.getUserName()+" username is already taken");
            ErrorLogger.logWarning(user.getUserName()+" username is already taken");
            return;
        }
        users.put(user.getUserName(),user);
        try (BufferedWriter userAdder = new BufferedWriter(new FileWriter(usersFilePath,true))){
            userAdder.write(user.getUserName()+','+
                                user.getPassword()+','+
                                user.getRule().name()+"\n");
        }catch (FileNotFoundException e){
            System.err.println("Users file not found when writing: " + usersFilePath);
            ErrorLogger.logWarning("Users file not found when writing: " + usersFilePath);
        }catch (SecurityException e) {
            System.err.println("No permission to write users file: " + usersFilePath);
            ErrorLogger.logWarning("No permission to write users file: " + usersFilePath);
        } catch (IOException e) {
            System.err.println("Error writing users file: " + e.getMessage());
            ErrorLogger.logWarning("Error writing users file: " + e.getMessage());
        }
    }

    public static void login(String username, String password,Rule rule) {
        User user = users.get(username);

        if (user == null) {
            ErrorLogger.logWarning("User not found: " + username);
            throw new IllegalArgumentException("User not found: " + username);
        }

        if (!user.getPassword().equals(password)) {
            ErrorLogger.logWarning("Incorrect password for user: " + username);
            throw new IllegalArgumentException("Incorrect password for user: " + username);
        }

        if (loggedInUsers.contains(username)) {
                String msg = "User \"" + username + "\" attempted to log in twice.";
                System.err.println(msg);
                ErrorLogger.logWarning(msg);
                throw new UserAlreadyLoggedInException(username);
        }
        if(!user.getRule().equals(rule)){
            String msg = "Incorrect rule for user: " + rule;
            System.err.println(msg);
            ErrorLogger.logWarning(msg);
            throw new IllegalArgumentException(msg);
        }
        loggedInUsers.add(username);
        System.out.println("User logged in: " + username);
        if(user.getRule().equals(Rule.MANAGER)){
            ManagerDashBoard.managerDashBoard(MainController.getFrame(),user.getUserName());
        }else if(user.getRule().equals(Rule.PRODUCTION_SUPERVISOR)){
            ProductionSupervisor.productionSupervisor(MainController.getFrame(),user.getUserName());
        }
    }

    public static void logout(String username){
        loggedInUsers.remove(username);
        Login.login(MainController.getFrame());
    }

    public static HashMap<String, User> getUsers() {
        return users;
    }

    public static void setUsers(HashMap<String, User> users) {
        UserController.users = users;
    }
}
