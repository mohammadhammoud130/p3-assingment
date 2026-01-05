package model;

public class User {
    private final  String userName;
    private  String password;
    private  Rule  rule;
    private  boolean logged;


    public User (String userName, String password, Rule rule){
        this.userName = userName;
        this.password = password;
        this.rule = rule;
        logged = false;
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

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
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
