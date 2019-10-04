package game;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PerftTest {
    private Board board;

    @Before
    public void before() {
        board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
    }


    @Test
    public void perft2Test() {
        long calculations = Perft.perftDetailed(board, 2);
        assertTrue(calculations == 400);
    }

    @Test
    public void perft3Test() {
        long calculations = Perft.perftDetailed(board, 3);
        assertTrue(calculations == 8902);
    }

    @Test
    public void perft4Test() {
        long calculations = Perft.perftDetailed(board, 4);
        assertTrue(calculations == 197281);
    }

    //Should take about 1.5seconds
    @Test
    public void perft5Test() {
        long calculations = Perft.perftDetailed(board, 5);
        assertTrue(calculations == 4865609);
    }

    //Should take about 40s
    @Ignore
    @Test
    public void perft6Test() {
        long calculations = Perft.perft(board, 6);
        assertTrue(calculations == 119060324);
    }

    @Test
    @Ignore
    public void perft7Test() {
        long calculations = Perft.perftDetailed(board, 7);
        assertTrue(calculations == 3195901860l);
    }


    @Test
    public void perftPos2Depth3Test() {
        board = new Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
        long nodes = Perft.perftDetailed(board, 3);
        assertTrue("captures", Perft.capturesCounter == 17102);
        assertTrue("EPs", Perft.epCounter == 45);
        assertTrue("check", Perft.checkCounter == 993);
        assertTrue("castling", Perft.castlingCounter == 3162);
        assertTrue("promo", Perft.promotionsCounter == 0);
        assertTrue(nodes == 97862);
    }

    @Test
    public void perftPos2Depth4Test() {
        board = new Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
        long nodes = Perft.perftDetailed(board, 4);
        assertTrue("EPs", Perft.epCounter == 1929);
        assertTrue("castling", Perft.castlingCounter == 128013);
        assertTrue("captures", Perft.capturesCounter == 757163);
        assertTrue("promos", Perft.promotionsCounter == 15172);
        assertTrue("nodes" ,Perft.nodesCounter == 4085603);
    }

    @Test
    @Ignore
    public void perftPos2Depth5Test() {
        board = new Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
        long nodes = Perft.perftDetailed(board, 5);
        assertTrue("EPs", Perft.epCounter == 73365);
        assertTrue("captures", Perft.capturesCounter == 35043416);
        assertTrue("promos", Perft.promotionsCounter == 8392);
        assertTrue("nodes" ,Perft.nodesCounter == 193690690);
        assertTrue("checks" ,Perft.checkCounter == 3309887);
        assertTrue("castles" ,Perft.castlingCounter == 4993637);
    }

    @Test
    public void perftPos3Depth2Test() {
        board = new Board("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
        long nodes = Perft.perftDetailed(board, 2);
        assertTrue("EPs", Perft.epCounter == 0);
        assertTrue("captures", Perft.capturesCounter == 14);
        assertTrue("promos", Perft.promotionsCounter == 0);
        assertTrue("checks" ,Perft.checkCounter == 10);
        assertTrue("castles" ,Perft.castlingCounter == 0);
        assertTrue("nodes" ,Perft.nodesCounter == 191);
    }

    @Test
    public void perftPos3Depth5Test() {
        board = new Board("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
        long nodes = Perft.perftDetailed(board, 5);
        assertTrue("EPs", Perft.epCounter == 1165);
        assertTrue("captures", Perft.capturesCounter == 52051);
        assertTrue("promos", Perft.promotionsCounter == 0);
        assertTrue("checks" ,Perft.checkCounter == 52950);
        assertTrue("castles" ,Perft.castlingCounter == 0);
        assertTrue("nodes" ,Perft.nodesCounter == 674624);
    }

    @Test
    public void perftPos3Depth6Test() {
        board = new Board("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
        Perft.perftDetailed(board, 6);
        assertTrue("EPs", Perft.epCounter == 33325);
        assertTrue("captures", Perft.capturesCounter == 940350);
        assertTrue("checks" ,Perft.checkCounter == 452473);
        assertTrue("castles" ,Perft.castlingCounter == 0);
        assertTrue("promos", Perft.promotionsCounter == 7552);
        assertTrue("nodes" ,Perft.nodesCounter == 11030083);
    }

    @Test
    public void perftPos4Depth2Test() {
        board = new Board("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        Perft.perftDetailed(board, 4);
//        assertTrue("EPs", Perft.epCounter == 33325);
//        assertTrue("captures", Perft.capturesCounter == 940350);
//        assertTrue("promos", Perft.promotionsCounter == 7552);
//        assertTrue("checks" ,Perft.checkCounter == 452473);
//        assertTrue("castles" ,Perft.castlingCounter == 0);
        assertTrue("nodes" ,Perft.nodesCounter == 422333);
    }


    //http://www.rocechess.ch/perft.html
    @Test
    public void perftPromotionBugTest() {
      board = new Board("n1n5/PPPk4/8/8/8/8/4Kppp/5N1N b - - 0 1");
      Perft.perftDetailed(board, 4);

      assertTrue(Perft.nodesCounter == 182838);
    }

    @After
    public void tearDown() {
        board.printBoard();
    }

}
