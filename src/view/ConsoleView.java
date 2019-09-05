package view;

import controller.Controller;
import game.Move;
import game.Position;
import piece.Piece;

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
                int fromX = input.charAt(0) - 'a';
                int fromY = 8 - Integer.parseInt("" + input.charAt(1));
                Piece selectedPiece = contr.getPieceAt(new Position(fromX, fromY));
                if(selectedPiece != null) {
                    for(Move move : contr.getMoves(selectedPiece)) {
                        System.out.print(move + ",");
                    }
                    System.out.println();
                }
            }
            else if (input.length() == 1 && input.charAt(0) == 'r') {
                contr.revertLastMove();
            }

            else if (validInput(input)) {
                int fromX = input.charAt(0) - 'a';
                int fromY = 8 - Integer.parseInt("" + input.charAt(1));
                int destX = input.charAt(2) - 'a';
                int destY = 8 - Integer.parseInt("" + input.charAt(3));

                Piece piece = contr.getPieceAt(new Position(fromX, fromY));
                contr.executeMove(piece, new Position(destX, destY));
                contr.getBoard().printBoard();

                contr.computerMove();

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
