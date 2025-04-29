import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TypingTest extends JFrame {
    private String originalText;
    private JTextPane displayTextPane;
    private JTextArea inputTextArea;
    private JLabel statusLabel;
    private JLabel wpmLabel;
    private JLabel accuracyLabel;
    private JLabel timerLabel;
    private Instant startTime;
    private int correctChars = 0;
    private int totalChars = 0;
    private boolean testCompleted = false;
    private int currentPosition = 0;

    // Options (to be implemented later)
    private boolean penaltyEnabled = false;
    private boolean timerEnabled = false;
    private int timerDuration = 60; // Default 60 seconds
    private Timer countdownTimer;
    private int timeLeft;


    // Text styles
    private Style defaultStyle;
    private Style correctStyle;
    private Style incorrectStyle;
    private Style currentPosStyle;

    public TypingTest(String filename) {
        // Set up the frame
        setTitle("Git Good - WPM Tester");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Create panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 248, 255)); // Light blue background

        // Initialize the countdown timer (not started yet)
        countdownTimer = new Timer(1000, e -> updateTimer());

        // Load the passage from file
        try {
            originalText = loadPassageFromFile("resource/" + filename);
        } catch (IOException e) {
            originalText = "Error loading passage. Please try again.";
            e.printStackTrace();
        }

        // Create display area for the passage
        displayTextPane = new JTextPane();
        displayTextPane.setEditable(false);
        displayTextPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        displayTextPane.setText(originalText);

        // Create a style for highlighted text
        StyledDocument doc = displayTextPane.getStyledDocument();
        defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        // Style for correctly typed characters
        correctStyle = displayTextPane.addStyle("correct", defaultStyle);
        StyleConstants.setForeground(correctStyle, new Color(34, 139, 34)); // Green color
        StyleConstants.setBold(correctStyle, true);

        // Style for incorrectly typed characters
        incorrectStyle = displayTextPane.addStyle("incorrect", defaultStyle);
        StyleConstants.setForeground(incorrectStyle, new Color(255, 0, 0)); // Red color
        StyleConstants.setBold(incorrectStyle, true);

        // Style for cursor position indicator
        currentPosStyle = displayTextPane.addStyle("currentPos", defaultStyle);
        StyleConstants.setBackground(currentPosStyle, new Color(173, 216, 230)); // Light blue highlight
        StyleConstants.setBold(currentPosStyle, true);

        JScrollPane displayScrollPane = new JScrollPane(displayTextPane);
        displayScrollPane.setBorder(BorderFactory.createTitledBorder("Type the following text:"));

        // Create input area for user typing
        inputTextArea = new JTextArea();
        inputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);

        //prevent copy and pasting in TextArea
        inputTextArea.setTransferHandler(null);
        InputMap inputMap = inputTextArea.getInputMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), "none");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.META_DOWN_MASK), "none");
        inputTextArea.setComponentPopupMenu(null);


        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputScrollPane.setBorder(BorderFactory.createTitledBorder("Type here:"));

        // Create status labels
        statusLabel = new JLabel("Start typing to begin the test");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);

        wpmLabel = new JLabel("WPM: 0");
        accuracyLabel = new JLabel("Accuracy: 0%");
        timerLabel = new JLabel("Time: 0:00");

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        statsPanel.add(wpmLabel);
        statsPanel.add(accuracyLabel);
        statsPanel.add(timerLabel);

        // Create button panel
        JButton restartButton = new JButton("Restart");
        restartButton.setBackground(new Color(34, 139, 34)); // Green color
        restartButton.setForeground(Color.WHITE);

        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setBackground(new Color(0, 153, 255)); // Blue color
        mainMenuButton.setForeground(Color.WHITE);



        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(restartButton);
        buttonPanel.add(mainMenuButton);

        // Add components to the main panel
        mainPanel.add(displayScrollPane, BorderLayout.NORTH);
        mainPanel.add(inputScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(statusLabel);
        bottomPanel.add(statsPanel);
        bottomPanel.add(buttonPanel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        add(mainPanel);

        // Add listener for typing
        inputTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkTyping();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkTyping();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Plain text components don't fire these events
            }
        });

        // Add button listeners
        restartButton.addActionListener(e -> restartTest());

        mainMenuButton.addActionListener(e -> {
            dispose(); // Close this window
            new Main().createAndShowGUI(); // Return to main menu
        });

        // Set focus to input area
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                inputTextArea.requestFocus();
            }
        });

        // Make the frame visible
        setVisible(true);
    }

    private String loadPassageFromFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    private void checkTyping() {
        if (testCompleted) {
            return;
        }

        if (startTime == null) {
            startTime = Instant.now();
            timeLeft = timerDuration;
            countdownTimer.start();
        }

        String inputText = inputTextArea.getText();
        StyledDocument doc = displayTextPane.getStyledDocument();

        // Reset all text to default style first
        doc.setCharacterAttributes(0, originalText.length(), defaultStyle, true);

        // Highlight correct and incorrect characters
        int minLength = Math.min(inputText.length(), originalText.length());
        correctChars = 0;

        for (int i = 0; i < minLength; i++) {
            if (inputText.charAt(i) == originalText.charAt(i)) {
                doc.setCharacterAttributes(i, 1, correctStyle, true);
                correctChars++;
            } else {
                doc.setCharacterAttributes(i, 1, incorrectStyle, true);
            }
        }

        // Update current position indicator
        currentPosition = Math.min(inputText.length(), originalText.length());
        if (currentPosition < originalText.length()) {
            doc.setCharacterAttributes(currentPosition, 1, currentPosStyle, true);
        }

        // Calculate and update statistics
        totalChars = inputText.length();
        updateStats();

        // Check if test is complete
        if (inputText.length() >= originalText.length() &&
                inputText.substring(0, originalText.length()).equals(originalText)) {
            testCompleted = true;
            statusLabel.setText("Test completed! Great job!");
            inputTextArea.setEditable(false);
        }
    }

    private void updateStats() {
        if (startTime == null) {
            return;
        }

        long elapsedSeconds = Duration.between(startTime, Instant.now()).toSeconds();
        if (elapsedSeconds < 1) {
            elapsedSeconds = 1; // Avoid division by zero
        }

        // Calculate WPM: (characters typed / 5) / minutes elapsed
        // 5 characters is the average word length
        double minutes = elapsedSeconds / 60.0;
        int wpm = (int) (correctChars / 5.0 / minutes);

        // Calculate accuracy
        int accuracy = totalChars > 0 ? (correctChars * 100) / totalChars : 0;

        // Apply penalty if enabled
        if (penaltyEnabled) {
            wpm = applyPenalty(wpm, accuracy);
        }


        wpmLabel.setText("WPM: " + wpm);
        accuracyLabel.setText("Accuracy: " + accuracy + "%");
    }


    private int applyPenalty(int wpm, int accuracy) {
        int penalty = (100 - accuracy) / 2;
        int penalizedWPM = wpm - penalty;
        return Math.max(penalizedWPM, 0); // WPM can't go below 0
    }

    // Setter methods for options
    public void setPenaltyEnabled(boolean enabled) {
        this.penaltyEnabled = enabled;
    }

    public void setTimerEnabled(boolean enabled) {
        this.timerEnabled = enabled;
        if (enabled) {
            timerLabel.setText("Time: 60s");
        }
    }

    public void setTimerDuration(int seconds) {
        this.timerDuration = seconds;
    }

    private void restartTest() {
        // Reset variables
        startTime = null;
        correctChars = 0;
        totalChars = 0;
        testCompleted = false;
        currentPosition = 0;

        // Stop timer if it's running
        countdownTimer.stop();

        // Clear input area
        inputTextArea.setText("");
        inputTextArea.setEditable(true);

        // Reset text styling
        StyledDocument doc = displayTextPane.getStyledDocument();
        doc.setCharacterAttributes(0, originalText.length(), defaultStyle, true);

        // Set current position indicator
        doc.setCharacterAttributes(0, 1, currentPosStyle, true);

        // Reset labels
        statusLabel.setText("Start typing to begin the test");
        wpmLabel.setText("WPM: 0");
        accuracyLabel.setText("Accuracy: 0%");
        timerLabel.setText("Time: 0:00");

        // Set focus to input area
        inputTextArea.requestFocus();
    }

    // Timer-related method to update display
    private void updateTimer() {
        if (timeLeft > 0) {
            timeLeft--;
            int minutes = timeLeft / 60;
            int seconds = timeLeft % 60;
            //update timer
            SwingUtilities.invokeLater(() -> {
                timerLabel.setText(String.format("Time: %d:%02d", minutes, seconds));
                timerLabel.revalidate();
                timerLabel.repaint();
            });
        } else {
            countdownTimer.stop();
            testCompleted = true;

            // Update UI to show that time has expired and prevent further typing
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Time's up!");
                statusLabel.revalidate();
                statusLabel.repaint();
                inputTextArea.setEditable(false);
            });
        }
    }
}