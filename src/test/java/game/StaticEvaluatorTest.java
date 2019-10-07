package game;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StaticEvaluatorTest {

    private Board board;

    @Test
    public void valueShoudBeZeroOnStart() {
        board = new Board();

        assertTrue(StaticEvaluator.getValue(board.getSquares()) == 0);
    }

    @Test
    public void doubledPawn() {
        board = new Board("rnbqkbnr/1ppppppp/4p3/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
        board.printBoard();

        int value = StaticEvaluator.getValue(board.getSquares());

        assertTrue("White should be ahead if black has doubled pawn", value > 0);

    }

    //TODO: How to test this?
    @Ignore
    @Test
    public void kingSecurity() {
        board = new Board("rnbqkbnr/ppp2ppp/8/8/8/8/3PPPPP/RNBQKBNR w qkQK -");
        board.printBoard();

        int value = StaticEvaluator.getValue(board.getSquares());

        assertTrue(value > 0);

    }
}
