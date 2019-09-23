package game0x88;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class QuiescenceTest {

    private Board0x88 board;

    @Before
    public void before() {
        board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
    }

    @Test
    public void quiescenceTest() {
        board = new Board0x88("4k3/8/4r3/4b3/4Q3/8/8/4K3 w - -");
        board.printBoard();
        Evaluator.setSearchDepth(1);
        Move bestMove = Evaluator.findBestMove(board);

        assertTrue (bestMove.getCapturedPiece() == Pieces.EMPTY_SQUARE);
    }

    @Test
    public void quiescenceTestTripleCaptureTest() {
        board = new Board0x88("4k3/8/4r3/4b3/4Q3/4R3/8/4K3 w - -");
        Evaluator.setSearchDepth(1);
        board.printBoard();
        Move bestMove = Evaluator.findBestMove(board);

        //White shouldnt capture because white loses more material in the exchange
        assertFalse(bestMove.toString().equals("e4xe5"));
    }

    @Test
    public void quiescenceTestTripleCaptureTest2() {
        board = new Board0x88("4k3/8/4r3/4b3/4R3/4Q3/8/4K3 w - -");
        Evaluator.setSearchDepth(1);
        board.printBoard();
        Move bestMove = Evaluator.findBestMove(board);

        //White should capture because black loses more material in the exchange
        assertTrue(bestMove.toString().equals("e4xe5"));
    }

}
