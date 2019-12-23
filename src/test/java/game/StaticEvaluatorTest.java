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

    @Test
    public void rookOnOpenFilesBonus() {
        board = new Board("r1r1kqnb/pp1ppppp/8/8/8/8/PP1PPPPP/R2RKQNB w qkQK -");

        int value = StaticEvaluator.getValue(board.getSquares());
        board.printBoard();

        assertTrue(value < -15);

        //Same but for white
        board = new Board("r2rkqnb/pp1ppppp/8/8/8/8/PP1PPPPP/R1R1KQNB w qkQK -");
        board.printBoard();

        value = StaticEvaluator.getValue(board.getSquares());

        assertTrue(value > 15);
    }

    @Test
    public void rookOnHalfOpenFilesBonus() {
        board = new Board("rr11kqnb/p1pppppp/8/8/8/8/PPPPPP1P/R1R1KQNB w qkQK -");
        int value = StaticEvaluator.getValue(board.getSquares());
        assertTrue(value <= -10);

        //Same but for white
        board = new Board("r1r1kqnb/p1pppppp/8/8/8/8/PPPPPP1P/RR11KQNB w qkQK -");

        value = StaticEvaluator.getValue(board.getSquares());

        assertTrue(value > 10);
    }

    @Test
    public void pawnEndgame() {
        board = new Board("7k/5ppp/2PP4/8/8/8/8/K7 w - -");
        assertTrue(board.getValue() > 0);
    }

    @Test
    public void bishopPairBonus() {
        board = new Board("rnbqknnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
        board.printBoard();
        assertTrue(board.getValue() > 50);
    }
}
