import javax.swing.*;
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
    TypingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JTextArea textArea = new JTextArea(testText);
    TypingFrame.getContentPane().add(textArea);
    TypingFrame.setVisible(true);
    }
}

