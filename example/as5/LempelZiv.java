
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A new instance of LempelZiv is created for every run.
 */
public class LempelZiv {

	private static int SEARCH_WINDOW_SIZE = 1024;

	/**
	 * Take uncompressed input as a text string, compress it, and return it as a text string.
	 */
	public String compress(String text) {
		StringBuilder sb = new StringBuilder();
		int cursor = 0;

		while (cursor < text.length()) {
			int lookAhead = 0;
			int previousMatch = cursor > SEARCH_WINDOW_SIZE ? SEARCH_WINDOW_SIZE : cursor;
			while (true) {
				int matchIndex = stringMatch(text, cursor, lookAhead);

				if (matchIndex >= 0) {
					previousMatch = matchIndex;
					lookAhead++;

				} else {
					int windowSize = cursor > SEARCH_WINDOW_SIZE ? SEARCH_WINDOW_SIZE : cursor;
					int offset = windowSize - previousMatch;
					Character nextChar;
					if (cursor + lookAhead < text.length()) {
						nextChar = text.charAt(cursor + lookAhead);
					} else {
						// it's the end of text
						nextChar = null;
					}

					sb.append(new Tuple(offset, lookAhead, nextChar).toString());
					cursor += lookAhead + 1;
					break;
				}
			}
		}

		return sb.toString();
	}

	private int stringMatch(String text, int cursor, int lookAhead) {
		// Window size must grow from 0 at beginning
		int searchWindowStart = cursor - SEARCH_WINDOW_SIZE < 0 ? 0 : cursor - SEARCH_WINDOW_SIZE;
		String searchWindow = text.substring(searchWindowStart, cursor);

		// cursor + lookahead + 1 mustn't go past end of text
		if (cursor + lookAhead + 1 > text.length()) {
			return -1;
		}

		String target = text.substring(cursor, cursor + lookAhead + 1);

		return KMP.search(target, searchWindow);
	}

	/**
	 * Take compressed input as a text string, decompress it, and return it as a text string.
	 */
	public String decompress(String compressed) {
		StringBuilder sb = new StringBuilder();
		int cursor = 0;

		// translate into a list of tuples
		ArrayList<Tuple> tuples = toTupleList(compressed);
		for (Tuple tuple : tuples) {
			if (tuple.offset == 0 && tuple.length == 0) {
				sb.append(tuple.nextChar);
				cursor++;
			} else {
				sb.append(sb.substring(cursor - tuple.offset, cursor - tuple.offset + tuple.length));
				cursor += tuple.length;
				// in case it's the end of text
				if (tuple.nextChar != null)
					sb.append(tuple.nextChar);
				cursor++;
			}
		}

		return sb.toString();
	}

	private ArrayList<Tuple> toTupleList(String compressed) {

		Scanner scan = new Scanner(new BufferedReader(new StringReader(compressed)));
		scan.useDelimiter("\\]");
		ArrayList<Tuple> tuples = new ArrayList<>();

		while (scan.hasNext()) {
			String str = scan.next();
			tuples.add(new Tuple(str));
		}

		scan.close();
		return tuples;
	}

	public String getInformation() {
		return "";
	}

	public static void setWindowSize(int size) {
		SEARCH_WINDOW_SIZE = size;
	}

	public static int getWindowSize() {
		return SEARCH_WINDOW_SIZE;
	}

	private class Tuple {

		private int offset;
		private int length;
		private Character nextChar;

		public Tuple(int offset, int length, Character nextChar) {
			this.offset = offset;
			this.length = length;
			this.nextChar = nextChar;
		}

		public Tuple(String text) {
			String[] values = text.split("\\|");
			this.offset = Integer.valueOf(values[0]);
			this.length = Integer.valueOf(values[1]);
			// in case it's the end of text
			if (values.length > 2) {
				this.nextChar = values[2].charAt(0);
			}
		}

		@Override
		public String toString() {
			if (nextChar == null) {
				return offset + "|" + length + "|]";
			}
			return offset + "|" + length + "|" + nextChar + "]";
		}
	}
}