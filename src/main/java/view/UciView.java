package view;

import controller.Controller;

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


//// GUI: let the engine know if starting a new game
//            ucinewgame
//
//// GUI: tell the engine the position to search
//            position startpos moves e2e4
//
//// GUI: tell the engine to start searching
////      in this case give it the timing information in milliseconds
//            go wtime 122000 btime 120000 winc 2000 binc 2000
        }
    }


}
