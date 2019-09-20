import game0x88.Board0x88;
import game0x88.Evaluator;
import game0x88.Move;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PuzzleTest {

    @Before
    public void before() {
        Evaluator.setSearchDepth(6);
    }

    //TODO doesnt work
    @Test
    public void puzzle1Test() {
        Board0x88 board = new Board0x88("3kqb1r/1pp3pp/3p1n2/4pp2/rnPP4/4P3/PB3PPP/KBR1Q1NR b - -");
        board.printBoard();

        Move bestMove = Evaluator.findBestMove(board);

        //best move is a4xa2
        assertTrue(bestMove.toString().equals("a4xa2"));
    }
}
