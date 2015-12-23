import java.util.*;

public class DLB {
	// Initialize root as 'empty' with a null child and sibling (next)
	Node root = new Node(' ', null, null);

	// Insert method for the DLB
	public void insert(String word) {
		// Set current node equal to the root
		Node currentNode = root;
		// For loop that will search the child and then the next node
		for (int i = 0; i < word.length(); i++) {
			// If the child is null, create a new child node. Go to the child
			// node
			if (currentNode.child == null)
				currentNode.child = new Node(word.charAt(i), null, null);
			currentNode = currentNode.child;

			// While loop that will execute as long as the current nodes
			// character is not equal to the character in the word at i
			while (currentNode.charValue != word.charAt(i)) {
				// If the next node is not null, go to it
				if (currentNode.next != null)
					currentNode = currentNode.next;
				// Else create a next node and move to it
				else {
					currentNode.next = new Node(word.charAt(i), null, null);
					currentNode = currentNode.next;
				}
			}
		}
	}

	// Search method that takes the string as a parameter
	public boolean search(String word) {
		// Current node is set to the root's child. The root is always 'empty'
		Node currentNode = root.child;
		// For loop that will only terminate when the node is null, all
		// characters are matched, or if the next node is null
		for (int i = 0; i < word.length();) {
			if (currentNode == null)
				return false;
			if (currentNode.charValue == word.charAt(i)) {
				currentNode = currentNode.child;
				i++;
			} else if (currentNode.next != null)
				currentNode = currentNode.next;
			else if (currentNode.next == null)
				return false;

		}
		//If the for loop executes fully, the word is found
		return true;
	}

	//Private class for the Node's in the DLB
	private class Node {
		private Node next;
		private Node child;
		private char charValue;

		//Constructor
		private Node(char character, Node childNode, Node nextNode) {
			this.charValue = character;
			this.child = childNode;
			this.next = nextNode;
		}
	}
}
