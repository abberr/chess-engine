package view;

import controller.Controller;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class UciTest {

    UciView uciView;

    @Before
    public void before() {
        Controller contr = new Controller();
        uciView = new UciView(contr);
        uciView.startGame();
    }

    @Test
    public void test1() {

    }
}
