package game0x88;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MiscTest {

    @Test
    public void test1() {
        Board0x88 board = new Board0x88("r1bq1k1r/ppp2ppp/3p4/3p2N1/3P4/8/PPP1QPPP/R3R1K1 b qk - 0 0");

        board.printBoard();

        Evaluator.setSearchDepth(5);

        Move bestMove = Evaluator.findBestMove(board);
        assertTrue(bestMove.toString().equals("c8f5"));
    }
}
