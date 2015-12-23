import java.util.*;
import java.io.*;

public class pw_check {
	private boolean isEmpty;
	static DLB dlbTrie = new DLB();
	static DLB dlbGoodPasswords = new DLB();
	static ArrayList<String> my_dictionaryWords = new ArrayList<String>();
	static boolean argsFlag = false;

	public static void main(String[] args) throws IOException {
		String searchPassword;
		// If there is a command line argument or it is equal to "-g"
		if (args.length > 0 || args.equals("-g")) {
			argsFlag = true;
			// Create a DLB with the bad passwords
			createDLB();
			// Generate all possible passwords according to the requirements
			passwordGeneration();
			// Generate a DLB from the good_passwords.txt
			passwordDLBGeneration();
		}

		// No command line arguments
		else {
			// In the description when there are no command line args, its just
			// says to search
			// It doesn'y say anything about creating my_dictionary.txt and
			// good_passwords.txt
			//There for I created a global boolean argsFlag that is true when there is a command line argument
			//When argsFlag is true I have if statements around creating the files
			createDLB();

			passwordDLBGeneration();

			Scanner inScan = new Scanner(System.in);

			// While loop that will ask the user to enter the password or quit
			while (true) {
				System.out.println("Enter Password or quit: ");
				searchPassword = inScan.nextLine();

				if (searchPassword.equals("quit"))
					break;

				//If the password length is < 5 or > 5 then it is not a valid password
				if (searchPassword.length() < 5 || searchPassword.length() > 5) {
					System.out
							.println("Passwords have to be 5 characters long.");
				
				}
				//Search the list of good passwords
				else if (dlbGoodPasswords.search(searchPassword))
					System.out.println("Congratulations your password, "
							+ searchPassword + ", is adequate");
				//The password is bad, therefore search for alternatives
				else {
					System.out.println("Your password, " + searchPassword
							+ " is subpar");
					// Find the other passwords
					badPasswordSearch(searchPassword);
				}
			}
		}
	}

	// The method that will search my_dictionary.txt with the substrings of word
	// in order to find the index that the password is bad at
	public static void badPasswordSearch(String word) throws IOException {
		int index = 0;
		if (dlbTrie.search(word.substring(0, 2))) {
			index = 1;
		}
		if (dlbTrie.search(word.substring(1, 3))) {
			index = 2;
		}
		if (dlbTrie.search(word.substring(2, 4))) {
			index = 3;
		}
		if (dlbTrie.search(word.substring(3, 5))) {
			index = 4;
		}
		if (dlbTrie.search(word.substring(0, 3))) {
			index = 2;
		}
		if (dlbTrie.search(word.substring(1, 4))) {
			index = 3;
		}
		if (dlbTrie.search(word.substring(2, 5))) {
			index = 4;
		}
		if (dlbTrie.search(word.substring(0, 4))) {
			index = 3;
		}
		if (dlbTrie.search(word.substring(1, 5)))
			index = 4;

		System.out.println("Word is " + word + " index is " + index);
		searchGoodPasswordFile(word, index);

	}

	// Theoretically searches the file "good_passwords.txt" for the word
	// substring
	public static void searchGoodPasswordFile(String word, int index)
			throws IOException {
		BufferedReader goodReader = new BufferedReader(new FileReader(
				"good_passwords.txt"));
		String currLine;
		//While good_passwords.txt still has lines
		while ((currLine = goodReader.readLine()) != null) {
			//If the current line sub string is equal to the word substring
			//Iterate through the next 10 words in the .txt file 
			if (currLine.substring(0, index).equals(word.substring(0, index))) {
				for (int i = 0; i < 10; i++)
					System.out
							.println("Passwords: " + goodReader.readLine());

				break;
			}

		}

	}

	// Method that uses 5 nested for loops to generate all possible combinations
	// of a password
	public static void passwordGeneration() throws IOException {
		char[] alphabet = { 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l',
				'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
				'y', 'z', '0', '2', '3', '5', '6', '7', '8', '9', '!', '@',
				'$', '%', '&', '*' };

		// Create a new PrintWriter to write to the good_passwords.txt later
		// when a password is verified to be good
		File goodPass = new File("good_passwords.txt");
		PrintWriter writer = new PrintWriter(goodPass);

		// 5 nested for loops that will iterate the array above that contains
		// all 42 valid characters in a password
		for (int i = 0; i < alphabet.length; i++) {
			for (int j = 0; j < alphabet.length; j++) {
				for (int h = 0; h < alphabet.length; h++) {
					for (int g = 0; g < alphabet.length; g++) {
						for (int k = 0; k < alphabet.length; k++) {
							// Create a new character array that will change
							// with each iteration of a for loop
							char passwords[] = { alphabet[i], alphabet[j],
									alphabet[h], alphabet[g], alphabet[k] };

							// nums is a counter for the amount of numbers in
							// the password
							// symbols is a counter for the amount of symbols in
							// the password
							int nums = 0, symbols = 0;

							// For loop that goes through the character array
							// and checks each char to find a number or symbol
							for (int f = 0; f < passwords.length; f++) {
								// ASCII num range is > 47 && < 58
								// If the character is within the range for 0-9
								// in ASCII nums++
								if (((int) passwords[f] > 47)
										&& ((int) passwords[f] < 58)) {
									nums++;
								}
								// Checks if each character is a symbol
								else if ((int) passwords[f] == 33
										|| (int) passwords[f] == 36
										|| (int) passwords[f] == 37
										|| (int) passwords[f] == 38
										|| (int) passwords[f] == 64
										|| (int) passwords[f] == 42) {
									symbols++;
								}
							}

							// Check the substrings
							// If the search returns true, it is a bad
							// password
							// If false, add to the good password list

							// A good password can only have a max of 2 nums and
							// a max of 2 symbols
							if ((nums > 0) && (nums < 3) && (symbols > 0)
									&& (symbols < 3)) {
								// Create new string that is the character array
								String pass = new String(passwords);
								// Create a boolean to see if the substring is
								// found when searching the bad passwords DLB
								boolean found = false;

								// If the search returns true, mark found as
								// true
								// All these if statements search through all
								// possible substrings > 1 && < 5
								if (dlbTrie.search(pass.substring(0, 2))) {
									found = true;
								}
								if (dlbTrie.search(pass.substring(1, 3))) {
									found = true;
								}
								if (dlbTrie.search(pass.substring(2, 4))) {
									found = true;
								}
								if (dlbTrie.search(pass.substring(3, 5))) {
									found = true;
								}
								if (dlbTrie.search(pass.substring(0, 3))) {
									found = true;
								}
								if (dlbTrie.search(pass.substring(1, 4))) {
									found = true;
								}
								if (dlbTrie.search(pass.substring(2, 5))) {
									found = true;
								}
								if (dlbTrie.search(pass.substring(0, 4))) {
									found = true;
								}
								if (dlbTrie.search(pass.substring(1, 5))) {
									found = true;
								}
								// If a substring was not found in the bad
								// passwords DLB, then the word is a good
								// password
								if (!found && argsFlag)
									writer.println(pass); // Write it to
															// good_passwords.txt

							}

						}
					}
				}
			}
		}
		writer.close(); // Close good_passwords.txt
		//The good_passwords.txt is empty only when argsFlag is true
		//When 
		if (goodPass.length() == 0)
			goodPass.delete();

	}

	// Reads a text file (dictionary.txt) and calls the add method w/ each line
	public static void createDLB() throws IOException {
		// Create a BufferedReader to read from the file
		BufferedReader reader = new BufferedReader(new FileReader(
				"dictionary.txt"));

		String currLine;
		// Read every valid line of the file
		while ((currLine = reader.readLine()) != null) {
			// Checks for invalid words - words cannot be over 4 characters or
			// less than 1 character
			if ((currLine.length() < 5) && (currLine.length() > 0)) {
				// Adds the word to the ArrayList
				my_dictionaryWords.add(currLine);
				// Inserts the word into the DLB
				dlbTrie.insert(currLine);
				// Calls the permutation method on the word
				// Description of permutation method is below
				permutation(currLine);
			}
		}

		// If argsFlag = true Create my_dictionary.txt and write to it
		if (argsFlag) {
			File output = new File("my_dictionary.txt");
			PrintWriter printWriter = new PrintWriter(output);
			// Iterates through the ArrayList, which contains all words in the
			// DLB,
			// and writes them to the text file my_dictionary.txt
			for (int i = 0; i < my_dictionaryWords.size(); i++)
				printWriter.write(my_dictionaryWords.get(i) + "\n");
			printWriter.close();
		}

		reader.close();
	}

	// Generates a DLB for all of the "good passwords" from good_passwords.txt
	public static void passwordDLBGeneration() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				"good_passwords.txt"));
		String currLine;
		while ((currLine = reader.readLine()) != null) {
			dlbGoodPasswords.insert(currLine);
		}
		reader.close();
	}

	// Method that will change the letters in words to numbers
	// Calls letterToNum with the word and index of the letter to change
	public static void permutation(String word) {
		String result;
		for (int i = 0; i < word.length(); i++) {
			// Checks each character of the word against the letters that need
			// to be changed
			if (word.charAt(i) == 't' || word.charAt(i) == 'a'
					|| word.charAt(i) == 'o' || word.charAt(i) == 'e'
					|| word.charAt(i) == 'i' || word.charAt(i) == 'l'
					|| word.charAt(i) == 's') {
				result = letterToNum(word, i); // letterToNum returns the
												// modified string
				permutation(result);
			}
		}

	}

	// Does the actual replacing of letters in the string and returns it
	public static String letterToNum(String str, int index) {
		// Create an empty string that will be built up during the method
		String buildWord = "";

		// For loop that goes through each character of str
		for (int i = 0; i < str.length(); i++) {
			// If i is the correct index, check if the character at that index
			// is a key letter then add the number that correspondes to that
			// letter to the empty string
			if (i == index) {
				//
				if (str.charAt(i) == 'i' || str.charAt(i) == 'l')
					buildWord += '1';

				if (str.charAt(i) == 't')
					buildWord += '7';

				if (str.charAt(i) == 'a')
					buildWord += '4';

				if (str.charAt(i) == 'o')
					buildWord += '0';

				if (str.charAt(i) == 'e')
					buildWord += '3';

				if (str.charAt(i) == 's')
					buildWord += '5';

			}
			// If i is not the specified index, add a character to the string
			else
				buildWord += str.charAt(i);

		}

		// Checks for duplicates
		// If the ArrayList already contains the word, don't add it to the DLB
		if (!my_dictionaryWords.contains(buildWord)) {
			my_dictionaryWords.add(buildWord);
			dlbTrie.insert(buildWord);
		}
		return buildWord;
	}
}
