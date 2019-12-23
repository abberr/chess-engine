package view;

import controller.Controller;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

@Ignore
public class UciTest {

    UciView uciView;

    @Before
    public void before() {
        Controller contr = new Controller();
        uciView = new UciView(contr);
        Thread t = new Thread(uciView);
        t.start();
    }
}
