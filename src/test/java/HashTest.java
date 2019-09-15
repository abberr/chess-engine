import controller.Controller;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class HashTest {

    private Controller contr;

    @Before
    public void before() {
        contr = new Controller();
    }

    @Test
    public void hashShouldChangeOnMove() {

        long hash = contr.getBoard().getHash();
        contr.executeMove("e2e4");
        long hash2 = contr.getBoard().getHash();
        contr.executeMove("e7e5");
        long hash3 = contr.getBoard().getHash();

        assertTrue(hash != hash2);
        assertTrue(hash2 != hash3);
        assertTrue(hash != hash3);
    }

    @Test
    public void hashShouldBeSameOnSamePosition() {
        long hash = contr.getBoard().getHash();
        contr.executeMove("b1c3");
        contr.executeMove("b8c6");
        contr.executeMove("c3b1");
        contr.executeMove("c6b8");
        long hash2 = contr.getBoard().getHash();

        assertTrue(hash == hash2);
    }

    @Test
    public void hashShouldTakeTurnIntoAccount() {
        long hash = contr.getBoard().getHash();
        contr.executeMove("b1c3");
        contr.executeMove("c3b1");
        long hash2 = contr.getBoard().getHash();

        assertTrue(hash != hash2);
    }

    @Test
    public void hashShouldBeSameAfterRevert() {

        long hash = contr.getBoard().getHash();
        contr.executeMove("e2e4");
        contr.revertLastMove();
        long hash2 = contr.getBoard().getHash();

        assertTrue(hash == hash2);
    }

    @Test
    public void hashShouldBeSameAfterRevertedCapture() {

        contr.executeMove("e2e4");
        contr.executeMove("d7d5");
        long hash = contr.getBoard().getHash();
        contr.executeMove("e4d5");
        contr.revertLastMove();
        long hash2 = contr.getBoard().getHash();

        assertTrue(hash == hash2);
    }

    @Test
    public void hashShouldBeSameAfterRevertCastle() {
        contr = new Controller("rn1qkbnr/pppppppp/8/8/3PPPP1/N2Q1b1N/PPPB2BP/R3K2R w qkQK");

        long hash = contr.getBoard().getHash();
        contr.executeMove("e1g1");
        contr.revertLastMove();
        long hash2 = contr.getBoard().getHash();

        assertTrue(hash == hash2);
    }

    @Test
    public void hashShouldBeSameAfterRevertEnPassant() {
        contr = new Controller("rnbqkbnr/1ppppppp/p7/4P3/8/8/PPPP1PPP/RNBQKBNR b qkQK");

        contr.executeMove("d7d5");
        long hash = contr.getBoard().getHash();
        contr.executeMove("e5d6");
        contr.revertLastMove();
        long hash2 = contr.getBoard().getHash();

        assertTrue(hash == hash2);
    }

    @Test
    public void hashShouldTakeInAccountCastling() {
        Controller contr = new Controller("rn1qkbnr/pppppppp/8/8/3PPPP1/N2Q1b1N/PPPB2BP/R3K2R w qkQK");

        long hash = contr.getBoard().getHash();
        contr.executeMove("e1g1");
        contr.executeMove("d8c8");
        contr.executeMove("g1f2");
        contr.executeMove("c8d8");
        contr.executeMove("f2e1");
        contr.executeMove("d8c8");
        contr.executeMove("f1h1");
        contr.executeMove("c8d8");

        long hash2 = contr.getBoard().getHash();

        assertTrue(hash != hash2);
    }

//    @Test
//    public void hashShouldTakeInAccountEnPassant() {
//
//        contr.executeMove("e2e4");
//        long hash = contr.getBoard().getHash();
//
//        contr = new Controller();
//        contr.executeMove("e2e3");
//        contr.executeMove("e3e4");
//        long hash2 = contr.getBoard().getHash();
//
//        assertTrue(hash != hash2);
//    }
}
