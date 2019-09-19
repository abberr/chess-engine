package view;

import controller.Controller;
import game0x88.Evaluator;
import game0x88.Move;

import java.util.Arrays;
import java.util.Scanner;

//https://www.stmintz.com/ccc/index.php?id=141612
public class UciView {

    private Controller contr;

    public UciView(Controller contr) {
        this.contr = contr;
    }

    public void startGame() {
        Scanner myObj = new Scanner(System.in);
        while (true) {
            String input = myObj.nextLine();

            if (input.equals("uci")) {
                System.out.println("id name abberchess 1");
                System.out.println("id author Albin");

                //Options here like hash size & stuff

                System.out.println("uciok");
            }

            if (input.equals("isready")) {
                System.out.println("readyok");
            }

            if (input.equals("ucinewgame")) {
                contr.restart();
            }

            if (input.startsWith("position startpos moves")) {
                contr.restart();
                Arrays.stream(input.split(" ")).skip(3).forEach(contr::executeMove);
            }

            if (input.startsWith("go")) {
                Move bestMove = contr.findBestMove();
            }
        }
    }


}
