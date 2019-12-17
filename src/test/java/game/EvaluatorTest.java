package game;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

//All 10 puzzles: 20s
public class EvaluatorTest {

    private Board board;
    private static int SEARCH_DEPTH = 8;

    @Before
    public void before() {
        Evaluator.reset();
    }

    @Ignore
    @Test
    public void startingPos() {
        board = new Board();

        Evaluator.findBestMove(board, 10);
    }

    //Pv: a1b1, b2ax2, d1xd4
    @Test
    public void test1() {
        board = new Board("r4rk1/ppp2ppp/8/2P5/3nN3/8/Pq3PPP/R2QR1K1 w - - 0");

        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);
        assertTrue(bestMove.toString().equals("a1b1"));
    }

    //Should work on at least depth 6, needs depth 8 since null move was implemented
    @Test
    public void test2() {
        board = new Board("3kqb1r/1pp3pp/3p1n2/4pp2/rnPP4/4P3/PB3PPP/KBR1Q1NR b - -");

        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("a4xa2"));
    }

    //Pv:  g4xf5,f7xf5,h6xf4
    @Test
    public void test3() {
        board = new Board("6k1/3q1r1p/2n1p1pB/1p3r2/2pPQnB1/2P5/5PRP/6RK w - -");
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("g4xf5"));
    }

    //Pv: c3b4, a3xb4, c1xc8
    @Test
    public void test4() {
        board = new Board("2r5/1p1nkppp/1p2p3/1B1p1b2/P2P4/bPB1P3/N4PPP/2R3K1 w - -");
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("c3b4"));
    }


    //Pv: c3xa4, a8xa4, c2xa4
    @Test
    public void test5() {
        board = new Board("r4rk1/3nppp1/1q2p2p/1N1pP3/nP1P4/2NBBP2/2Q3PP/1R4K1 w Qq - 0 1");
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("c3xa4"));
    }

    //TODO dosnt work since lategame king piece square table
    //Pv: f4xe5, d6xe5, g5xe7
    @Ignore
    @Test
    public void test6() {
        board = new Board("7r/p3p3/1p1k4/2pPp1Bp/4PP1K/P7/1P2B3/8 w - - 0 1");
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("f4xe5"));
    }


    //Pv: e3c6, d7f7, c6xf8
    @Test
    public void test7() {
        board = new Board("r1b2rk1/ppp2pp1/2nq3p/7Q/5P2/3BB3/PPP3PP/R4RK1 w Qq - 0 1");
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("e3c5"));
    }

    //PV: c3xe4, d5xe4, b2xe2
    @Test
    public void test8() {
        board = new Board("r3k2r/ppq2pp1/2pbp3/3pNb1p/2PPnP2/1QN1n2P/PP2B1P1/2R1BRN1 w - -");
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("c3xe4"));
    }

    //PV d5c3, b2xc3, d7xd1
    @Test
    public void test9() {
        board = new Board("r2k3r/pppqbpQp/8/3nP3/8/5P2/PPP3PP/1K1R1BNR b - - 0");
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("d5c3"));
    }

    //PV: c6e5, f6xe5, c2xc8, a8xc8, c1xc8
    @Test
    public void test10() {
        board = new Board("r1r5/5k1p/1pNqppp1/pP1p4/P2PnP2/3QP3/2R3PP/2R3K1 w - -");
        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);

        assertTrue(bestMove.toString().equals("c6e5"));
    }

    //52s depth 7
    //2m19s depth 8 cache size 0xFFFFFFF
    //2m19s depth 8 cache size 0xFFFFFF
    @Ignore
    @Test
    public void testWholeGame() {
        String [] moves = "g1f3 g8f6 e2e3 d7d5 f1e2 b8c6 e1g1 e7e5 d2d4 f8d6 d4e5 c6e5 f3e5 d6e5 c2c4 e8g8 f2f4 e5d6 c4d5 c8f5 d1b3 d8d7 b3b7 f5b1 a1b1 d7f5 c1d2 f6d5 e2f3 d5b6 b7a6 a8d8 b1d1 d6c5 g1h1 f5c2 d1c1 c2b2 c1c5 d8d2 a6a7 d2f2 f1c1 f2f3 g2f3 b6d5 c5c2 b2b6 a7b6 c7b6 c1e1 f8e8 e1d1 d5e3 c2e2 g7g5 f4g5 e3d1 e2e8 g8g7 e8b8 d1c3 b8b6 c3a2 h2h4 a2c3 h4h5 c3d5 h5h6 g7f8 b6b8 f8e7 b8h8 d5f4 h8h7 f4g6 h1g2 g6f4 g2g3 f4g6 f3f4 e7f8 f4f5 g6e5 h7g7 e5c4 f5f6 c4e3 h6h7 f8e8 h7h8q e8d7 g7f7 d7d6 h8d8 d6c5 f7c7 c5b6 d8b8".split(" ");
        board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");

        for (int i = 0; i < moves.length; i++) {
            System.out.println("Executing move " + moves[i]);
            board.executeMove(moves[i]);
            if (i%2 == 0) {
                Move bestMove = Evaluator.findBestMove(board, 8);
                assertTrue(bestMove != null);
            }
        }

        Move bestMove = Evaluator.findBestMove(board, SEARCH_DEPTH);
        board.printBoard();

        assertTrue("Move is not null", bestMove != null);
    }

    @Ignore
    @Test
    public void bugTest() {
        board = new Board("2k1r3/pp4pp/1n5b/B1p2P2/2P1p3/PP1r1BP1/4K2P/1R5R w - - 2 22");
        board.executeMove("a5b6");

        Move bestMove = Evaluator.findBestMove(board, 15);

        assertTrue(bestMove != null);
    }

    @After
    public void tearDown() {
        board.printBoard();
    }
}
