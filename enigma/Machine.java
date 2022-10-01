package enigma;

import java.util.HashMap;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Mingyan Zou
 */
class Machine {


    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        assert 1 < numRotors : "number of rotors should be more than one";
        assert 0 <= pawls && pawls < numRotors : "wrong number of pawls";
        _numrotors = numRotors;
        _pawls = pawls;
        _mycollection = new HashMap<String, Rotor>();
        _allRotors  = allRotors;
        for (Rotor r : allRotors) {
            _mycollection.put(r.name().toUpperCase(), r);
        }
        _plug = new Permutation("", new Alphabet());
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {

        return _numrotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {

        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _myrotors = new Rotor[numRotors()];
        if (rotors.length > numRotors()) {
            System.arraycopy(rotors, 0, rotors, 0, _numrotors);
        }
        for (Rotor r : _allRotors) {
            r.set(0);
        }
        for (int i = 0; i < rotors.length; i++) {
            String name = rotors[i].toUpperCase();
            if (_mycollection.containsKey(name)) {
                if (i == 0 && !_mycollection.get(name).reflecting()) {
                    throw error("the first rotor is not a reflector!");
                }

                if (i < _numrotors - _pawls
                        && _mycollection.get(name).rotates()) {
                    throw error("there supposed " + "to be a fixed rotor");
                }
                _myrotors[i] = _mycollection.get(name);
            } else {
                throw new EnigmaException("bad rotor name");
            }
        }
    }



    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        assert setting.length() == numRotors() - 1
                : "incorrect length of setting";
        for (int i = 0; i < setting.length(); i++) {
            assert _alphabet.contains(setting.charAt(i))
                    : "Setting is out of bound";
        }
        int j = 1;
        for (int i = 0; i < setting.length(); i++) {
            _myrotors[j].set(setting.charAt(i));
            j += 1;
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plug = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        int output = _plug.permute(c);

        int fixed = _numrotors - _pawls;
        boolean[] moving = new boolean[_pawls];
        moving[_pawls - 1] = true;


        for (int i = fixed + 1; i < _numrotors; i++) {
            if (_myrotors[i].atNotch()) {
                moving[i - fixed] = true;
                moving[i - fixed - 1] = true;
            }
        }
        for (int i = _pawls - 1; i >= 0; i--) {
            if (moving[i]) {
                _myrotors[i + fixed].advance();
            } else {
                break;
            }
        }

        for (int i = _numrotors - 1; i >= 0; i--) {
            output = _myrotors[i].convertForward(output);
        }
        for (int i = 1; i < _numrotors; i++) {
            output = _myrotors[i].convertBackward(output);
        }
        output = _plug.invert(output);


        return output;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String codes = "";
        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);
            int pos = convert(_alphabet.toInt(c));
            char co = _alphabet.toChar(pos);
            codes += String.valueOf(co);
        }

        return codes;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** give number of rotors.*/
    private int _numrotors;
    /** give number of pawls.*/
    private int _pawls;
    /** a hash map for my collections.*/
    private HashMap<String, Rotor> _mycollection;
    /**give my plugboard.*/
    private Permutation _plug;
    /**give my rotors array.*/
    private Rotor[] _myrotors;
    /**give my collection rotor.*/
    private Collection<Rotor> _allRotors;
    /** return my rotors.*/
    public Rotor[] getMyRotors() {
        return _myrotors;
    }
}
