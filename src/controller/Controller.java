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

	private Move lastMove;


	public Controller() {
        board = new Board();
        System.out.println(board.getValue());
        Evaluator.minMax(board, Player.WHITE, 5);

//		Evaluator.perft(board, 5, Player.WHITE);
		System.out.println("Hash: " + board.getHash());
	}

	public Board getBoard() {
	    return board;
    }


	public void executeMove(Piece piece, Position moveTo) {
		this.lastMove = new Move(piece, board.getPositionOfPiece(piece), moveTo);
		board.executeMove(lastMove);
		System.out.println("Hash: " + board.getHash());

//		Move[] bestMoves = Evaluator.minMax(board, piece.player.getOpponent(), 5);
	}

	public void revertLastMove() {
		board.executeInvertedMove(lastMove);
		System.out.println("Hash: " + board.getHash());
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
