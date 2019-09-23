package game0x88;

import org.junit.After;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class CastlingTest {

    private Board0x88 board;

    @Test
    public void castlingRightsTest() {
        board = new Board0x88("r1b1k2r/8/8/8/8/8/8/R3K2R b qkQK - 0 0");

        //Black
        board.executeMove("e8g8");
        assertTrue(board.getCastlingRights().equals("QK"));
        board.revertLastMove();
        assertTrue(board.getCastlingRights().equals("qkQK"));
        board.executeMove("h8f8");
        assertTrue(board.getCastlingRights().equals("qQK"));
        board.revertLastMove();
        assertTrue(board.getCastlingRights().equals("qkQK"));
        board.executeMove("a8b8");
        assertTrue(board.getCastlingRights().equals("kQK"));
        board.revertLastMove();

        //White
        board.executeMove("e1g1");
        assertTrue("White castling kingside", board.getCastlingRights().equals("qk"));
        board.revertLastMove();
        assertTrue(board.getCastlingRights().equals("qkQK"));
        board.executeMove("e1c1");
        assertTrue("White castling queenside", board.getCastlingRights().equals("qk"));
        board.revertLastMove();
        assertTrue(board.getCastlingRights().equals("qkQK"));
        board.executeMove("h1f1");
        assertTrue("White queenside rook move", board.getCastlingRights().equals("qkQ"));
        board.revertLastMove();
        assertTrue( board.getCastlingRights().equals("qkQK"));
        board.executeMove("a1b1");
        assertTrue("White kingside rook moved", board.getCastlingRights().equals("qkK"));
        board.revertLastMove();

        //Special cases
        board.executeMove("h8h1");
        assertTrue(board.getCastlingRights().equals("qQ"));
        board.revertLastMove();
        assertTrue(board.getCastlingRights().equals("qkQK"));

        board.executeMove("a8a1");
        assertTrue(board.getCastlingRights().equals("kK"));
        board.revertLastMove();
        assertTrue(board.getCastlingRights().equals("qkQK"));
    }

    @Test
    public void cantCastleWhenInCheckTest() {
        board = new Board0x88("r3k2r/8/8/1Q6/8/8/8/4K3 b qkQK - 0 0");
        List<Move> moves = board.getAvailableMoves(false);

        assertTrue("", moves.size() == 4);
    }

    @After
    public void tearDown() {
        board.printBoard();
    }
}
