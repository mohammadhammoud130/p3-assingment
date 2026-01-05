package controller;

import model.Rule;
import model.User;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

public class UserController {
    private HashMap<String,User> users = new HashMap<>();
    private static final String usersFilePath = "/data/Users";

    public static HashMap<String, User> loadUsers() {
        HashMap<String, User> users = new HashMap<>();
        File file = new File(usersFilePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

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
                    Rule rule = Rule.valueOf(fields[2].trim());

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

    public void updateUsersFile(){
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

    public void addUser(User user){
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

    public boolean  Login(String userName, String password, Rule rule){
        if(users.containsKey(userName)){
            User user = users.get(userName);
            if(user.getPassword().equals(password) && user.getRule().equals(rule)){
                if(!user.isLogged())
                    return true;
                else{
                    ;
                }
            }else {
                System.err.println("Password or Rule is wrong! ");
                ErrorLogger.logWarning("Password or Rule is wrong! ");
                return false;
                return false;
            }
        }else
            return false;

    }

}
