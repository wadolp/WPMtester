import java.io.*;
import java.util.HashMap;

public class UserManager {
    private HashMap<String, User> users = new HashMap<>();
    private final String lastUserFile = "last_user.txt";

    // Check if username exists in the system
    public boolean usernameExists(String username) {
        return users.containsKey(username);
    }

    // Register a new user if the username doesn't already exist
    public boolean register(User user) {
        if (usernameExists(user.getUsername())) {
            return false; // Username already exists
        }
        users.put(user.getUsername(), user);
        return true; // Successfully registered
    }

    // Login and save the last successfully logged-in user
    public boolean login(String username, String password) {
        User user = users.get(username);
        boolean success = user != null && user.getPassword().equals(password); // Check if password matches
        if (success) {
            saveLastUser(username); // Save to file
        }
        return success;
    }

    // Save last logged-in user to a file
    public void saveLastUser(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(lastUserFile))) {
            writer.write(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load last logged-in user from a file
    public String loadLastUser() {
        try (BufferedReader reader = new BufferedReader(new FileReader(lastUserFile))) {
            return reader.readLine();
        } catch (IOException e) {
            return null; // No user saved or file doesn't exist
        }
    }
}
