package game;


import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class BoardTest {


    @Test
    public void fenShouldBeCorrect() {
        String [] moves = "e2e4 c7c5 g1f3 d7d6 d2d4 c5d4 f3d4 g8f6 b1c3 a7a6 c1e3 e7e5 d4b3 f6g4 e3c1 b8c6 f2f3 g4f6 c1e3 c8e6 d1e2 f8e7 e1c1 e6b3 a2b3 e8g8 g2g3 d8a5 c1b1 f8d8 e2f2 f6d7 c3d5 e7f8 d5b6 a8b8 b6d7 d8d7 e3b6 a5b4 b6c5 b4a5 f1h3 d7d8 c5b6 a5b4 b6d8 b8d8 f3f4 f8e7 f4e5 d6e5 d1d8 e7d8 h1d1 b4e4 f2g2 e4g2 h3g2 d8f6 d1d7 c6d8 g2b7 d8b7 d7b7 g8f8 b7b8 f8e7 c2c3 a6a5 b8a8 g7g6 b1c2 e5e4 a8a5 e7e6 b3b4 f6e5 b4b5 e5d6 b5b6 e6d7 b6b7 d6b8 a5c5 b8c7 b2b4 f7f5 b4b5 f5f4 b5b6 c7b8 c5c8 f4g3 h2g3 b8g3 c3c4 h7h6 c4c5 e4e3 c5c6 d7e7 c6c7 e3e2 b7b8q e2e1n c2b3".split(" ");
        Board board = new Board();

        Arrays.stream(moves).forEach(board::executeMove);
        board.printBoard();

        assertTrue(board.generateFen().contains("1QR5/2P1k3/1P4pp/8/8/1K4b1/8/4n3 b - -"));
//        assertTrue(board.generateFen().equals("1QR5/2P1k3/1P4pp/8/8/1K4b1/8/4n3 b - - 1 54"));
    }

    @Test
    public void makeMoveTest() {
        Board board = new Board();

        board.executeMove("e2e4");
    }
}
