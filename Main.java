import java.util.*;
import java.io.*;

class Dictionary {
	private HashSet<String> dictionarySet;
	private String[] dictionaryArray;
	// Could put this in the method, but with how many times it will be iterated it makes more sense to cache
	private HashMap<String, char[]> correctionsLookup;

	public Dictionary() {
		this.dictionarySet = getDictionaryFile();
		this.dictionaryArray = dictionarySet.toArray(new String[0]);
		Arrays.sort(dictionaryArray);
		this.correctionsLookup = populateLookups(dictionaryArray);
	}


	private HashSet<String> getDictionaryFile() {
		File dictionaryFile = new File("dictionary.txt");
		if (!dictionaryFile.exists()) {
			System.err.println("Error: Dictionary file not found.");
			return new HashSet<String>();
		}
		try (Scanner scanner = new Scanner(dictionaryFile)) {
			// Create a HashSet to store unique words
			HashSet<String> wordList = new HashSet<>();

			// Read the file line by line and add each word to the set
			while (scanner.hasNextLine()) {
				wordList.add(scanner.nextLine().trim().toLowerCase());
			}

			// make the job easier for the garbage collector
			scanner.close();
			dictionaryFile = null;

			// return the value
			return wordList;

		} catch (FileNotFoundException e) {
			System.err.println("Error reading dictionary file: " + e.getMessage());
			return new HashSet<String>();
		}
	}
	
	private HashMap<String, char[]> populateLookups(String[] myWords) {
		HashMap<String, char[]> lookups = new HashMap<>();
		for (String word : myWords) {
			char[] chars = word.toCharArray();
			lookups.put(word, chars);
		}
		return lookups;
	}

	public boolean contains(String word) {
		if (dictionarySet.contains(word.toLowerCase())) {
			return true;
		} else {
			return false;
		}
	}

	public List<String> getCorrections(String word) {

		char[] chars = word.trim().toLowerCase().toCharArray();

		List<String> correctionCoefficientsOfTwo = new ArrayList<>();
		List<String> correctionCoefficientsOfThree = new ArrayList<>();
		List<String> correctionCoefficientsOfFour = new ArrayList<>();

		List<String> corrections = new ArrayList<>();

		for(String dictWord : dictionaryArray) {
			char[] dictChars = correctionsLookup.get(dictWord);
			if (dictChars == null) {
				continue; // Skip if the word is not in the lookup
			}
			int distance = levenshteinDistance(chars, dictChars);

			if (distance <= 1) {
				corrections.add(dictWord);
			}

			if (distance == 2) {
				correctionCoefficientsOfTwo.add(dictWord);
			}

			if (distance == 3) {
				correctionCoefficientsOfThree.add(dictWord);
			}

			if (distance == 4) {
				correctionCoefficientsOfFour.add(dictWord);
			}
		}

		if (corrections.isEmpty()) {
			if (!correctionCoefficientsOfTwo.isEmpty()) {
				corrections = correctionCoefficientsOfTwo;
			} else if (!correctionCoefficientsOfThree.isEmpty()) {
				corrections = correctionCoefficientsOfThree;
			} else if (!correctionCoefficientsOfFour.isEmpty()) {
				corrections = correctionCoefficientsOfFour;
			}
		}

		return corrections;
	}

	private int levenshteinDistance(char[] foo, char[] bar) {
		int[][] dp = new int[foo.length + 1][bar.length + 1];
		for (int i = 0; i <= foo.length; i++) {
			for (int j = 0; j <= bar.length; j++) {
				if (i == 0) {
					dp[i][j] = j; // Deletion
				} else if (j == 0) {
					dp[i][j] = i; // Insertion
				} else if (foo[i - 1] == bar[j - 1]) {
					dp[i][j] = dp[i - 1][j - 1]; // No operation
				} else {
					dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + 1); // Substitution
				}
			}
		}
		return dp[foo.length][bar.length];
	}
}



public class Main {

	private static boolean isValidInput(String input) {
		return input != null && input.matches("[a-zA-Z]+");
	}

	private static void printSplashScreen() {
		System.out.println("                                                                                                    ");
		System.out.println("                                                                                                    ");
		System.out.println("                                                                                                    ");
		System.out.println("    ███████╗██████╗ ███████╗██╗     ██╗      ██████╗██╗  ██╗███████╗ ██████╗██╗  ██╗███████╗██████╗ ");
		System.out.println("    ██╔════╝██╔══██╗██╔════╝██║     ██║     ██╔════╝██║  ██║██╔════╝██╔════╝██║ ██╔╝██╔════╝██╔══██╗");
		System.out.println("    ███████╗██████╔╝█████╗  ██║     ██║     ██║     ███████║█████╗  ██║     █████╔╝ █████╗  ██████╔╝");
		System.out.println("    ╚════██║██╔═══╝ ██╔══╝  ██║     ██║     ██║     ██╔══██║██╔══╝  ██║     ██╔═██╗ ██╔══╝  ██╔══██╗");
		System.out.println("    ███████║██║     ███████╗███████╗███████╗╚██████╗██║  ██║███████╗╚██████╗██║  ██╗███████╗██║  ██║");
		System.out.println("    ╚══════╝╚═╝     ╚══════╝╚══════╝╚══════╝ ╚═════╝╚═╝  ╚═╝╚══════╝ ╚═════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝");
		System.out.println("                                                                                                    ");
		System.out.println("                                                                                                    ");
		System.out.println("                                                                                                    ");
		System.out.println("   By: Charles Deane, 2025                                                                          ");
		System.out.println("                                                                                                    ");
		System.out.println("                                                                                                    ");
	}

	public static void main(String[] args) {
		Dictionary dictionary = new Dictionary();

		Scanner input = new Scanner(System.in);

		printSplashScreen();

		while (true) {
			System.out.println("Type a word to check its spelling or type 'exit' to quit.");
			String word = input.nextLine().trim();
			if (word.equalsIgnoreCase("exit")) {
				System.out.println("Exiting the program.");
				break;
			}
			if (!isValidInput(word)) {
				System.out.println("Invalid input. Please enter a valid word.");
				continue;
			}
			if (dictionary.contains(word)) {
				System.out.println("The word '" + word + "' is spelled correctly.");
			} else {
				List<String> corrections = dictionary.getCorrections(word);
				if (corrections.isEmpty()) {
					System.out.println("No suggestions found for the word '" + word + "'.");
				} else {
					System.out.println("Did you mean: " + String.join(", ", corrections) + "?");
				}
			}
			System.out.println('\n');
		}
	}
}

