package game0x88;

import org.junit.Test;

public class FindMateTest {

//        board = new Board0x88("r2qkb1r/pp2nppp/3p4/2pNN1B1/2BnP3/3P4/PPP2PPP/R2bK2R w qkQK -");          //cant find mate in 2
//        board = new Board0x88("8/k1p2ppp/8/P7/6P1/3q4/4r3/K7 b ");          //Mate in 1

    private Board0x88 board;

    //Mate in 2
    @Test
    public void findMateTest1() {
        Evaluator.setSearchDepth(4);
        board = new Board0x88("4r3/k1p2ppp/8/P7/6P1/3q4/1K6/8 b - -");
        board.printBoard();
        Evaluator.findBestMove(board);
    }
}
