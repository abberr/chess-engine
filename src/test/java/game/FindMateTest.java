package game;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class FindMateTest {

    private static int SEARCH_DEPTH = 4;

    private Board board;

    @Before
    public void before() {
        Evaluator.reset();
    }

    @Test
    public void mateInThree() {
        board = new Board("r5rk/5p1p/5R2/4B3/8/8/7P/7K w - -");
        board.printBoard();
        Move bestMove = Evaluator.findBestMove(board, 10);
    }

    //Mate in 2
    @Test
    public void mateInTwo() {
        board = new Board("4r3/k1p2ppp/8/P7/6P1/3q4/1K6/8 b - -");
        board.printBoard();
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("e8e2"));
    }

    @Test
    public void mateInTwo_2() {
        board = new Board("r2qkb1r/pp2nppp/3p4/2pNN1B1/2BnP3/3P4/PPP2PPP/R2bK2R w qkQK - -");
        board.printBoard();
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("d5f6"));
    }

    @Test
    public void MateInOne() {
        board = new Board("8/k1p2ppp/8/P7/6P1/3q4/4r3/K7 b - -");
        board.printBoard();
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("d3d1"));
    }
}
