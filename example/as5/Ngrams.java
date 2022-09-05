    import java.util.*;
import java.lang.*;
/**
 * Ngrams predictive probabilities for text
 */
public class Ngrams {
	/**
	 * The constructor would be a good place to compute and store the Ngrams probabilities.
	 * Take uncompressed input as a text string, and store a List of Maps. The n-th such
	 * Map has keys that are prefixes of length n. Each value is itself a Map, from
	 * characters to floats (this is the probability of the char, given the prefix).
	 */
	List<Map<String,Map<String, Float>>> ngram;  /* nb. suggestion only - you don't have to use
                                                     this particular data structure */


	public Ngrams(String input) {
		ngram = new ArrayList<>();
		for(int i = 0; i<= 5; i ++) {
			ngram.add(ngramProbsCalc(input, i));
		}
		System.out.println(ngram);
		// TODO fill this in.
	}

	/**
	 * Take a string, and look up the probability of each character in it, under the Ngrams model.
	 * Returns a List of Floats (which are the probabilities).
	 */
	public List<Float> findCharProbs(String mystring) {
		// TODO fill this in.
		return null;
	}

	/**
	 * Take a list of probabilites (floats), and return the sum of the logs (base 2) in the list.
	 */
	public float calcTotalLogProb(List<Float> charProbs) {
		// TODO fill this in.
		return 0;
	}

	/**
	 * Calc probabilites, train text
	 * @param s big big text
	 * @param n prefix's size
	 */
	public Map<String, Map<String, Float>> ngramProbsCalc(String s, int n) {
		Map<String, Map<String, Float>> probs = new HashMap<>();
		int len = s.length();
		Map<String, Map<String, Integer>> counts = new HashMap<>();
		for( int i = 0; i < (len - n); i ++){
			String prefix = s.substring(i, i + n + 1);
			// Next char
			String c = String.valueOf(s.charAt(i + n));
			if(counts.containsKey(prefix)) {
				Map<String, Integer> innerMap = counts.get(prefix);
				if(innerMap.containsKey(c)) {
					innerMap.put(c, innerMap.get(c) + 1);
				} else {
					innerMap.put(c, 1);
				}
				counts.put(prefix, innerMap);
			} else {
				Map<String, Integer> innerMap = new HashMap<>();
				innerMap.put(c, 1);

				counts.put(prefix, innerMap);
			}

		}
		Set<String> countKeys = counts.keySet();
		for (String key : countKeys) {
		    Map<String, Integer> innerMap = counts.get(key);
			Map<String, Float> propMap = new HashMap<>();
			int total = 0;
			for(String keyInner: innerMap.keySet()) {
				total += innerMap.get(keyInner);
			}
			for(String keyInner: innerMap.keySet()) {
				  propMap.put(keyInner, intToFloat(innerMap.get(keyInner)) / intToFloat(total));
			}
			probs.put(key, propMap);
		}
		return probs;

	}
	private Float intToFloat(int a) {
		return Float.parseFloat(String.valueOf(a));
	}
}
