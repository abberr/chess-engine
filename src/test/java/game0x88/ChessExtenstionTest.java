package game0x88;

import org.junit.Test;

public class ChessExtenstionTest {

    @Test
    public void test() {
        Board0x88 board = new Board0x88("6k1/7p/2p1r3/8/1p6/2N2bB1/1PP2P1P/R5K1 w - - 0 32 ");
        Evaluator.setSearchDepth(7);
        Evaluator.findBestMove(board);


    }
}
