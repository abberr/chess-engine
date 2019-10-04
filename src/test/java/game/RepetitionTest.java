package game;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RepetitionTest {

    private Board board;
    private static int SEARCH_DEPTH = 6;

    @Before
    public void before() {
        Evaluator.reset();
    }

    @Test
    public void test1() {
        board = new Board("k1K5/8/8/8/8/NQ5n/8/8 w - - ");
        Evaluator.findBestMove(board, SEARCH_DEPTH);
        board.printBoard();
        board.executeMove("a3b1");
        board.executeMove("h3g1");
        board.executeMove("b1a3");
        assertTrue(board.getValue() > 0);
        board.executeMove("g1h3");
        assertTrue(board.isRepetition());
    }

    @Test
    public void evalTest2() {
        board = new Board("rk1b4/ppp3pp/4pn2/4N3/1P6/P3P3/1B3PPP/2R3K1 w - - 0 21");
        board.executeMove("e5f7");
        board.executeMove("b8c8");
        board.executeMove("f7d6");
        board.executeMove("c8b8");
        board.executeMove("d6f7");
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue("Force draw", bestMove.toString().equals("b8c8"));

    }
}
