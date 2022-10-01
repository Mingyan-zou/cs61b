package enigma;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Mingyan Zou
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);


        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enigma = readConfig();

        boolean noSetting = true;
        if (!_input.hasNextLine()) {
            throw new EnigmaException("invalid input");
        }

        while (_input.hasNextLine()) {
            String line = _input.nextLine().trim();

            if (line.replaceAll("(\\s)+", "").equals("")) {
                _output.println();
                continue;
            }
            if (line.charAt(0) == '*') {
                setUp(enigma, line.replaceAll("\\*", "").trim());
                noSetting = false;
            } else {
                if (noSetting) {
                    throw error("Missing setting!");
                }
                String print = enigma.convert(line.replaceAll(" ", ""));
                printMessageLine(print);
            }

        }

    }




    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            ArrayList<Rotor> allrotors = new ArrayList<>();
            String alphabet = _config.next();
            alphabet = alphabet.replaceAll(" ", "").trim();
            if (alphabet.contains("+") || alphabet.contains("(")
                    || alphabet.contains(")") || alphabet.contains("*")) {
                throw error("alphabet includes wrong symbols");
            }
            _alphabet = new Alphabet(alphabet);
            if (!_config.hasNextInt()) {
                throw new EnigmaException(
                        "Wrong configuration of format, no rotor number!");
            }
            _numrotors = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw new EnigmaException("wrong configuration, no pawls");
            }
            _pawls = _config.nextInt();
            temp = _config.next();
            while (_config.hasNext()) {
                Rotor r = readRotor();
                allrotors.add(r);
            }

            return new Machine(_alphabet, _numrotors, _pawls, allrotors);

        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }



    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = temp;
            String state = _config.next().trim();
            String perm = "";
            temp = _config.next().trim();
            while (temp.contains("(") && _config.hasNext()) {
                if (!temp.contains(")")) {
                    throw new EnigmaException(
                            "invalid configuration for permutation");
                }
                perm = perm.concat(temp + " ");
                temp = _config.next();
            }
            if (!_config.hasNext()) {
                perm = perm.concat(temp + " ");
            }
            Permutation permutation = new Permutation(perm.trim(), _alphabet);
            if (state.toUpperCase().charAt(0) == 'M') {
                String notches = state.substring(1);
                if (notches == null) {
                    throw new EnigmaException(
                            "invalid notches of moving rotor");
                }
                return new MovingRotor(name.toUpperCase(),
                        permutation, notches);
            } else if (state.toUpperCase().charAt(0) == 'N') {
                return new FixedRotor(name.toUpperCase(), permutation);
            } else if (state.toUpperCase().charAt(0) == 'R') {
                return new Reflector(name.toUpperCase(), permutation);
            } else {
                throw new EnigmaException("bad rotor");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] tem = settings.trim().split("(\\s)+");
        int n = _numrotors;
        String[] names = new String[n];
        System.arraycopy(tem, 0, names, 0, n);
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (names[i] == names[j]) {
                    throw new EnigmaException(
                            "rotors in settings are not unique!");
                }

            }
        }
        M.insertRotors(names);
        M.setRotors(tem[n]);
        String perms = "";
        for (int i = n + 1; i < tem.length; i++) {
            perms += tem[i].trim();
        }
        Permutation plug = new Permutation(perms, _alphabet);
        M.setPlugboard(plug);


    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            if (i % 6 == 0) {
                msg = msg.substring(0, i) + " " + msg.substring(i);
            }
        } _output.println(msg.trim());
    }
    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;


    /** Source of machine configuration. */
    private Scanner _config;
    /**give the number of rotors.*/
    private int _numrotors;
    /**give the number of pawls.*/
    private int _pawls;
    /**a temporary string that scanner output.*/
    private String temp;
    /** File for encoded/decoded messages.*/
    private PrintStream _output;
}
