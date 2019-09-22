package game0x88;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PerftTest {
    private Board0x88 board;

    @Before
    public void before() {
        board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
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
    @Test
    public void perft6Test() {
        long calculations = Perft.perft(board, 6);
        assertTrue(calculations == 119060324);
    }

//    3195903162 moves calculated in 1292081ms. Evaluations per second: 2473454.2
//    captures: 108329926
//    promos: 0
//    ep: 319617
//    checks: 33103848
//    castles: 884755 - WRONG

    @Test
    @Ignore
    public void perft7Test() {
        long calculations = Perft.perftDetailed(board, 7);
        assertTrue(calculations == 3195901860l);
//        assertTrue(calculations == 3195903162l);
    }


    @Test
    public void perft3Pos2Test() {
        board = new Board0x88("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
        long calculations = Perft.perftDetailed(board, 3);
        assertTrue("captures", Perft.capturesCounter == 17102);
        assertTrue("EPs", Perft.epCounter == 45);
        assertTrue("check", Perft.checkCounter == 993);
        assertTrue("castling", Perft.castlingCounter == 3162);
        assertTrue("promo", Perft.promotionsCounter == 0);
        assertTrue(calculations == 97862);
    }

    //TODO fix movegen
    @Test
    public void perft4Pos2Test() {
        board = new Board0x88("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
        long calculations = Perft.perftDetailed(board, 4);
        assertTrue("EPs", Perft.epCounter == 1929);
        assertTrue("captures", Perft.capturesCounter == 757163);
        assertTrue("promos", Perft.promotionsCounter == 15172);
        assertTrue("nodes" ,Perft.nodesCounter == 4085603);
    }


}
