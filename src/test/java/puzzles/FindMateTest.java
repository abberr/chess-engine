package puzzles;

import game.Board;
import game.Evaluator;
import game.Move;
import org.junit.Ignore;
import org.junit.Test;
import util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertTrue;

public class FindMateTest {

    private static final String PATH_MATE_IN_TWO = "src/test/resources/MateInTwo.txt";
    private static final String PATH_MATE_IN_THREE = "src/test/resources/MateInThree.txt";
    private static final String PATH_MATE_IN_FOUR = "src/test/resources/MateInFour.txt";

    @Test
    public void mateInTwo() throws IOException {
        processFile(PATH_MATE_IN_TWO);
    }

    @Test
    public void mateInThree() throws IOException {
        processFile(PATH_MATE_IN_THREE);
    }

    @Ignore
    @Test
    public void mateInFour() throws IOException {
        processFile(PATH_MATE_IN_FOUR);
    }

    private void processFile(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filePath));

        IntStream.range(0,lines.size())
                .filter(i -> (i + 2) % 5 == 0)
                .forEach(i -> testPuzzle(lines.get(i), lines.get(i + 1).split(" ")[1]));
    }

    private void testPuzzle(String fen, String solution) {
        System.out.println(fen);
        System.out.println("Solution:" + solution);
        Board board = new Board(fen);
        board.printBoard();
        Move bestMove = Evaluator.findBestMove(board, 10);

        assertTrue(Util.isSameMove(solution, bestMove));
        System.out.println();
    }

}
