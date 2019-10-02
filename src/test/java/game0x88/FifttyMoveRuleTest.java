package game0x88;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FifttyMoveRuleTest {

    private Board0x88 board;

    @Test
    public void clockResetsOnPawnMove() {
        board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK - 0");
        assertTrue(board.getHalfMoveClock() == 0);
        board.executeMove("b1c3");
        assertTrue(board.getHalfMoveClock() == 1);
        board.executeMove("b8c6");
        assertTrue(board.getHalfMoveClock() == 2);
        board.executeMove("e2e4");
        assertTrue(board.getHalfMoveClock() == 0);
    }

    @Test
    public void clockResetsOnCapture() {
        board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK - 0");
        board.executeMove("d2d4");
        board.executeMove("b8c6");
        board.executeMove("b1c3");
        assertTrue(board.getHalfMoveClock() == 2);
        board.executeMove("c6d4");
        assertTrue(board.getHalfMoveClock() == 0);
    }

    @Test
    public void clockGetsOldValueOnRevert() {
        board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK - 0");
        board.executeMove("b1c3");
        board.executeMove("b8c6");
        assertTrue(board.getHalfMoveClock() == 2);
        board.revertLastMove();
        assertTrue(board.getHalfMoveClock() == 1);
        board.executeMove("e7e5");
        assertTrue(board.getHalfMoveClock() == 0);
        board.revertLastMove();
        assertTrue(board.getHalfMoveClock() == 1);
    }
}
