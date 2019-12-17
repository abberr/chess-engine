package game;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class EndGameTest {


    //TODO disable null move in endgame
    @Ignore
    @Test
    public void pawnPromotionTest() {
        Board board = new Board("8/8/8/8/7k/8/P7/K7 w - -");
        board.printBoard();

        Move bestMove = Evaluator.findBestMove(board, 9);

        assertTrue(bestMove.toString().equals("a2a4"));
    }
}
