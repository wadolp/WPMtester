import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    private UserManager userManager = new UserManager();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().createAndShowGUI();
        });
    }

    public void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("Git Good - WPM Tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLocationRelativeTo(null); // Center the frame on the screen


        Color welcomeColor = new Color(0, 153, 255); // Blue color
        Color buttonColor = new Color(34, 139, 34); // Green color
        Color backgroundColor = new Color(240, 248, 255); // Light blue background
        Color boxColor = new Color(255, 255, 255); // White box for login area

        // Set the background color of the frame
        frame.getContentPane().setBackground(backgroundColor);

        // Create a panel for the login
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(boxColor);
        loginPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 153, 255), 5)); // Add border to the box
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Welcome label
        JLabel welcomeLabel = new JLabel("Let's Git Good!");
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 30));
        welcomeLabel.setForeground(welcomeColor);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create input fields for username and password
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);


        JLabel messageLabel = new JLabel();
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons for Register and Login
        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");


        registerButton.setBackground(buttonColor);
        loginButton.setBackground(buttonColor);
        registerButton.setForeground(Color.WHITE);
        loginButton.setForeground(Color.WHITE);

        // difficulty combo box
        JComboBox<String> difficultyComboBox = new JComboBox<>(new String[] {"Easy", "Medium", "Hard"});
        difficultyComboBox.setVisible(false);

        // Registration
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    messageLabel.setText("Please enter both username and password.");
                    return;
                }

                if (userManager.usernameExists(username)) {
                    messageLabel.setText("Username already exists. Please log in.");
                } else {
                    User newUser = new User(username, password);
                    if (userManager.register(newUser)) {
                        messageLabel.setText("Registration successful!");
                        usernameField.setText("");
                        passwordField.setText("");
                    } else {
                        messageLabel.setText("Error during registration.");
                    }
                }
            }
        });

        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    messageLabel.setText("Please enter both username and password.");
                    return;
                }

                if (userManager.login(username, password)) {
                    messageLabel.setText("Login successful! Choose difficulty.");
                    difficultyComboBox.setVisible(true);
                } else {
                    messageLabel.setText("Invalid login. Try again.");
                }
            }
        });

        // Select how difficult the game should be
        difficultyComboBox.addActionListener(e -> {
            String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
            messageLabel.setText("Selected difficulty: " + selectedDifficulty);
        });

        // Add components to the login panel
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(registerButton);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(loginButton);
        loginPanel.add(messageLabel);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(difficultyComboBox);

        // add components to frame
        frame.add(welcomeLabel, BorderLayout.NORTH);
        frame.add(loginPanel, BorderLayout.CENTER);

        // Display the frame
        frame.setVisible(true);
    }
}
