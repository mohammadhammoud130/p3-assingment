package model;

public class User {
    private final  String userName;
    private  String password;
    private  Rule  rule;


    public User (String userName, String password, Rule rule){
        this.userName = userName;
        this.password = password;
        this.rule = rule;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", rule=" + rule +
                '}';
    }
}
