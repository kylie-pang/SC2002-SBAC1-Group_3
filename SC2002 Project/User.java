import java.util.*;

public abstract class User {
    private final String userID;
    private String name;
    private String email;
    private String password;

    protected User(String userID, String name, String email) {
        this.userID = requireNonBlank(userID, "User ID");
        this.name   = requireNonBlank(name, "Name");
        this.email  = requireNonBlank(email, "Email");
        this.password = "password"; // default password
    }

    private static String requireNonBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return trimmed;
    }

    public void login() {
        System.out.println(name + " logged in.");
    }

    public void logout() {
        System.out.println(name + " logged out.");
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.password = newPassword.trim();
        System.out.println("Password changed successfully.");
    }

    //check password
    public boolean checkPassword(String inputPassword) {
        return password != null && password.equals(inputPassword);
    }

    public abstract void displayMenu();

    //getters
    public String getUserID() { return userID; }
    public String getName()   { return name; }
    public String getEmail()  { return email; }

    //setters
    public void setName(String name) {
        this.name = requireNonBlank(name, "Name");
    }

    public void setEmail(String email) {
        this.email = requireNonBlank(email, "Email");
    }

    @Override
    public String toString() {
        return "%s (%s)".formatted(name, userID);
    }
}