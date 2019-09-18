import game0x88.Board0x88;
import game0x88.Evaluator;
import game0x88.Move;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PuzzleTest {

    @Before
    public void before() {
        Evaluator.setSearchDepth(5);
    }

    //TODO doesnt work
    @Test
    public void puzzle1Test() {
        Board0x88 board = new Board0x88("3kqb1r/1pp3pp/3p1n2/4pp2/rnPP4/4P3/PB3PPP/KBR1Q1NR b - -");

        Move bestMove = Evaluator.findBestMove(board);

        //best move is a5xa4
        assertTrue(bestMove.toString().equals("a5xa4"));
    }
}
