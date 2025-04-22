import java.util.*;

public class User {
    private String username;
    private String password; // In real life, never store plain passwords!

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
