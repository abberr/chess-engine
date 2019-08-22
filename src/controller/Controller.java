package controller;

import game.Board;
import game.Evaluator;
import game.Move;
import game.Position;
import piece.Piece;
import piece.Player;

import java.util.List;

public class Controller {

	private Board board;

	public Controller() {
        board = new Board();
        System.out.println(board.getValue());
        Evaluator.minMax(board, Player.BLACK, 3);

	}

	public Board getBoard() {
	    return board;
    }


	public void executeMove(Piece piece, Position moveTo) {
        board.executeMove(piece, moveTo);
        if (piece.player == Player.BLACK)
            Evaluator.minMax(board, Player.WHITE, 3);
        else
            Evaluator.minMax(board, Player.BLACK, 3);
	}
	
	public List<Move> getAvailableMoves(Player player, Board board) {
		return board.getAvailableMoves(player, false);
	}

	public Piece getPieceAt(Position pos) {
		return board.getPieceAt(pos);
	}

	public List<Move> getMoves(Piece piece) {
		return board.getMoves(piece, false);
	}



}
