package tablut;

import org.junit.Test;
import static org.junit.Assert.*;
import ucb.junit.textui;
import java.util.List;

/** Junit tests for our Tablut Board class.
 *  @author Vivant Sakore
 */
public class TablutTests {

    /** Run the JUnit tests in this package. */
    public static void main(String[] ignored) {
        textui.runClasses(TablutTests.class);
    }

    /**
     * Tests legalMoves for white pieces to make sure it
     * returns all legal Moves.
     * This method needs to be finished and may need to be changed
     * based on your implementation.
     */
    @Test
    public void testLegalWhiteMoves() {
        Board b = new Board();

        List<Move> movesList = b.legalMoves(Piece.WHITE);

        assertEquals(56, movesList.size());

        assertFalse(movesList.contains(Move.mv("e7-8")));
        assertFalse(movesList.contains(Move.mv("e8-f")));

        assertTrue(movesList.contains(Move.mv("e6-f")));
        assertTrue(movesList.contains(Move.mv("f5-8")));

    }

    /**
     * Tests legalMoves for black pieces to make sure it
     * returns all legal Moves.
     * This method needs to be finished and may need to be changed
     * based on your implementation.
     */
    @Test
    public void testLegalBlackMoves() {
        Board b = new Board();

        List<Move> movesList = b.legalMoves(Piece.BLACK);

        assertEquals(80, movesList.size());

        assertFalse(movesList.contains(Move.mv("e8-7")));
        assertFalse(movesList.contains(Move.mv("e7-8")));

        assertTrue(movesList.contains(Move.mv("f9-i")));
        assertTrue(movesList.contains(Move.mv("h5-1")));

    }
    @Test
    public void testget() {
        Board b = new Board();
        assertEquals(Piece.BLACK, b.get(Square.sq("d1")));
        assertEquals(Piece.WHITE, b.get(Square.sq(3, 4)));
    }

    @Test
    public void testput() {
        Board b = new Board();
        b.put(Piece.BLACK, Square.sq(1, 1));
        b.put(Piece.EMPTY, Square.sq(1, 4));
        assertEquals(Piece.BLACK, b.get(Square.sq(1, 1)));
        assertEquals(Piece.EMPTY, b.get(Square.sq(1, 4)));

    }

    @Test
    public void testmove() {
        Board b = new Board();
        b.makeMove(Square.sq("h5"), Square.sq("h6"));
        b.makeMove(Square.sq("g5"), Square.sq("g2"));
        assertEquals(Piece.BLACK, b.get(Square.sq("h6")));
        assertEquals(Piece.EMPTY, b.get(Square.sq("h5")));
        assertEquals(Piece.WHITE, b.get(Square.sq("g2")));
        assertEquals(Piece.EMPTY, b.get(Square.sq("g5")));
    }
    @Test
    public void testcapture() {
        Board b = new Board();
        b.makeMove(Square.sq("i6"), Square.sq("f6"));
        b.makeMove(Square.sq("e6"), Square.sq("c6"));
        b.makeMove(Square.sq("d9"), Square.sq("d6"));
        b.makeMove(Square.sq("e5"), Square.sq("e6"));
        b.makeMove(Square.sq("f9"), Square.sq("f7"));
        b.makeMove(Square.sq("e4"), Square.sq("d4"));
        b.makeMove(Square.sq("e9"), Square.sq("d9"));

        assertEquals(Piece.EMPTY, b.get(Square.sq("d6")));
    }

    @Test
    public void testundo() {
        Board b = new Board();
        b.makeMove(Square.sq("f1"), Square.sq("f4"));
        b.makeMove(Square.sq("e4"), Square.sq("d4"));
        b.makeMove(Square.sq("i6"), Square.sq("h6"));
        b.makeMove(Square.sq("e7"), Square.sq("d7"));
        b.undo();
        b.undo();

        assertEquals(Piece.WHITE, b.get(Square.sq("e7")));
        assertEquals(Piece.BLACK, b.get(Square.sq("i6")));

    }


    private void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = target[Board.SIZE - 1 - row][col];
                b.put(piece, Square.sq(col, row));
            }
        }
        System.out.println(b);
    }

    static final Piece E = Piece.EMPTY;
    static final Piece W = Piece.WHITE;
    static final Piece B = Piece.BLACK;
    static final Piece K = Piece.KING;

    static final Piece[][] INITIALBOARDSTATE = {
            {E, E, E, B, B, B, E, E, E},
            {E, E, E, E, B, E, E, E, E},
            {E, E, E, E, W, E, E, E, E},
            {B, E, E, E, W, E, E, E, B},
            {B, B, W, W, K, W, W, B, B},
            {B, E, E, E, W, E, E, E, B},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, E, B, E, E, E, E},
            {E, E, E, B, B, B, E, E, E},
    };
}
