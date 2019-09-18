import game0x88.Board0x88;
import game0x88.Evaluator;
import game0x88.Move;
import game0x88.MoveGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class MoveGenTest {

    private Board0x88 board;

    @Before
    public void before() {
        board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
    }

    @Test
    public void perft5Test() {
        Evaluator.setSearchDepth(5);
        long calculations = Evaluator.perft(board);
        assertTrue(calculations == 4865609);
    }

    //Should take about 40s
    @Test
    public void perft6Test() {
        Evaluator.setSearchDepth(6);
        long calculations = Evaluator.perft(board);
        assertTrue(calculations == 119060324);
    }

    @Test
    public void perft7Test() {
        Evaluator.setSearchDepth(7);
        long calculations = Evaluator.perft(board);
        assertTrue(calculations == 3195901860l);
    }

    @Test
    public void quiscentMoveGenTest() {
        board = new Board0x88("4k3/8/4r3/4b3/4Q2p/8/8/4K3 w - -");
        MoveGenerator.setSearchModeQuiescence();
        List<Move> moves = board.getAvailableMoves(false);
        MoveGenerator.setSearchModeQuiescence();

//        board.printBoard();

        assertTrue(moves.size() == 2);
    }

}
