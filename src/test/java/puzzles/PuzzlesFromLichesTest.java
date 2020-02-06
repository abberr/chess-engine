package puzzles;

import game.Board;
import game.Evaluator;
import game.Move;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PuzzlesFromLichesTest {

    @Test
    public void test1() {
        Board board = new Board("r1bq1rk1/1p2ppbp/p1np2p1/8/2PNP1n1/2N1B3/PP2BPPP/R2Q1R1K - - - -");

        Move bestMove = Evaluator.findBestMove(board, 3000L);
        assertTrue(bestMove.toString().equals("e2xg4"));

    }
}
