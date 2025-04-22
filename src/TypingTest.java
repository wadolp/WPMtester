import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TypingTest {

    String user;
    String FileName;
    String timer;
    String testText = new String();

    public TypingTest(String user, String filename, String timer) {
        this.user = user;
        FileName = filename;
        this.timer = timer;
        System.out.println(FileName);
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(FileName)) {
            if (in == null) {
                throw new IOException("Could not find file: " + FileName);
            }

            this.testText = new BufferedReader(new InputStreamReader(in)).readLine();

        } catch (IOException e) {
            e.printStackTrace();
            this.testText = "Error loading passage.";
        }
    }


    public void startTest() {
        JFrame TypingFrame = new JFrame("WPM tester- " + user);
        TypingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        TypingFrame.setLayout(new BorderLayout());



        JTextPane textPane = new JTextPane();
        textPane.setText(testText); // Set initial text
        textPane.setEditable(false);  // Allow the user to type in the JTextPane
        textPane.setFont(new Font("Serif", Font.BOLD, 30)); // Set font for text


        StyledDocument doc = textPane.getStyledDocument();
        Style style = textPane.addStyle("CustomStyle", null);
        StyleConstants.setLineSpacing(style, 4.5f);  // Set line spacing to 1.5x the font size
        doc.setParagraphAttributes(0, doc.getLength(), style, false);

        // Add the JTextPane to the frame inside a JScrollPane
        JScrollPane scrollPane = new JScrollPane(textPane);


        JTextArea typingArea = new JTextArea(5, 40);
        typingArea.setFont(new Font("Monospaced", Font.PLAIN, 28));
        typingArea.setLineWrap(true);
        typingArea.setWrapStyleWord(true);

        JScrollPane typingScrollPane = new JScrollPane(typingArea);

        // Combine components
        TypingFrame.add(scrollPane, BorderLayout.CENTER);
        TypingFrame.add(typingScrollPane, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> {
                    scrollPane.getVerticalScrollBar().setValue(0);
        });

        TypingFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        TypingFrame.setVisible(true);
    }
}

