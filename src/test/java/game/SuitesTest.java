package game;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.TestCase.assertTrue;

public class SuitesTest {

    private static final long MAX_TIME = 20000;

    private static int counter = 0;

    //175 passed, 1 sec limit
    //343 passed, 20 sec limit
    @Ignore
    @Test
    public void ECM() throws IOException {
        Files.readAllLines(Path.of("src/test/resources/ECM.EPD")).stream().forEach(this::testPos);

        System.out.println("TESTS PASSED: " + counter);
    }


    private void testPos(String line) {
        Board board = new Board(line.split(" bm ")[0]);
        String expectedBestMove = line.split(" ")[5].replace(";", "");
        String id = line.split("\"")[1];

        Evaluator.reset();
        Move bestMove = Evaluator.findBestMove(board,MAX_TIME);

        System.out.println("Found " + bestMove + ", expected " + expectedBestMove);
        if (sameMove(expectedBestMove, bestMove)) {
            System.out.println("correct");
            counter++;
        }

    }

    private boolean sameMove(String moveInDescriptiveNotation, Move move) {

        moveInDescriptiveNotation = moveInDescriptiveNotation.replace("+", "");

        char firstChar = moveInDescriptiveNotation.charAt(0);
        if (Character.isUpperCase(firstChar)) {

            //Nfxe4
            String temp = moveInDescriptiveNotation.substring(0, moveInDescriptiveNotation.length()-2);
            temp = temp.replace("x","");
            temp = temp.substring(1);
            if (temp.length() == 1 && !temp.equals(move.toString().substring(0,1))) {
                return false;
            }

            if (Character.toUpperCase(Pieces.PIECE_CHAR[move.getPiece()]) != firstChar) {
                return false;
            }
        }

        if (!lastTwo(moveInDescriptiveNotation).equals(lastTwo(move.toString()))) {
            return false;
        }

        return true;
    }

    private String lastTwo(String string) {
        return string.substring(string.length() - 2);
    }

}
