package game0x88;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RepetitionTest {

    private Board0x88 board;
    private static int SEARCH_DEPTH = 6;

    @Before
    public void before() {
        Evaluator.reset();
    }

    //TODO fix this
    @Ignore
    @Test
    public void test() {
        board = new Board0x88("k1K5/8/8/8/8/8/8/8 b - - ");
        board.printBoard();
        Evaluator.findBestMove(board, SEARCH_DEPTH);
        board.printBoard();
    }

    @Test
    public void test1() {
        board = new Board0x88("k1K5/8/8/8/8/NQ5n/8/8 w - - ");
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
    public void evalTest() {
        board = new Board0x88("4k2r/2pq1pp1/r4n1p/1Q2p3/1b2P3/1P3N2/1PP2PPP/2B2K1R w k - 1 17 ");
        Evaluator.findBestMove(board, SEARCH_DEPTH);
    }

    @Test
    public void evalTest2() {
        board = new Board0x88("rk1b4/ppp3pp/4pn2/4N3/1P6/P3P3/1B3PPP/2R3K1 w - - 0 21");
        board.executeMove("e5f7");
        board.executeMove("b8c8");
        board.executeMove("f7d6");
        board.executeMove("c8b8");
        board.executeMove("d6f7");
        Evaluator.findBestMove(board, SEARCH_DEPTH);

    }
}
