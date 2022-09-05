
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * A new instance of HuffmanCoding is created for every run. The constructor is passed the full text
 * to be encoded or decoded, so this is a good place to construct the tree. You should store this
 * tree in a field and then use it in the encode and decode methods.
 */
public class HuffmanCoding {

	private HuffmanTree huffmanTree;
	private HashMap<Character, Integer> frequencyMap;
	private HashMap<Character, String> huffmanCodes;

	/**
	 * This would be a good place to compute and store the tree.
	 * HuffmanCoding
	 */
	public HuffmanCoding(String text) {
		this.frequencyMap = computeFrequency(text.toCharArray());
		this.huffmanTree = buildHuffmanTree(frequencyMap);
		this.huffmanCodes = translateTree(huffmanTree);
	}

	/**
	 * Take an input string, text, and encode it with the stored tree. Should return the encoded
	 * text as a binary string, that is, a string containing only 1 and 0.
	 */
	public String encode (String text) {
		char[] charArray = text.toCharArray();
		StringBuilder encoded = new StringBuilder();
		for (char c : charArray) {
			encoded.append(huffmanCodes.get(c));
		}
		return encoded.toString();
	}

	/**
	 * Take encoded input as a binary string, decode it using the stored tree, and return the
	 * decoded text as a text string.
	 */
	public String decode(String text) {
		StringBuilder decoded = new StringBuilder();
		char[] charArray = text.toCharArray();

		// traverse the huffman tree according to the encoded binary string
		int index = 0;
		HuffmanNode root = huffmanTree.getRoot();
		HuffmanNode nodePointer = root;
		while (index < charArray.length) {
			char charPointer = charArray[index];
			if (charPointer == '0') {
				nodePointer = nodePointer.getLeftNode();
				if (nodePointer.getLeftNode() == null) {
					decoded.append(nodePointer.getCha());
					nodePointer = root;
				}
			} else if (charPointer == '1') {
				nodePointer = nodePointer.getRightNode();
				if (nodePointer.getLeftNode() == null) {
					decoded.append(nodePointer.getCha());
					nodePointer = root;
				}
			}
			index++;
		}

		return decoded.toString();
	}

	/**
	 * The getInformation method is here for your convenience, you don't need to fill it in if you
	 * don't wan to. It is called on every run and its return value is displayed on-screen. You
	 * could use this, for example, to print out the encoding tree.
	 */
	public String getInformation() {
		// prints all the leaves of the encoding tree, i.e. the binary string of
		// each char
		StringBuilder sb = new StringBuilder("\nThe encoding of each char:\n");

		for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
			sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		}

		return sb.toString();
	}

	/**
	 * Compute the frequency of all characters in the given text.
	 */
	private HashMap<Character, Integer> computeFrequency(char[] charArray) {
		HashMap<Character, Integer> frequency = new HashMap<Character, Integer>();
		for (char c : charArray) {
			if (frequency.containsKey(c)) {
				frequency.put(c, frequency.get(c) + 1);
			} else {
				frequency.put(c, 1);
			}
		}
		return frequency;
	}

	/**
	 * Build the binary coding tree used in huffman coding algorithm.
	 *
	 * @param frequencyMap
	 * @return
	 */
	private HuffmanTree buildHuffmanTree(HashMap<Character, Integer> frequencyMap) {
		PriorityQueue<HuffmanNode> queue = new PriorityQueue<>();
		// add all leaves into the priority queue
		for (char c : frequencyMap.keySet()) {
			HuffmanNode hNode = new HuffmanNode(c);
			hNode.setFrequence(frequencyMap.get(c));
			queue.offer(hNode);
		}
		// Iterate through all leaves, poll them out and build towards the root.
		int size = queue.size();
		for (int i = 1; i < size; i++) {
			HuffmanNode hNode1 = queue.poll();
			HuffmanNode hNode2 = queue.poll();
			HuffmanNode parentNode = new HuffmanNode('\0');
			parentNode.setFrequence(hNode1.getFrequence() + hNode2.getFrequence());
			parentNode.setLeftNode(hNode1);
			parentNode.setRightNode(hNode2);
			hNode1.setParent(parentNode);
			hNode2.setParent(parentNode);
			queue.offer(parentNode);
		}
		// the only node left in the queue is the root
		return new HuffmanTree(queue.poll());
	}

	private HashMap<Character, String> translateTree(HuffmanTree huffmanTree) {
		// Traverse tree to assign codes:
		// if node has code c, assign c0 to left child, c1 to right child
		HashMap<Character, String> codings = new HashMap<>();

		Stack<HuffmanNode> stack = new Stack<>();

		stack.push(huffmanTree.getRoot());

		while (!stack.isEmpty()) {
			HuffmanNode poppedNode = stack.pop();
			HuffmanNode left = poppedNode.getLeftNode();
			HuffmanNode right = poppedNode.getRightNode();

			if (left != null) { // if left != null, then right cannot be null
				left.setCoding(poppedNode.getCoding() + '0');
				stack.push(left);
				right.setCoding(poppedNode.getCoding() + '1');
				stack.push(right);
			} else {
				codings.put(poppedNode.getCha(), poppedNode.getCoding());
			}
		}

		return codings;
	}

	private class HuffmanTree {
		private HuffmanNode root;

		public HuffmanTree(HuffmanNode root) {
			this.root = root;
		}

		public HuffmanNode getRoot() {
			return root;
		}
	}

	@SuppressWarnings("unused")
	private class HuffmanNode implements Comparable<HuffmanNode> {

		private final char cha;
		private String coding = "";
		private int frequency = 0;
		private HuffmanNode parent;
		private HuffmanNode leftNode;
		private HuffmanNode rightNode;

		public HuffmanNode(char cha) {
			this.cha = cha;
		}

		@Override
		public int compareTo(HuffmanNode other) {
			return this.frequency - other.frequency;
		}

		public int getFrequence() {
			return frequency;
		}

		public void setFrequence(int frequence) {
			this.frequency = frequence;
		}

		public char getCha() {
			return cha;
		}

		public String getCoding() {
			return this.coding;
		}

		public void setCoding(String coding) {
			this.coding = coding;

		}

		public void setParent(HuffmanNode parent) {
			this.parent = parent;
		}

		public HuffmanNode getLeftNode() {
			return leftNode;
		}

		public void setLeftNode(HuffmanNode leftNode) {
			this.leftNode = leftNode;
		}

		public HuffmanNode getRightNode() {
			return rightNode;
		}

		public void setRightNode(HuffmanNode rightNode) {
			this.rightNode = rightNode;
		}

		@Override
		public String toString() {
			return "char: " + this.cha + ", coding: " + this.coding + ".";
		}

	}
}