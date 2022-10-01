package enigma;


/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Mingyan Zou
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */

    Permutation(String cycles, Alphabet alphabet) {
        _cycles = cycles.replaceAll("(\\s)+", "");
        _alphabet = alphabet;

    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles += cycle;

    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int c = wrap(p);
        char n = alphabet().toChar(c);
        char f = permute(n);
        int out = alphabet().toInt(f);
        return out;

    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int a = wrap(c);
        char n = alphabet().toChar(a);
        char f = invert(n);
        int out = alphabet().toInt(f);
        return out;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (_cycles.equals("") || !_alphabet.contains(p)) {
            return p;
        }
        int index = _cycles.indexOf(p);
        if (index != -1) {
            char result = _cycles.charAt(index + 1);
            if (result != ')') {
                return result;
            } else {
                int front = index;
                while (_cycles.charAt(front) != '(') {
                    front -= 1;
                }
                return _cycles.charAt(front + 1);
            }

        } else {
            return p;
        }





    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int index = _cycles.indexOf(c);
        if (_cycles.equals("")) {
            return c;
        }
        if (!_alphabet.contains(c)) {
            return c;
        }
        if (index != -1) {
            char curr = _cycles.charAt(index - 1);
            if (curr != '(') {
                return curr;
            } else {
                int next = index;
                while (_cycles.charAt(next) != ')') {
                    next = next + 1;

                }
                return _cycles.charAt(next - 1);
            }
        } else {
            return c;
        }

    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i++) {
            char c = _alphabet.toChar(i);
            if (c == permute(c)) {
                return false;
            }
        } return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** string of cycles.*/
    private String _cycles;


}
