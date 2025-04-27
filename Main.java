import java.util.*;
import java.io.*;

// class for all the spell checking and dictionary activities
class Dictionary {
	private HashSet<String> dictionarySet;
	private String[] dictionaryArray;
	// Could put this in the function, but with how many times it will be iterated it makes more sense to cache
	private HashMap<String, char[]> correctionsLookup;

	// Constructor that initializes the dictionary object
	public Dictionary() {
		this.dictionarySet = getDictionaryFile();
		this.dictionaryArray = dictionarySet.toArray(new String[0]);
		Arrays.sort(dictionaryArray);
		this.correctionsLookup = populateLookups(dictionaryArray);
	}

	// Reads the dictionary file and returns a HashSet of unique words
	private HashSet<String> getDictionaryFile() {
		File dictionaryFile = new File("dictionary.txt");
		// Check if the dictionary file exists
		if (!dictionaryFile.exists()) {
			System.err.println("Error: Dictionary file not found.");
			return new HashSet<String>();
		}
		//tries to read the dictionary file
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

		} 
		// if the dictionary file cannot be read then return an empty dataset
		catch (FileNotFoundException e) {
			System.err.println("Error reading dictionary file: " + e.getMessage());
			return new HashSet<String>();
		}
	}
	
	// Populates a HashMap with words as keys and their character arrays as values
	// This is used to speed up the lookup process when checking for corrections
	// Calling this function in the constructor means that it is only done once
	private HashMap<String, char[]> populateLookups(String[] myWords) {
		HashMap<String, char[]> lookups = new HashMap<>();
		for (String word : myWords) {
			char[] chars = word.toCharArray();
			lookups.put(word, chars);
		}
		return lookups;
	}

	// Checks if a word is in the dictionary
	public boolean contains(String word) {
		if (dictionarySet.contains(word.toLowerCase())) {
			return true;
		} else {
			return false;
		}
	}

	// Returns a list of corrections for a given word based on the Levenshtein distance
	public List<String> getCorrections(String word) {

		char[] chars = word.trim().toLowerCase().toCharArray();

		List<String> correctionCoefficientsOfTwo = new ArrayList<>();
		List<String> correctionCoefficientsOfThree = new ArrayList<>();
		List<String> correctionCoefficientsOfFour = new ArrayList<>();

		List<String> corrections = new ArrayList<>();

		// divide the results into different lists based on the distance
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

		// make sure that only the lowest distance results are returned
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

	// calculate the distance metric between two strings which have already been parsed to char[]
	// using the Levenshtein distance algorithm
	private int levenshteinDistance(char[] foo, char[] bar) {
		int[][] myMatrix = new int[foo.length + 1][bar.length + 1];
		for (int i = 0; i <= foo.length; i++) {
			for (int j = 0; j <= bar.length; j++) {
				if (i == 0) {
					myMatrix[i][j] = j; // Deletion
				} else if (j == 0) {
					myMatrix[i][j] = i; // Insertion
				} else if (foo[i - 1] == bar[j - 1]) {
					myMatrix[i][j] = myMatrix[i - 1][j - 1]; // No operation
				} else {
					myMatrix[i][j] = Math.min(Math.min(myMatrix[i - 1][j] + 1, myMatrix[i][j - 1] + 1), myMatrix[i - 1][j - 1] + 1); // Substitution
				}
			}
		}
		return myMatrix[foo.length][bar.length];
	}
}



public class Main {

	// Validates the input to ensure it contains only letters
	private static boolean isValidInput(String input) {
		return input != null && input.matches("[a-zA-Z]+");
	}

	// Prints the splash screen
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

	// Main method to run the spell checker program
	public static void main(String[] args) {

		// Initialize the dictionary and input scanner
		Dictionary dictionary = new Dictionary();
		Scanner input = new Scanner(System.in);

		// print the splash screen
		printSplashScreen();

		// use a game loop to keep the program running until the user decides to exit.
		while (true) {

			//continuously prompt the user for input
			System.out.println("Type a word to check its spelling or type 'exit' to quit.");
			String word = input.nextLine().trim();

			//break the list if the user types exit
			if (word.equalsIgnoreCase("exit")) {
				System.out.println("Exiting the program.");
				break;
			}

			// validate the input to ensure it contains only letters
			// if not valid then continue to the next iteration where the user will be prompted again
			if (!isValidInput(word)) {
				System.out.println("Invalid input. Please enter a valid word.");
				continue;
			}

			// check if the word is in the dictionary and print the result
			if (dictionary.contains(word)) {
				System.out.println("The word '" + word + "' is spelled correctly.");
			} 

			// if the word is not in the dictionary then get the corrections
			else {
				List<String> corrections = dictionary.getCorrections(word);
				if (corrections.isEmpty()) {
					System.out.println("No suggestions found for the word '" + word + "'.");
				} else {
					System.out.println("Did you mean: " + String.join(", ", corrections) + "?");
				}
			}

			// print a new line for better readability
			System.out.println('\n');
		}
	}
}

