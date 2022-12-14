package tablut;

import java.util.List;
import java.util.ArrayList;

import static java.lang.Math.*;

import static tablut.Piece.*;
import static tablut.Square.*;

/** A Player that automatically generates moves.
 *  @author Mingyan Zou
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        Piece turn = b.turn();
        int sense;
        if (turn.equals(BLACK)) {
            sense = -1;
        } else {
            sense = 1;
        }
        findMove(b, 4, true, sense, -INFTY, INFTY);

        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }

        int v = 0;

        if (sense == 1) {
            List<Move> moves = board.legalMoves(WHITE);
            v = Integer.MIN_VALUE + 1;
            for (Move mv: moves) {
                Board b = new Board(board);
                b.makeMove(mv);
                if (b.kingPosition().isEdge()) {
                    if (saveMove) {
                        _lastFoundMove = mv;
                    }
                    return WINNING_VALUE;
                }
                int score = findMove(b, depth - 1, false, -sense, alpha, beta);
                if (score >= v) {
                    v = score;
                    if (saveMove) {
                        _lastFoundMove = mv;
                    }
                }
                alpha = max(alpha, v);
                if (v >= beta) {
                    break;
                }
            }
        } else if (sense == -1) {
            List<Move> move = board.legalMoves(BLACK);
            v = Integer.MAX_VALUE - 1;
            for (Move mv: move) {
                Board b = new Board(board);
                b.makeMove(mv);
                if (b.kingPosition() == null) {
                    if (saveMove) {
                        _lastFoundMove = mv;
                    }
                    return -WINNING_VALUE;
                }
                int score = findMove(b, depth - 1, false, -sense, alpha, beta);
                if (score <= v) {
                    v = score;
                    if (saveMove) {
                        _lastFoundMove = mv;
                    }
                }
                beta = min(beta, v);
                if (v <= alpha) {
                    break;
                }
            }
        }

        return v;
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {




        return 4;
    }


    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        Square kingsq = board.kingPosition();
        if (kingsq == null) {
            return -WINNING_VALUE;
        }
        if (kingsq.isEdge()) {
            return WINNING_VALUE;
        }
        if (!board.isThrown(kingsq)) {
            int block = 0;
            ArrayList<Square> ss = surroundedsquare(kingsq);
            for (Square s: ss) {
                if (board.get(s).equals(BLACK)) {
                    block++;
                }
                if (block == 1) {
                    return -WILL_WIN_VALUE;
                }
            }
        }


        int ret = 0;

        for (int i = 0; i < NUM_SQUARES; i++) {
            if (board.get(sq(i)).equals(WHITE)) {
                ret++;
            } else if (board.get(sq(i)).equals(BLACK)) {
                ret--;
            }
        }

        return ret;

    }

    /** return a list of squares around sq.
     * @param sq my square */
    private ArrayList<Square> surroundedsquare(Square sq) {
        ArrayList<Square> ss = new ArrayList<Square>();
        int col = sq.col();
        int row = sq.row();
        if (exists(col, row + 1)) {
            ss.add(sq(col, row + 1));
        }
        if (exists(col + 1, row)) {
            ss.add(sq(col + 1, row));
        }
        if (exists(col, row - 1)) {
            ss.add(sq(col, row - 1));
        }
        if (exists(col - 1, row)) {
            ss.add(sq(col - 1, row));
        }
        return ss;
    }

}
