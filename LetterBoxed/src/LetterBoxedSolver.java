import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;


public class LetterBoxedSolver {
    private Map<String, Integer> wordUsesLetters = new HashMap<>();
    private Map<Character, Set<String>> wordsStartingWith = new HashMap<>();
    private Map<Character, Integer> letterPositions = new HashMap<>();
    private String bs;//board string without semicolons


    public static void main(String[] args) throws Exception {
//      String board = args[0];
        String board = "ota,myv,ihu,fsr";
        long start = System.currentTimeMillis();
        new LetterBoxedSolver().cleanFile();
        new LetterBoxedSolver().solve(board.toUpperCase());
        long ms = System.currentTimeMillis() - start;
        System.out.println("Time elapsed: " + ms + "ms");
    }

    /**
     * takes the words.txt file and cleans it up so that it an be properly used for our game. we need words of size
     * three or higher, and we don't want to have any non-alphabetical symbols in our words since that won't be on the
     * board.
     */
    private void cleanFile() {
        try {
            File myObj = new File("cleaned_words.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        BufferedReader br = null;
        try {
            // create file object
            File file = new File("src/words.txt");

            // create BufferedReader object from the File
            br = new BufferedReader(new FileReader(file));
            String line = null;
            FileWriter file_writer = new FileWriter("cleaned_words.txt");


            // read file line by line
            while ((line = br.readLine()) != null) {

                while ((line = br.readLine()) != null) {
                    line = line.toUpperCase().strip();
                    if (line.length() > 2) {
                        line = line.replaceAll("[^A-Za-z0-9]", "");
                        file_writer.write(line + "\n");

                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * utilize a priority queue to order elements by object type. In this case, we can prioritize Step objects based on
     * the compareTo method.
     * @param boardString board we are iterating through
     * @throws IOException
     */
    private void solve(String boardString) throws IOException {
        //init
        bs = boardString.replace(",", "");
        String[] board = boardString.split(",");

        for (int i = 0; i < board.length; i++) {
            for (char ch : board[i].toCharArray()) {
                letterPositions.put(ch, i);
            }
        }

        Files.lines(Paths.get("cleaned_words.txt"))
                .forEach(word -> addIfPossible(word));
        System.out.println("# possible words: " + wordUsesLetters.size());

        //A* search
        Queue<Step> queue = new PriorityQueue<>();
        queue.add(new Step(null, 0, null));
        int maxAllowedWordsPlayed = 2;

        while (!queue.isEmpty()) {
            Step step = queue.poll();

            if (step.numLettersRemaining == 0) {
                maxAllowedWordsPlayed = step.numWordsPlayed;
                StringBuilder sb = new StringBuilder();

                for (Step s = step; s.parent != null; s = s.parent) {
                    sb.insert(0, s.prevWord + " ");
                }
                System.out.println(sb.toString().toLowerCase());
            }

            if (step.numWordsPlayed < maxAllowedWordsPlayed) {
                Set<String> nextWords = (step.prevWord == null)
                        ? wordUsesLetters.keySet()
                        : wordsStartingWith.get(step.prevWord.charAt(step.prevWord.length() - 1));

                for (String nextWord : nextWords) {
                    Step nextStep = new Step(nextWord, step.numWordsPlayed + 1, step);
                    queue.add(nextStep);
                }
            }
        }
    }

    /**
     * attempts to add a word to solution list if it fits into the current format of the board
     * @param word - word trying to be added
     */
    private void addIfPossible(String word) {
        int wul = 0;//wordUsesLetters
        int prevPosition = -1;

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);

            if (!letterPositions.containsKey(ch)) {
                return;
            }

            int position = letterPositions.get(ch);

            if (prevPosition == position) {
                return;
            }
            prevPosition = position;
            wul |= (1 << bs.indexOf(ch));
        }

        wordUsesLetters.put(word, wul);
        char firstChar = word.charAt(0);

        if (!wordsStartingWith.containsKey(firstChar)) {
            wordsStartingWith.put(firstChar, new HashSet<>());
        }

        wordsStartingWith.get(firstChar).add(word);
    }

    /**
     * Steps that get prioritized by our Priority Queue. This implements Comparable, which requires we make a compareTo
     * method.
     */
    private class Step implements Comparable<Step> {
        String prevWord;
        int lettersUsed;
        int numLettersRemaining;
        int numWordsPlayed;
        Step parent;

        public Step(String prevWord, int numWordsPlayed, Step parent) {
            this.prevWord = prevWord;
            this.numWordsPlayed = numWordsPlayed;
            this.parent = parent;

            //lettersUsed
            if (prevWord != null) {
                lettersUsed = parent.lettersUsed;

                for (int i = 0; i < prevWord.length(); i++) {
                    char ch = prevWord.charAt(i);
                    lettersUsed |= (1 << bs.indexOf(ch));
                }
            }

            //numLettersRemaining
            numLettersRemaining = 12;

            for (int lu = lettersUsed; lu > 0; lu >>= 1) {
                if ((lu & 0b1) == 0b1) {
                    numLettersRemaining--;
                }
            }
        }

        /**
         * compare steps to see which should be prioritized in our A* search.
         * @param o the object to be compared. the queue iterates until max letters have been played, and the number of
         *          words that we still need to make for our solution.
         * @return
         */
        @Override
        public int compareTo(Step o) {
            return (o.numWordsPlayed == numWordsPlayed)
                    ? numLettersRemaining - o.numLettersRemaining
                    : numWordsPlayed - o.numWordsPlayed;
        }
    }
}
