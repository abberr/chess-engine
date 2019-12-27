package game;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CastlingTest {

    private Board board;

    @Test
    public void castlingRightsTest() {
        board = new Board("r1b1k2r/8/8/8/8/8/8/R3K2R b qkQK - 0 0");

        //Black
        board.executeMove("e8g8");
        assertTrue(board.getCastlingRightsString().equals("QK"));
        board.revertLastMove();
        assertTrue(board.getCastlingRightsString().equals("qkQK"));
        board.executeMove("h8f8");
        assertTrue(board.getCastlingRightsString().equals("qQK"));
        board.revertLastMove();
        assertTrue(board.getCastlingRightsString().equals("qkQK"));
        board.executeMove("a8b8");
        assertTrue(board.getCastlingRightsString().equals("kQK"));
        board.revertLastMove();

        //White
        board.executeMove("e1g1");
        assertTrue("White castling kingside", board.getCastlingRightsString().equals("qk"));
        board.revertLastMove();
        assertTrue(board.getCastlingRightsString().equals("qkQK"));
        board.executeMove("e1c1");
        assertTrue("White castling queenside", board.getCastlingRightsString().equals("qk"));
        board.revertLastMove();
        assertTrue(board.getCastlingRightsString().equals("qkQK"));
        board.executeMove("h1f1");
        assertTrue("White queenside rook move", board.getCastlingRightsString().equals("qkQ"));
        board.revertLastMove();
        assertTrue( board.getCastlingRightsString().equals("qkQK"));
        board.executeMove("a1b1");
        assertTrue("White kingside rook moved", board.getCastlingRightsString().equals("qkK"));
        board.revertLastMove();

        //Special cases
        board.executeMove("h8h1");
        assertTrue(board.getCastlingRightsString().equals("qQ"));
        board.revertLastMove();
        assertTrue(board.getCastlingRightsString().equals("qkQK"));

        board.executeMove("a8a1");
        assertTrue(board.getCastlingRightsString().equals("kK"));
        board.revertLastMove();
        assertTrue(board.getCastlingRightsString().equals("qkQK"));
    }

    @Test
    public void cantCastleWhenInCheckTest() {
        board = new Board("r3k2r/8/8/1Q6/8/8/8/4K3 b qkQK - 0 0");
        MoveList moves = board.getAvailableMoves(false);

        assertTrue("", moves.size() == 4);
    }

    @After
    public void tearDown() {
        board.printBoard();
    }
}
