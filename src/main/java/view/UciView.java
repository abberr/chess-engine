package view;

import controller.Controller;
import game.Evaluator;
import game.Move;
import game.Player;

import java.util.Arrays;
import java.util.Scanner;

//https://www.stmintz.com/ccc/index.php?id=141612
public class UciView {

    private Controller contr;

    public UciView(Controller contr) {
        this.contr = contr;
    }

    public void startGame() {
        System.out.println("Abberchess started");
        Scanner myObj = new Scanner(System.in);
        while (true) {
            String input = myObj.nextLine();

            if(input.equals("exit")) {
                break;
            }

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
                Evaluator.reset();
            }

            if (input.startsWith("position startpos moves")) {
                contr.restart();
                Arrays.stream(input.split(" ")).skip(3).forEach(contr::executeMove);
                contr.getBoard().printBoard();
            }

            //go wtime 299990 btime 295521 winc 0 binc 0 movestogo 49
            if (input.startsWith("go")) {

                long wtime = Long.parseLong(getParameterFromString(input, "wtime"));
                long btime = Long.parseLong(getParameterFromString(input, "btime"));
                long movesToGo = -1;
                String movesToGoString = getParameterFromString(input, "movestogo");
                if (movesToGoString != null) {
                    movesToGo = Long.parseLong(movesToGoString);
                }

                long timeRemaining = contr.getPlayerToMove() == Player.WHITE ? wtime : btime;

                long searchTime;

                if (movesToGo == -1) {
                    searchTime = timeRemaining/30;
                } else {
                    searchTime = timeRemaining/movesToGo;
                }

                System.out.println(String.format("searching for %s ms", searchTime));

                Move bestMove = contr.findBestMove(searchTime);
                if (bestMove == null) {
                    System.out.println("bestmove 0000");
                } else {
                    System.out.println("bestmove " + bestMove);
                }
            }
        }
    }


    private String getParameterFromString(String string, String parameterName) {
        String [] parts = string.split(" ");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(parameterName)) {
                return parts[i+1];
            }
        }

        return null;
    }

}
