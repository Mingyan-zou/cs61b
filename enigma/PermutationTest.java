package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Mingyan Zou
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    @Test
    public void checkPermute() {
        perm = new Permutation("(ABCD) (EF)", UPPER);
        assertEquals(1, perm.permute(0));
        assertEquals(0, perm.permute(3));
    }
    @Test
    public void checkInvert() {
        perm = new Permutation("(ABCDEF) (GH)", UPPER);
        assertEquals(0, perm.invert(1));
        assertEquals(3, perm.invert(4));
        assertEquals(5, perm.invert(0));
        assertEquals(7, perm.invert(6));
    }

    @Test
    public void checkCPermute() {
        perm = new Permutation("(ABCDHY) (EF) (G)", UPPER);
        assertEquals('B', perm.permute('A'));
        assertEquals('A', perm.permute('Y'));
        assertEquals('G', perm.permute('G'));
        assertEquals('E', perm.permute('F'));
    }

    @Test
    public void checkCInvert() {
        perm = new Permutation("(ABCDEF) (GH)", UPPER);
        assertEquals('A', perm.invert('B'));
        assertEquals('F', perm.invert('A'));
        assertEquals('H', perm.invert('G'));
    }

}
