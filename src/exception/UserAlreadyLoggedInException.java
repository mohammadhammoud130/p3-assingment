package exception;

 public class UserAlreadyLoggedInException extends RuntimeException {
     public UserAlreadyLoggedInException(String username) {
         super("User \"" + username + "\" is already logged in.");
     }


 }