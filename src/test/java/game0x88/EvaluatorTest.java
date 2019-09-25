package game0x88;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class EvaluatorTest {

    private Board0x88 board;

    @Before
    public void before() {
        Evaluator.setSearchDepth(6);
    }

    @Ignore
    @Test
    public void test1() {
        board = new Board0x88("r1bq1k1r/ppp2ppp/3p4/3p2N1/3P4/8/PPP1QPPP/R3R1K1 b qk - 0 0 ");

        Move bestMove = Evaluator.findBestMove(board);
        assertTrue(bestMove.toString().equals("c8f5"));
    }

    //Takes about 10s
    //80% of time spent on sorting
    //With move ordering takes about 2.2s
    @Test
    public void test2() {
        board = new Board0x88("3kqb1r/1pp3pp/3p1n2/4pp2/rnPP4/4P3/PB3PPP/KBR1Q1NR b - -");

        Move bestMove = Evaluator.findBestMove(board);

        assertTrue(bestMove.toString().equals("a4xa2"));
    }

    @Test
    public void test3() {
        board = new Board0x88("r2r2k1/p1p1R1R1/6pp/1p6/2BP1p2/2P3P1/P4P1P/6K1 b - - 1 30");
        Move bestMove = Evaluator.findBestMove(board);


        assertTrue("Move is not null", bestMove != null);
    }

    @Test
    public void test4() {
        board = new Board0x88("1bk3rr/pp3Q2/4p3/2p1qp2/5p2/3P4/PPP2PP1/R1B2RK1 w - - 4 23");
        Move bestMove = Evaluator.findBestMove(board);
        System.out.println(bestMove);
        assertTrue("Move is not null", bestMove != null);
    }

    //2m49s with improved move ordering
    //3m 39s with hash (test fails)
    //4m 42s without
    @Ignore
    @Test
    public void test5() {
        Evaluator.setSearchDepth(6);
        String [] moves = "g1f3 g8f6 e2e3 d7d5 f1e2 b8c6 e1g1 e7e5 d2d4 f8d6 d4e5 c6e5 f3e5 d6e5 c2c4 e8g8 f2f4 e5d6 c4d5 c8f5 d1b3 d8d7 b3b7 f5b1 a1b1 d7f5 c1d2 f6d5 e2f3 d5b6 b7a6 a8d8 b1d1 d6c5 g1h1 f5c2 d1c1 c2b2 c1c5 d8d2 a6a7 d2f2 f1c1 f2f3 g2f3 b6d5 c5c2 b2b6 a7b6 c7b6 c1e1 f8e8 e1d1 d5e3 c2e2 g7g5 f4g5 e3d1 e2e8 g8g7 e8b8 d1c3 b8b6 c3a2 h2h4 a2c3 h4h5 c3d5 h5h6 g7f8 b6b8 f8e7 b8h8 d5f4 h8h7 f4g6 h1g2 g6f4 g2g3 f4g6 f3f4 e7f8 f4f5 g6e5 h7g7 e5c4 f5f6 c4e3 h6h7 f8e8 h7h8q e8d7 g7f7 d7d6 h8d8 d6c5 f7c7 c5b6 d8b8".split(" ");
        board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");

        for (int i = 0; i < moves.length; i++) {
            board.executeMove(moves[i]);
            if (i%2 == 0) {
                System.out.println(Evaluator.findBestMove(board));
            }
        }

        Move bestMove = Evaluator.findBestMove(board);
        board.printBoard();

        assertTrue("Move is not null", bestMove != null);
    }






    @After
    public void tearDown() {
        board.printBoard();
    }
}
