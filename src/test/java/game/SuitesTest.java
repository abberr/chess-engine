package game;

import org.junit.Ignore;
import org.junit.Test;
import util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.TestCase.assertTrue;

public class SuitesTest {

//    private static final long MAX_TIME = 20000;
    private static final long MAX_TIME = 1000;

    private static int counter = 0;

    //251 passed, 1 sec limit
    //390(old) passed, 20 sec limit
    @Ignore
    @Test
    public void ECM() throws IOException {
        Files.readAllLines(Path.of("src/test/resources/ECM.EPD")).stream().forEach(this::testPos);

        System.out.println("TESTS PASSED: " + counter);
        System.out.println();
    }


    private void testPos(String line) {
        Board board = new Board(line.split(" bm ")[0]);
        String expectedBestMove = line.split(" ")[5].replace(";", "");
        String id = line.split("\"")[1];

        Evaluator.reset();
        Move bestMove = Evaluator.findBestMove(board,MAX_TIME);

        System.out.println("Found " + bestMove + ", expected " + expectedBestMove);
        if (Util.isSameMove(expectedBestMove, bestMove)) {
            System.out.println("correct");
            counter++;
        }

    }



}
