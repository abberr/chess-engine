import controller.Controller;
import game0x88.Evaluator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MoveGenTest {

    private Controller contr;

    @Before
    public void before() {
        contr = new Controller();
    }

    @Test
    public void perft5Test() {
        long calculations = Evaluator.perft(contr.getBoard(), 5);
        assertTrue(calculations == 4865609);
    }

    //Should take about 40s
    @Test
    public void perft6Test() {
        long calculations = Evaluator.perft(contr.getBoard(), 6);
        assertTrue(calculations == 119060324);
    }

    @Test
    public void perft7Test() {
        long calculations = Evaluator.perft(contr.getBoard(), 7);
        assertTrue(calculations == 3195901860l);
    }

}
