import java.util.HashMap;

public class UserManager {
    private HashMap<String, User> users = new HashMap<>();

    public boolean usernameExists(String username) {
        return users.containsKey(username);
    }

    public boolean register(User user) {
        if (usernameExists(user.getUsername())) {
            return false; // Username already exists
        }
        users.put(user.getUsername(), user);
        return true; // Successfully registered
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password); // Check if password matches
    }
}
