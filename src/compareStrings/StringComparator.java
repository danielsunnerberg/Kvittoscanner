package compareStrings;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class to compare for comparing strings.
 *
 * Created by gustavbergstrom on 2017-02-15.
 */
public class StringComparator {

	/**
	 * Calculates the similarity (a number within 0 and 1) between two strings.
	 *
	 * @param s1 the first string
	 * @param s2 the second string
	 */
	public static double getSimilarity(String s1, String s2) {

		int longerLength = Math.max(s1.length(), s2.length());
		
		if (longerLength == 0) {
			// Both strings have length zero.
			return 1.0;
		} else {
			// Calculate and return the similarity value, using the Levenshtein distance
			return (longerLength - StringUtils.getLevenshteinDistance(s1, s2)) /
					(double) longerLength;
		}
	}
}
