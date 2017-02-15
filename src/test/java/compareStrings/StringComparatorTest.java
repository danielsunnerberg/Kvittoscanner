package compareStrings;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by gustavbergstrom on 2017-02-15.
 */
public class StringComparatorTest {

	@Test
	public void testGetSimilarity (){
		String string1 = "abc";
		String string2 = "abc";

		assertEquals(StringComparator.getSimilarity(string1, string2), 1.0, 10e-10);

		string2 = "abd";

		assertEquals(StringComparator.getSimilarity(string1, string2), 0.6666666667, 10e-10);

		string2 = "ade";

		assertEquals(StringComparator.getSimilarity(string1, string2), 0.3333333333, 10e-10);
	}
}
