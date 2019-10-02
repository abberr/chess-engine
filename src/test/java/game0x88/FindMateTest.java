package game0x88;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class FindMateTest {

    private static int SEARCH_DEPTH = 4;

//        board = new Board0x88("r2qkb1r/pp2nppp/3p4/2pNN1B1/2BnP3/3P4/PPP2PPP/R2bK2R w qkQK -");          //cant find mate in 2
//        board = new Board0x88("8/k1p2ppp/8/P7/6P1/3q4/4r3/K7 b ");          //Mate in 1

    private Board0x88 board;

    @Before
    public void before() {
        Evaluator.reset();
    }

    //Mate in 2
    @Test
    public void findMateIn2Test1() {
        board = new Board0x88("4r3/k1p2ppp/8/P7/6P1/3q4/1K6/8 b - -");
        board.printBoard();
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("e8e2"));
    }

    @Test
    public void findMateIn1Test1() {
        board = new Board0x88("8/k1p2ppp/8/P7/6P1/3q4/4r3/K7 b - -");
        board.printBoard();
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("d3d1"));
    }
}
