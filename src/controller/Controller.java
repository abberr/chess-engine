package controller;

import game.Board;
import game.Evaluator;
import game.Move;
import game.Position;
import piece.Piece;
import game.Player;

import java.util.List;

public class Controller {

	private Board board;

	public Controller() {
        board = new Board();
        System.out.println(board.getValue());
        Evaluator.minMax(board, Player.WHITE, 1);

//		System.out.println(Evaluator.perft(board, 5, Player.WHITE));
	}

	public Board getBoard() {
	    return board;
    }


	public void executeMove(Piece piece, Position moveTo) {
        board.executeMove(piece, moveTo);

//		Move[] bestMoves = Evaluator.minMax(board, piece.player.getOpponent(), 5);
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
