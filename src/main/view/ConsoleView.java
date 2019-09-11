package main.view;

import main.controller.Controller;
import main.game.Move;
import main.game.Position;
import main.piece.Piece;

import java.util.Scanner;

public class ConsoleView {

    private Controller contr;

    public ConsoleView(Controller contr) {
        this.contr = contr;
    }

    public void startGame() {
        Scanner myObj = new Scanner(System.in);
        while (true) {
            contr.getBoard().printBoard();

            String input = myObj.nextLine();

            if (input.length() == 2) {
                contr.getMovesFromSquare(input).forEach(System.out::println);
            }
            else if (input.length() == 1 && input.charAt(0) == 'r') {
                contr.revertLastMove();
            }

            else if (validInput(input)) {
                if (contr.executeMove(input)) {
                    contr.getBoard().printBoard();
                    contr.computerMove();
                }else {
                    System.out.println("Invalid move");
                }

            } else {
                System.out.println("Invalid input");
                continue;
            }
        }
    }

    private boolean validInput(String input) {
        if (input.length() != 4) return false;
        if (input.charAt(0) < 'a' || input.charAt(0) > 'h') return false;
        if (input.charAt(1) < '1' || input.charAt(1) > '8') return false;
        if (input.charAt(2) < 'a' || input.charAt(2) > 'h') return false;
        if (input.charAt(3) < '1' || input.charAt(3) > '8') return false;

        return true;
    }
}
