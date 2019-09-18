import game0x88.Board0x88;
import game0x88.Evaluator;
import game0x88.Move;
import game0x88.Pieces;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class QuiescenceTest {

    private Board0x88 board;

    @Before
    public void before() {
        board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
    }

    @Test
    public void quiescenceTest() {
        board = new Board0x88("4k3/8/4r3/4b3/4Q3/8/8/4K3 w - -");
        Evaluator.setSearchDepth(1);
        Move bestMove = Evaluator.findBestMove(board);

        assertTrue (bestMove.getCapturedPiece() == Pieces.EMPY_SQUARE);
    }

    @Test
    public void quiescenceTestTripleCapture() {
        board = new Board0x88("4k3/8/4r3/4b3/4Q3/4R3/8/4K3 w - -");
        board.printBoard();
        Evaluator.setSearchDepth(1);
        Move bestMove = Evaluator.findBestMove(board);

        assertTrue (bestMove.getMoveTo() == 0x44);
    }

}
