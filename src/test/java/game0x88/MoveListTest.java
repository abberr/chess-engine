package game0x88;

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

        moves.prepare(new Board0x88("k6K/4P3/8/8/8/1p1q4/2Q1P3/8 w - - "));
        for (Move move : moves) {
            assertTrue("move not null", move != null);
        }

        assertTrue(moves.size() == 0);
        assertTrue(moves.isEmpty());
    }

    @Test
    public void moveOrderingTestWhite() {
        Board0x88 board = new Board0x88("k6K/4P3/8/8/8/1p1q4/2Q1P3/8 w - - ");

        MoveList moves = board.getAvailableMoves(false);
        moves.prepare(board);

        String [] expectedMoves = {"e7e8q" , "e7e8r", "e7e8b", "e7e8n", "e2xd3", "c2xd3", "c2xb3"};


        for (String expectedMove : expectedMoves) {
            System.out.println(expectedMove);
            assertTrue(moves.getNextMove().toString().equals(expectedMove));
        }

        board.printBoard();
    }

    @Test
    public void moveOrderingTestBlack() {
        Board0x88 board = new Board0x88("8/1p1q4/2Q1P3/8/8/8/4p3/k6K b - - ");
        board.printBoard();
        MoveList moves = board.getAvailableMoves(false);
        moves.prepare(board);

        String [] expectedMoves = {"e2e1q" , "e2e1r", "e2e1b", "e2e1n", "b7xc6", "d7xc6", "d7xe6"};

        for (String expectedMove : expectedMoves) {
            System.out.println(expectedMove);
            assertTrue(moves.getNextMove().toString().equals(expectedMove));
        }


    }
}
