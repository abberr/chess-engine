package game;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class QuiescenceTest {

    private Board board;
    private static int SEARCH_DEPTH = 1;

    @Before
    public void before() {
        Evaluator.reset();
        board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
    }

    @Test
    public void quiescenceTest() {
        board = new Board("4k3/8/4r3/4b3/4Q3/8/8/4K3 w - -");
        board.printBoard();
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.getCapturedPiece() == Pieces.EMPTY_SQUARE);
    }

    @Test
    public void quiescenceTestTripleCaptureTest() {
        board = new Board("4k3/8/4r3/4b3/4Q3/4R3/8/4K3 w - -");
        board.printBoard();
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        //White shouldnt capture because white loses more material in the exchange
        assertFalse(bestMove.toString().equals("e4xe5"));
    }

    @Test
    public void quiescenceTestTripleCaptureTest2() {
        board = new Board("4k3/8/4r3/4b3/4R3/4Q3/8/4K3 w - -");
        board.printBoard();
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        //White should capture because black loses more material in the exchange
        assertTrue(bestMove.toString().equals("e4xe5"));
    }

}
