package game;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MoveListTest {

    @Test
    public void foeachTest() {
        MoveList moves = new MoveList();
        moves.add(new Move((byte) 1, 1, 1), MoveType.QUIET);
        moves.add(new Move((byte) 1, 1, 1), MoveType.QUIET);
        moves.add(new Move((byte) 1, 1, 1), MoveType.QUIET);

        assertFalse(moves.isEmpty());

        moves.prepare(new Board("k6K/4P3/8/8/8/1p1q4/2Q1P3/8 w - - "));
        for (Move move : moves) {
            assertTrue("move not null", move != null);
        }

        assertTrue(moves.size() == 0);
        assertTrue(moves.isEmpty());
    }

    @Test
    public void moveOrderingTestWhite() {
        Board board = new Board("k6K/4P3/8/8/8/1p1q4/2Q1P3/8 w - - ");

        MoveList moves = board.getAvailableMoves(false);
        moves.prepare(board);

        String [] expectedMoves = {"e7e8q" , "e7e8r", "e7e8b", "e7e8n", "e2xd3", "c2xd3", "c2xb3"};

        assertMoves(expectedMoves, moves);
    }

    @Test
    public void moveOrderingTestBlack() {
        Board board = new Board("8/1p1q4/2Q1P3/8/8/8/4p3/k6K b - - ");
        board.printBoard();
        MoveList moves = board.getAvailableMoves(false);
        moves.prepare(board);

        String [] expectedMoves = {"e2e1q" , "e2e1r", "e2e1b", "e2e1n", "b7xc6", "d7xc6", "d7xe6"};

        assertMoves(expectedMoves, moves);
    }

    @Test
    public void mvvLvaTest() {
        Board board = new Board("7k/8/8/1pr3q1/1PP3Q/8/8/7K w - - ");
        board.printBoard();

        MoveGenerator.setSearchModeQuiescence();
        MoveList moves = board.getAvailableMoves(false);
        MoveGenerator.setSearchModeNormal();
        moves.prepare(board);

        String [] expectedMoves = {"g4xg5", "b4xc5", "c4xb5"};

        assertMoves(expectedMoves, moves);
    }


    private void assertMoves(String [] expectedMoves, MoveList moves) {
        for (String expectedMove : expectedMoves) {
            Move move = moves.getNextMove();
            System.out.println("Move: " + move + ", Expected: " + expectedMove);
            assertTrue(move.toString().equals(expectedMove));
        }
    }
}
