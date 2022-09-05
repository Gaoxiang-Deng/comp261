/**
 * A new KMP instance is created for every substring search performed. Both the
 * pattern and the text are passed to the constructor and the search method. You
 * could, for example, use the constructor to create the match table and the
 * search method to perform the search itself.
 */
public class KMP {
	String pattern;
	String text;

	public KMP(String pattern, String text) {
		// TODO maybe fill this in.
		this.pattern = pattern;
		this.text = text;
	}

	/**
	 * Perform KMP substring search on the given text with the given pattern.
	 * 
	 * This should return the starting index of the first substring match if it
	 * exists, or -1 if it doesn't.
	 */
	public static int search(String pattern, String text) {
		// TODO fill this in.r

		if (pattern.length() == 0 || text.length() == 0) {
			return -1;
		}

		int patLength = pattern.length();
		int textLength = text.length();
		int[] matchTable = matchTable(pattern);
		int patternIndex = 0;
		int textIndex = 0;

		while (textIndex + patternIndex < textLength) {
			if (pattern.charAt(patternIndex) == text.charAt(textIndex + patternIndex)) {
				patternIndex++;
				if (patternIndex == patLength) {
					return textIndex;
				}
			} else if (matchTable[patternIndex] == -1) {
				patternIndex = 0;
				textIndex = textIndex + patternIndex + 1;
			} else {
				textIndex = textIndex + patternIndex - matchTable[patternIndex];
				patternIndex = matchTable[patternIndex];
			}
		}

		return -1;
	}
	private static int[] matchTable(String pattern) {

		if (pattern.length() == 0) {
			return null;
		} else if (pattern.length() == 1) {
			return new int[] { -1 };
		}

		int patLength = pattern.length();
		int[] matchTable = new int[patLength];
		matchTable[0] = -1;
		matchTable[1] = 0;
		int index = 0;
		int pos = 2;

		while (pos < patLength) {
			if (pattern.charAt(pos - 1) == pattern.charAt(index)) {
				matchTable[pos] = index + 1;
				pos++;
				index++;
			} else if (index > 0) {
				index = matchTable[index];
			} else {
				matchTable[pos] = 0;
				pos++;
			}
		}

		for (int i = 0; i < matchTable.length; i++) {
			System.out.println("" + i + " : " + matchTable[i]);
		}

		return matchTable;
	}
}
