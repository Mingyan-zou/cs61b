package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Mingyan Zou
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        if (!notches.equals("") && notches != null) {
            _notch = new int[notches.length()];
            for (int i = 0; i < notches.length(); i++) {
                _notch[i] = alphabet().toInt(notches.charAt(i));
            }
        } else {
            _notch = null;
        }


    }

    @Override
    boolean rotates() {
        return true;
    }




    @Override
    boolean atNotch() {
        if (_notch == null) {
            return false;
        } else {
            for (int c : _notch) {
                if (c == setting()) {
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    void advance() {
        set(permutation().wrap(setting() + 1));
    }


    /** return my notches.*/
    private int[] _notch;



}
