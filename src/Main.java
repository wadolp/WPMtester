import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    private UserManager userManager = new UserManager();
    private String passageFileName = new String();
    private boolean timerEnabled = false;
    private boolean penaltyEnabled = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().createAndShowGUI();
        });
    }

    public void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("Git Good - WPM Tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400); // Increased height to accommodate new options
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

        // Game options panel (initially hidden)
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(boxColor);
        optionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Game Options"));
        optionsPanel.setVisible(false);

        // Add difficulty selection
        JLabel difficultyLabel = new JLabel("Select Difficulty:");
        difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JComboBox<String> difficultyComboBox = new JComboBox<>(new String[] {"Easy", "Medium", "Hard"});
        difficultyComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        difficultyComboBox.setMaximumSize(new Dimension(150, 25));

        // Add timer and penalty checkboxes
        JCheckBox timerCheckbox = new JCheckBox("Enable Timer (60 seconds)");
        timerCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerCheckbox.setBackground(boxColor);

        JCheckBox penaltyCheckbox = new JCheckBox("Penalize Mistakes");
        penaltyCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        penaltyCheckbox.setBackground(boxColor);

        // Start button
        JButton startButton = new JButton("Start Typing Test");
        startButton.setBackground(buttonColor);
        startButton.setForeground(Color.WHITE);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setMaximumSize(new Dimension(200, 30));

        // Add components to options panel
        optionsPanel.add(Box.createVerticalStrut(10));
        optionsPanel.add(difficultyLabel);
        optionsPanel.add(Box.createVerticalStrut(5));
        optionsPanel.add(difficultyComboBox);
        optionsPanel.add(Box.createVerticalStrut(15));
        optionsPanel.add(timerCheckbox);
        optionsPanel.add(Box.createVerticalStrut(5));
        optionsPanel.add(penaltyCheckbox);
        optionsPanel.add(Box.createVerticalStrut(15));
        optionsPanel.add(startButton);
        optionsPanel.add(Box.createVerticalStrut(10));

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
                    // Hide login components and show options
                    usernameField.setVisible(false);
                    passwordField.setVisible(false);
                    registerButton.setVisible(false);
                    loginButton.setVisible(false);
                    messageLabel.setText("Login successful! Choose your options.");

                    // Show options panel
                    optionsPanel.setVisible(true);
                } else {
                    messageLabel.setText("Invalid login. Try again.");
                }
            }
        });

        // Select difficulty
        difficultyComboBox.addActionListener(e -> {
            String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
            passageFileName = TextLibrary.getPassage_filename(selectedDifficulty);
        });

        // Set timer option
        timerCheckbox.addActionListener(e -> {
            timerEnabled = timerCheckbox.isSelected();
        });

        // Set penalty option
        penaltyCheckbox.addActionListener(e -> {
            penaltyEnabled = penaltyCheckbox.isSelected();
        });

        // Start button action
        startButton.addActionListener(e -> {
            frame.dispose(); // Close the login window
            // Create typing test with selected options
            TypingTest test = new TypingTest(passageFileName);
            //test.setPenaltyEnabled(penaltyEnabled);
            test.setTimerEnabled(timerEnabled);
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

        // add components to frame
        frame.add(welcomeLabel, BorderLayout.NORTH);
        frame.add(loginPanel, BorderLayout.CENTER);
        frame.add(optionsPanel, BorderLayout.SOUTH);


        // Auto-login if remembered
        String rememberedUser = userManager.loadLastUser();

        if (rememberedUser != null) {
            messageLabel.setText("Welcome back, " + rememberedUser + "! Auto-login successful.");
            usernameField.setVisible(false);
            passwordField.setVisible(false);
            registerButton.setVisible(false);
            loginButton.setVisible(false);
            optionsPanel.setVisible(true);
        }




        // Display the frame
        frame.setVisible(true);
    }
}