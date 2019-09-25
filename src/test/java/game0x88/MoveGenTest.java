package game0x88;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static game0x88.Pieces.EMPTY_SQUARE;
import static org.junit.Assert.assertTrue;

public class MoveGenTest {

    private Board0x88 board;

    @Before
    public void before() {
        board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
    }

    //Should not capture the bishop because white will lose queen next move.
    @Test
    public void quiscentMoveGenTest() {
        board = new Board0x88("4k3/8/4r3/4b3/4Q2p/8/8/4K3 w - -");
        MoveGenerator.setSearchModeQuiescence();
        MoveList moves = board.getAvailableMoves(false);
        MoveGenerator.setSearchModeNormal();

//        board.printBoard();

        assertTrue(moves.size() == 2);
    }

    @Test
    public void promotingMoveWhiteTest() {
        board = new Board0x88("k7/4P3/8/8/8/8/8/K7 w - -");
        MoveList moves = board.getAvailableMoves(false);

//        moves.forEach(System.out::println);

        assertTrue("All promoting moves generated", moves.size() == 7);
    }

    @Test
    public void promotingMoveBlackTest() {
        board = new Board0x88("k7/4P3/8/8/8/8/4p3/K7 b - -");
        MoveList moves = board.getAvailableMoves(false);

//        moves.forEach(System.out::println);

        assertTrue("All promoting moves generated", moves.size() == 7);
    }

    @Test
    public void promotingWithCatptureTest() {
        board = new Board0x88("k7/4P3/8/8/8/8/4p3/K2B4 b - -");
        MoveList moves = board.getAvailableMoves(false);
        board.printBoard();

//        List<Move> promoAndCaptures = moves.stream().filter(m -> m.toString().startsWith("e2xd1")).collect(Collectors.toList());

//        for (Move move : moves) {
        for (Move move : moves) {
            if (move.toString().startsWith("ex2d1")) {
                assertTrue("All moves generated", moves.size() == 11);
                assertTrue("Promoting moves with captures generated", move.getCapturedPiece() != EMPTY_SQUARE);
                return;
            }
        }

    }

}
