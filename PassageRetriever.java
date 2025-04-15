import java.util.Random;

public class PassageRetriever {

    public static String getPassage_filename(String difficulty) {
        difficulty = difficulty.toLowerCase();
        Random rand = new Random();
        int fileNumber = rand.nextInt(4) + 1; // 1–4
        String prefix;

        switch (difficulty) {
            case "easy":
                prefix = "1";
                break;
            case "medium":
                prefix = "2";
                break;
            case "hard":
                prefix = "3";
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty: " + difficulty); //prevents dummy value initializing prefix
        }
        //returns filename of a random passage depending on difficulty selected
        return prefix + fileNumber + getFileLabel(fileNumber) + "_" + difficulty + ".txt";
    }

    private static String getFileLabel(int number) {
        switch (number) {
            case 1:
                return "first";
            case 2:
                return "second";
            case 3:
                return "third";
            case 4:
                return "fourth";
            default:
                return "";
        }
    }
}