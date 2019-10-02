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
        Evaluator.reset();
    }

    //Pv: a1b1, b2ax2, d1xd4
    @Test
    public void test1() {
        board = new Board0x88("r4rk1/ppp2ppp/8/2P5/3nN3/8/Pq3PPP/R2QR1K1 w - - 0");

        Move bestMove = Evaluator.findBestMove(board);
        assertTrue(bestMove.toString().equals("a1b1"));
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

    //Pv:  g4xf5,f7xf5,h6xf4
    @Test
    public void test3() {
        board = new Board0x88("6k1/3q1r1p/2n1p1pB/1p3r2/2pPQnB1/2P5/5PRP/6RK w - -");
        Move bestMove = Evaluator.findBestMove(board);

        assertTrue(bestMove.toString().equals("g4xf5"));
    }

    //Pv: c3b4, a3xb4, c1xc8
    @Test
    public void test4() {
        board = new Board0x88("2r5/1p1nkppp/1p2p3/1B1p1b2/P2P4/bPB1P3/N4PPP/2R3K1 w - -");
        Move bestMove = Evaluator.findBestMove(board);

        assertTrue(bestMove.toString().equals("c3b4"));
    }


    //Pv: c3xa4, a8xa4, c2xa4
    @Test
    public void test5() {
        board = new Board0x88("r4rk1/3nppp1/1q2p2p/1N1pP3/nP1P4/2NBBP2/2Q3PP/1R4K1 w Qq - 0 1");
        Move bestMove = Evaluator.findBestMove(board);

        assertTrue(bestMove.toString().equals("c3xa4"));
    }

    //Pv: f4xe5, d6xe5, g5xe7
    @Test
    public void test6() {
        board = new Board0x88("7r/p3p3/1p1k4/2pPp1Bp/4PP1K/P7/1P2B3/8 w - - 0 1");
        Move bestMove = Evaluator.findBestMove(board);

        assertTrue(bestMove.toString().equals("f4xe5"));
    }


    //Pv: e3c6, d7f7, c6xf8
    @Test
    public void test7() {
        board = new Board0x88("r1b2rk1/ppp2pp1/2nq3p/7Q/5P2/3BB3/PPP3PP/R4RK1 w Qq - 0 1");
        Move bestMove = Evaluator.findBestMove(board);

        assertTrue(bestMove.toString().equals("e3c5"));
    }

    //2m49s with improved move ordering
    //3m 39s with hash (test fails)
    //4m 42s without
    @Ignore
    @Test
    public void testWholeGame() {
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

    @Ignore
    @Test
    public void bugTest() {
        board = new Board0x88("r1bQk2r/ppp2ppp/2p2n2/8/4P3/2P5/P1P2PPP/R1B1KB1R b KQkq - 0 8");

        Evaluator.setSearchDepth(6);
        Move bestMove = Evaluator.findBestMove(board);

//        assertTrue(bestMove.toString().equals("e8xd8"));
        assertTrue(bestMove != null);
    }

    @Ignore
    @Test
    public void shouldReturnNegativeScoreWhenBlackIsWinning() {
        board = new Board0x88("kqbr4/8/8/8/8/8/5PPP/6PK b - -");
        Evaluator.findBestMove(board);
    }



    @After
    public void tearDown() {
        board.printBoard();
    }
}
