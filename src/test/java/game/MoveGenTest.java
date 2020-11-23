package game;

import org.junit.Before;
import org.junit.Test;

import static game.Pieces.EMPTY_SQUARE;
import static org.junit.Assert.assertTrue;

public class MoveGenTest {

    private Board board;

    @Before
    public void before() {
        board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
    }

    @Test
    public void quiscentMoveGenTest() {
        board = new Board("4k3/8/4r3/4b3/4Q2p/8/8/4K3 w - -");
        MoveList moves = board.getAvailableMoves();
        int counter = 0;
        while (moves.getNextCapturingMove() != null) {
            counter++;
        }
        assertTrue(counter == 2);
    }

    @Test
    public void promotingMoveWhiteTest() {
        board = new Board("k7/4P3/8/8/8/8/8/K7 w - -");
        MoveList moves = board.getAvailableMoves();

//        moves.forEach(System.out::println);
        int numberOfMoves = getSizeOfMoves(moves);
        assertTrue("All promoting moves generated", numberOfMoves == 7);
    }

    @Test
    public void promotingMoveBlackTest() {
        board = new Board("k7/4P3/8/8/8/8/4p3/K7 b - -");
        MoveList moves = board.getAvailableMoves();

        int numberOfMoves = getSizeOfMoves(moves);
        assertTrue("All promoting moves generated", numberOfMoves == 7);
    }

    @Test
    public void promotingWithCatptureTest() {
        board = new Board("k7/4P3/8/8/8/8/4p3/K2B4 b - -");
        MoveList moves = board.getAvailableMoves();
        board.printBoard();

        int counter = 0;
        Move move = moves.getNextMove();
        while (move != null) {
            counter++;
            if (move.getCapturedPiece() != EMPTY_SQUARE) {
                assertTrue(move.getPromotingPiece() != EMPTY_SQUARE);
            }
            move = moves.getNextMove();
        }

        assertTrue("All moves generated", counter == 11);
    }

    private int getSizeOfMoves(MoveList moves) {
        Move move = moves.getNextMove();
        int counter = 0;
        while (move != null) {
            counter++;
            move = moves.getNextMove();
        }
        return counter;
    }

}
