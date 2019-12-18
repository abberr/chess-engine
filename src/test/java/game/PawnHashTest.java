package game;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class PawnHashTest {

    private Board board;

    @Before
    public void before() {
        board = new Board();
    }

    @After
    public void after() {
        board.printBoard();
    }

    @Test
    public void hashShouldntChangeOnNonPawnMoves() {
        long hash = board.getPawnHash();
        board.executeMove("b1c3");
        board.executeMove("b8c6");
        board.executeMove("g1f3");
        board.executeMove("g8f6");

        assertTrue(hash == board.getPawnHash());
    }

    @Test
    public void hashShouldChangeOnPawnMoves() {
        long hash1 = board.getPawnHash();
        board.executeMove("e2e4");
        long hash2 = board.getPawnHash();
        board.executeMove("d7d5");
        long hash3 = board.getPawnHash();
        board.executeMove("b1c3");
        long hash4 = board.getPawnHash();

        assertTrue(hash1 != hash2);
        assertTrue(hash2 != hash3);
        assertTrue(hash3 == hash4);
    }

    @Test
    public void hashShouldChangeOnCapturedPawn() {
        board.executeMove("d2d4");
        board.executeMove("b8c6");
        board.executeMove("b1c3");
        long hash = board.getPawnHash();
        board.executeMove("c6d4");

        assertTrue(hash != board.getPawnHash());
    }

    @Test
    public void hashShouldChangeOnPxP() {
        board.executeMove("e2e4");
        board.executeMove("d7d5");
        long hash = board.getPawnHash();
        board.executeMove("e4d5");

        assertTrue(hash != board.getPawnHash());
    }

    @Test
    public void hashShouldChangeOnPromo() {
        board = new Board("8/P7/8/8/8/8/8/k1K5 w - -");
        long hash = board.getPawnHash();
        board.executeMove("a7a8q");

        assertTrue(hash != board.getPawnHash());
    }

    @Test
    public void correctEnPassantHash() {
            board.executeMove("e2e4");
            board.executeMove("b8a6");
            board.executeMove("e4e5");
            board.executeMove("d7d5");
            board.executeMove("e5d6");
            long hash = board.getPawnHash();
            board.printBoard();

            //Same position but without capturing e.p
            board = new Board();
            board.executeMove("e2e4");
            board.executeMove("d7d5");
            board.executeMove("e4d5");
            board.executeMove("b8a6");
            board.executeMove("d5d6");

            assertTrue(hash == board.getPawnHash());
    }
}
