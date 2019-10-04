package game;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class HashTest {

    private Board board;

    @Before
    public void before() {
        board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
    }

    @Test
    public void hashShouldChangeOnMove() {

        long hash = board.getHash();
        board.executeMove("e2e4");
        long hash2 = board.getHash();
        board.executeMove("e7e5");
        long hash3 = board.getHash();

        assertTrue(hash != hash2);
        assertTrue(hash2 != hash3);
        assertTrue(hash != hash3);
    }

    @Test
    public void hashShouldBeSameOnSamePosition() {
        long hash = board.getHash();
        board.executeMove("b1c3");
        board.executeMove("b8c6");

        board.executeMove("c3b1");
        board.executeMove("c6b8");
        long hash2 = board.getHash();

        assertTrue(hash == hash2);
    }

    @Test
    public void hashShouldTakeTurnIntoAccount() {
        long hash = board.getHash();
        board.executeMove("b1c3");
        board.executeMove("c3b1");
        long hash2 = board.getHash();

        assertTrue(hash != hash2);
    }

    @Test
    public void hashShouldBeSameAfterRevert() {

        long hash = board.getHash();
        board.executeMove("e2e4");
        board.revertLastMove();
        long hash2 = board.getHash();

        assertTrue(hash == hash2);
    }

    @Test
    public void hashShouldBeSameAfterRevertedCapture() {

        board.executeMove("e2e4");
        board.executeMove("d7d5");
        long hash = board.getHash();
        board.executeMove("e4d5");
        board.revertLastMove();
        long hash2 = board.getHash();

        assertTrue(hash == hash2);
    }

    @Test
    public void hashShouldBeSameAfterRevertCastle() {
        board = new Board("rn1qkbnr/pppppppp/8/8/3PPPP1/N2Q1b1N/PPPB2BP/R3K2R w qkQK -");

        long hash = board.getHash();
        board.executeMove("e1g1");
        board.revertLastMove();
        long hash2 = board.getHash();

        assertTrue(hash == hash2);
    }

    @Test
    public void hashShouldBeSameAfterRevertEnPassant() {
        board = new Board("rnbqkbnr/1ppppppp/p7/4P3/8/8/PPPP1PPP/RNBQKBNR b qkQK -");

        board.executeMove("d7d5");
        long hash = board.getHash();
        board.executeMove("e5d6");
        board.revertLastMove();
        long hash2 = board.getHash();

        assertTrue(hash == hash2);
    }

    @Test
    public void hashShouldTakeInAccountCastling() {
        board = new Board("rn1qkbnr/pppppppp/8/8/3PPPP1/N2Q1b1N/PPPB2BP/R3K2R w qkQK -");

        long hash = board.getHash();
        board.executeMove("e1g1");
        board.executeMove("d8c8");
        board.executeMove("g1f2");
        board.executeMove("c8d8");
        board.executeMove("f2e1");
        board.executeMove("d8c8");
        board.executeMove("f1h1");
        board.executeMove("c8d8");

        long hash2 = board.getHash();

        assertTrue(hash != hash2);
    }

    @Test
    public void hashShouldTakeInAccountCastlingFen() {
        String [] castlingRights = {"q", "qk", "qQ", "qK", "qkQ","qkK","qQK", "qkQK", "k", "kK", "kQ", "kQK", "Q", "QK", "K"};
        long [] hashes = new long[castlingRights.length];

        for (int i = 0; i < castlingRights.length; i++) {
            String castlingRight = castlingRights[i];
            board = new Board(("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w " + castlingRight + " -"));
            hashes[i] = board.getHash();
        }

        assertFalse(containsDuplicats(hashes));
    }


    @Test
    public void hashShouldTakeInAccountEnPassant() {

        board.executeMove("e2e4");
        board.executeMove("e7e5");
        long hash = board.getHash();

        board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
        board.executeMove("e2e3");
        board.executeMove("e7e6");
        board.executeMove("e3e4");
        board.executeMove("e6e5");
        long hash2 = board.getHash();

        assertTrue(hash != hash2);
    }

    @Test
    public void hashShouldTakeInAccountEnPassantRevert() {

        board.executeMove("e2e4");
        long hash = board.getHash();
        board.executeMove("e7e5");
        board.revertLastMove();
        long hash2 = board.getHash();

        assertTrue(hash == hash2);
    }

    @Test
    public void hashShouldNotChangeOnEvaluating() {
        long hash = board.getHash();
        Evaluator.findBestMove(board);

        assertTrue("Hash hasnt changed after finding best move", hash == board.getHash());
    }

    private boolean containsDuplicats(long [] array) {
        boolean duplicates=false;
        for (int j=0;j<array.length;j++)
            for (int k=j+1;k<array.length;k++)
                if (k!=j && array[k] == array[j])
                    duplicates=true;

        return duplicates;
    }
}
