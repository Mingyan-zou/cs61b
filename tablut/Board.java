package tablut;


import java.util.List;
import java.util.Stack;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Formatter;
import java.util.ArrayList;


import static tablut.Piece.*;
import static tablut.Square.*;
import static tablut.Move.mv;


/** The state of a Tablut Game.
 *  @author Mingyan Zou
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 9;

    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
        NTHRONE = sq(4, 5),
        STHRONE = sq(4, 3),
        WTHRONE = sq(3, 4),
        ETHRONE = sq(5, 4);

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
        sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
        sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
        sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
        sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        }

        _turn = model.turn();
        _record = new Stack<String>();
        _moverecord = new Stack<Move>();
        _winner = model.winner();
        _moveCount = model._moveCount;

        _moveLimit = model._moveLimit;
        _board = new HashMap<Square, Piece>();
        _board.putAll(model._board);

        for (String r: model._record) {
            _record.add(r);
        }
        for (Move mv: model._moverecord) {
            _moverecord.add(mv);
        }



    }

    /** Clears the board to the initial position. */
    void init() {
        _moveCount = 0;
        _turn = BLACK;
        _repeated = false;
        _board = new HashMap<Square, Piece>();
        _record = new Stack<String>();
        _moveLimit = Integer.MAX_VALUE;
        _moverecord = new Stack<Move>();
        _winner = null;
        for (Square s: INITIAL_ATTACKERS) {
            put(BLACK, s);
        }
        for (Square s: INITIAL_DEFENDERS) {
            put(WHITE, s);
        }
        put(KING, THRONE);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (!_board.containsKey(sq(i, j))) {
                    put(EMPTY, sq(i, j));
                }
            }
        }
    }

    /** Set the move limit to LIM.  It is an error if 2*LIM <= moveCount().
     * @param n the number of move limit.*/
    void setMoveLimit(int n) {
        if (2 * n <= moveCount()) {
            throw new Error("the movecount outweighs the movelimits");
        } else {
            _moveLimit = n + _moveCount;
        }
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        return _repeated;
    }




    /** Record current position and set winner() next mover if the current
     *  position is a repeat. */
    private void checkRepeated() {
        int r = _record.search(this.encodedBoard());
        _record.push(this.encodedBoard());
        if (r == -1) {
            _repeated = false;
        } else {
            _repeated = true;
            _winner = turn().opponent();
        }

    }


    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _moveCount;
    }

    /** Return location of the king. */
    Square kingPosition() {
        for (int i = 0; i < NUM_SQUARES; i++) {
            if (get(sq(i)).equals(KING)) {
                return sq(i);
            }
        }

        return null;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        assert 0 <= col && col <= 9 && row >= 0 && row <= 9
                : "wrong cols and rows";
        return _board.get(sq(col, row));
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */

    final void put(Piece p, Square s) {
        if (get(s) == null) {
            _board.put(s, p);
        } else {
            _board.replace(s, p);
        }
    }


    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        put(p, s);
        checkRepeated();


    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
        Square.SqList sql = ROOK_SQUARES[from.index()][from.direction(to)];
        if (from.isRookMove(to)) {
            int dis;
            int a = Math.abs(from.col() - to.col());
            int b = Math.abs(from.row() - to.row());
            if (a == 0) {
                dis = b;
            } else {
                dis = a;
            }
            for (int i = 0; i < dis; i++) {
                Square sq = sql.get(i);
                if (!_board.get(sq).equals(EMPTY)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }



    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return get(from).side() == _turn;
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {
        if (!isLegal(from)) {
            return false;
        }
        if (to.equals(THRONE)) {
            if (!get(from).equals(KING)) {
                return false;
            }
        } else if (moveCount() > _moveLimit) {
            return false;
        } else if (!isUnblockedMove(from, to)) {
            return false;
        }
        return true;
    }

    /** return true iff move is a legal move.
     * @param from a square that I want to move from
     * @param to a square that I want to move to.*/
    boolean isLegal1(Square from, Square to) {
        if (to.equals(THRONE)) {
            if (!get(from).equals(KING)) {
                return false;
            }
        } else if (moveCount() > _moveLimit) {
            return false;
        } else if (!isUnblockedMove(from, to)) {
            return false;
        }
        return true;
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        assert isLegal(from, to);
        put(get(from), to);
        revPut(EMPTY, from);
        _moverecord.push(mv(from, to));
        if (kingPosition().isEdge()) {
            _winner = WHITE;
            return;
        }
        if (exists(to.col(), to.row() + 2)) {
            Square up = sq(to.col(), to.row() + 2);
            capture(up, to);
        }
        if (exists(to.col() + 2, to.row())) {
            Square right = sq(to.col() + 2, to.row());
            capture(right, to);
        }
        if (exists(to.col(), to.row() - 2)) {
            Square down = sq(to.col(), to.row() - 2);
            capture(down, to);
        }
        if (exists(to.col() - 2, to.row())) {
            Square left = sq(to.col() - 2, to.row());
            capture(left, to);
        }
        if (kingPosition() == null) {
            _winner = BLACK;
        }
        _turn = turn().opponent();
        _moveCount += 1;

    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        Square cap = sq0.between(sq2);
        Piece ca = get(cap);
        if (ca.equals(KING)) {
            if (isThrown(cap)) {
                Square up = sq(cap.col(), cap.row() + 1);
                Square right = sq(cap.col() + 1, cap.row());
                Square down = sq(cap.col(), cap.row() - 1);
                Square left = sq(cap.col() - 1, cap.row());
                if (isHostile(up, cap) && isHostile(right, cap)
                        && isHostile(down, cap) && isHostile(left, cap)) {
                    put(EMPTY, cap);
                    _winner = BLACK;
                }
            } else {
                if (isHostile(sq0, cap) && isHostile(sq2, cap)) {
                    put(EMPTY, cap);
                    _winner = BLACK;
                }
            }
        } else {
            if (isHostile(sq0, cap) && isHostile(sq2, cap)) {
                put(EMPTY, cap);
            }
        }
    }


    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            undoPosition();
            Move mv = _moverecord.peek();
            put(get(mv.to()), mv.from());
            put(EMPTY, mv.to());
            _moverecord.pop();

            _moveCount -= 1;
        }
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        if (_repeated || moveCount() == 0) {
            return;
        } else {
            _record.pop();
        }

    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        _moverecord.clear();
        _record.clear();
        _moveCount = 0;

    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        List<Move> list = new ArrayList<Move>();
        HashSet<Square> ss = pieceLocations(side);
        for (Square s: ss) {
            for (int i = 0; i < 4; i++) {
                SqList a = ROOK_SQUARES[s.index()][i];
                for (Square sq: a) {
                    if (isLegal1(s, sq)) {
                        list.add(mv(s, sq));
                    }
                }
            }
        }
        return list;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        return !legalMoves(side).isEmpty();
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /** Return the locations of all pieces on SIDE. */
    private HashSet<Square> pieceLocations(Piece side) {
        assert side != EMPTY;
        HashSet<Square> locations = new HashSet<Square>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (_board.get(sq(i, j)).equals(side)) {
                    locations.add(sq(i, j));
                }
                if (side.equals(WHITE) && _board.get(sq(i, j)).equals(KING)) {
                    locations.add(sq(i, j));
                }
            }
        }

        return locations;
    }

    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or null if it has not been
     *  computed. */
    private Piece _winner;
    /** Number of (still undone) moves since initial position. */
    private int _moveCount;
    /** True when current board is a repeated position (ending the game). */
    private boolean _repeated;
    /** the board that represents pieces on squares.*/
    private HashMap<Square, Piece> _board;
    /** the movelimits of my current board.**/
    private int _moveLimit;
    /** a stack of record that records each board.*/
    private Stack<String> _record;
    /** a stack of moves.*/
    private Stack<Move> _moverecord;





    /** check if the other is hostile to me.
     * @return true if the other square is hostile to me.
     * @param other a square other than me.
     * @param me my square.*/
    public boolean isHostile(Square other, Square me) {
        if (other.equals(THRONE)) {
            if (get(other).equals(EMPTY)) {
                return true;
            } else if (get(me).equals(WHITE)) {
                Square dia1 = me.diag1(other);
                Square dia2 = me.diag2(other);
                if (get(dia1).equals(get(dia2)) && get(dia1).equals(BLACK)) {
                    if (get(dia1.diag1(other)).equals(BLACK)
                            || get(dia1.diag2(other)).equals(BLACK)) {
                        return true;
                    }
                }
            } else if (get(me).equals(BLACK)) {
                return true;
            }
        } else if (get(other).equals(BLACK)) {
            if (get(me).equals(WHITE) || get(me).equals(KING)) {
                return true;
            }
        } else if (get(other).equals(WHITE) && get(me).equals(BLACK)) {
            return true;
        } else if (get(other).equals(KING) && get(me).equals(BLACK)) {
            return true;
        }
        return false;

    }
    /** check if the square is on thrown.
     * @param  s1 a square.
     * @return return true if i am on the throne positions.*/
    public boolean isThrown(Square s1) {
        return s1.equals(THRONE)
                || s1.equals(NTHRONE)
                || s1.equals(ETHRONE)
                || s1.equals(WTHRONE)
                || s1.equals(STHRONE);

    }


}
