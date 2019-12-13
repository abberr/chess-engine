package game;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class TranspositionTableTest {

    @Test
    public void searchCached() {
        Board board = new Board("2r5/1p1nkppp/1p2p3/1B1p1b2/P2P4/bPB1P3/N4PPP/2R3K1 w - -");
        Move move1 = Evaluator.findBestMove(board, 1);
        Move move2 = Evaluator.findBestMove(board, 3);
        Move move3 = Evaluator.findBestMove(board, 1);

        assertTrue(!move1.equals(move2));
        assertTrue("Searching again at a lower depth should return same result as previous higher depth search", move2.equals(move3));
    }

    @Ignore
    @Test
    public void bugTest() {
        Board board = new Board("2r5/1p1nkppp/1p2p3/1B1p1b2/P2P4/bPB1P3/N4PPP/2R3K1 w - -");
        board.printBoard();
        Evaluator.findBestMove(board, 10000l);
        board.executeMove("c3b2");
        board.executeMove("c8a8");
        board.printBoard();
        Evaluator.findBestMove(board, 10000l);
        board.executeMove("a4a5");
        board.executeMove("a3b1");
        board.printBoard();
        Evaluator.findBestMove(board, 10000l);
    }
}
