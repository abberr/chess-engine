import game0x88.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

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
        List<Move> moves = board.getAvailableMoves(false);
        MoveGenerator.setSearchModeNormal();

//        board.printBoard();

        assertTrue(moves.size() == 2);
    }

}
