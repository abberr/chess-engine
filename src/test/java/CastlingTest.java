import game0x88.Board0x88;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CastlingTest {

    @Test
    public void testCastling() {
        Board0x88 board = new Board0x88("r1b1k2r/8/2n5/2bqp3/8/3B1N2/8/R3K2R b qkQK - 0 0");
        board.printBoard();

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
}
