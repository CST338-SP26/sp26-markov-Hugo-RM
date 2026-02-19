import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
//import java.util.regex.Pattern;

/**
 * Read in a text file, use the information contained to build a dictionary of words and their descendants.
 * Randomly select from the words to generate new text.
 *
 * @author Hugo Ruiz-Mireles
 * @version 1.0.0
 * @since 02/17/26
 */
public class Markov {
	private static final String BEGINS_SENTENCE = "__$";
	private static final String PUNCTUATION_MARKS = ".!?$";

	private final HashMap<String, ArrayList<String>> words = new HashMap<>();
	private String prevWord;

	/**
	 * Constructs a new Markov object
	 * Initializes the sentence-start key and sets prevWord to that key.
	 */
	public Markov() {
		words.put(BEGINS_SENTENCE, new ArrayList<>());
		prevWord = BEGINS_SENTENCE;
	}

	HashMap<String, ArrayList<String>> getWords() {
		return words;
	}

	/**
	 * Reads a text file line by line and adds each line to the Markov chain.
	 *
	 * @param filename the path to the file to read
	 */
	public void addFromFile(String filename) {
		File f = new File(filename);

		try (Scanner s = new Scanner(f)) {
			while (s.hasNextLine()) {
				addLine(s.nextLine());
			}
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found Exception.");
		}
	}

	/**
	 * Splits a line into words by whitespace and adds each word to the chain.
	 * Does nothing if the line is empty.
	 *
	 * @param line a single line of text to process
	 */
	void addLine(String line) {
		if (line.isEmpty()) return;

		String[] words = line.trim().split("[ \n\t]");

		for (String word : words) {
			addWord(word);
		}
	}

	/**
	 * Generates a single sentence by randomly traversing the word map
	 * until a word ending with punctuation is reached.
	 *
	 * @return a randomly generated sentence as a String
	 */
	public String getSentence() {
		StringBuilder sentence = new StringBuilder();
		boolean sentenceFinished = false;
		String currentWord = BEGINS_SENTENCE;

		while (!sentenceFinished) {
			currentWord = randomWord(currentWord);
			sentence.append(currentWord);

			if (endsWithPunctuation(currentWord)) {
				sentenceFinished = true;
			} else {
				sentence.append(' ');
			}
		}

		return sentence.toString();
	}

	/**
	 * Adds a single word to the Markov chain.
	 * Maps the word as a successor of prevWord, then updates prevWord.
	 * If prevWord ends with punctuation, the word is treated as a sentence starter.
	 * Does nothing if the word is empty.
	 *
	 * @param word the word to add to the chain
	 */
	void addWord(String word) {
		if (word.isEmpty()) return;

		if (endsWithPunctuation(prevWord)) {
			words.get(BEGINS_SENTENCE).add(word);
			prevWord = word;
			return;
		}

		if (!words.containsKey(prevWord)) {
			words.put(prevWord, new ArrayList<>());
		}

		words.get(prevWord).add(word);

		prevWord = word;
	}

	/**
	 * Randomly selects a word from the list of successors for the given word.
	 *
	 * @param currentWord the word whose successors to choose from
	 * @return a randomly selected successor word
	 */
	String randomWord(String currentWord) {
		Random randomGenerator = new Random();

		ArrayList<String> candidates = words.get(currentWord);

		return candidates.get(randomGenerator.nextInt(candidates.size()));
	}

	/**
	 * Returns a string representation of the entire word map.
	 *
	 * @return the word map as a String
	 */
	@Override
	public String toString() {
		return words.toString();
	}

	/**
	 * Checks whether a string ends with a sentence-ending punctuation mark (. ! ? $).
	 *
	 * @param sentence the string to check
	 * @return true if the last character is punctuation, false otherwise
	 */
	public static boolean endsWithPunctuation(String sentence) {
		try {
			return PUNCTUATION_MARKS.contains(sentence.substring(sentence.length() - 1));
		} catch (StringIndexOutOfBoundsException e) {
			return false;
		}
	}
}
