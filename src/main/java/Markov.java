import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
//import java.util.regex.Pattern;

public class Markov {
	private static final String BEGINS_SENTENCE = "__$";
	private static final String PUNCTUATION_MARKS = ".!?$";

	private HashMap<String, ArrayList<String>> words = new HashMap<>();
	private String prevWord;

	public Markov() {
		words.put(BEGINS_SENTENCE, new ArrayList<>());
		prevWord = BEGINS_SENTENCE;
	}

	HashMap<String, ArrayList<String>> getWords() {
		return words;
	}

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

	void addLine(String line) {
		if (line.isEmpty()) return;

		String[] words = line.trim().split("[ \n\t]");

		for (String word : words) {
			addWord(word);
		}
	}

	public String getSentence() {
		StringBuilder sentence = new StringBuilder();
		boolean sentenceFinished = false;
		String currentWord = BEGINS_SENTENCE;

//		sentence.append(BEGINS_SENTENCE);

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

	String randomWord(String currentWord) {
		Random randomGenerator = new Random();

		ArrayList<String> candidates = words.get(currentWord);

		return candidates.get(randomGenerator.nextInt(candidates.size()));
	}

	@Override
	public String toString() {
		return words.toString();
	}

	public static boolean endsWithPunctuation(String sentence) {
		try {
			return PUNCTUATION_MARKS.contains(sentence.substring(sentence.length() - 1));
		} catch (StringIndexOutOfBoundsException e) {
			return false;
		}
	}
}
