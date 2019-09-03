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
//        board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w ");
//        board = new Board("rnbqkbnr/ppp1pppp/8/1B1p4/4P3/8/PPPP1PPP/RNBQK1NR b ");
//        board = new Board("4r3/p4p1Q/P2k4/2np4/1p6/1B3q1P/2P1r3/5K2 w");          //Mate
//        board = new Board("4r3/k1p2ppp/8/P7/6P1/3q4/1K6/8 b ");          //Mate in 2
        board = new Board("8/k1p2ppp/8/P7/6P1/3q4/4r3/K7 b ");          //Mate in 1

//        System.out.println(board.getValue());
        Move bestMove = Evaluator.minMax(board, 3);

//		Evaluator.perft(board, 5, Player.WHITE);
//		System.out.println("Hash: " + board.getHash());
	}

	public Board getBoard() {
	    return board;
    }


	public void executeMove(Piece piece, Position moveTo) {
		this.lastMove = new Move(piece, board.getPositionOfPiece(piece), moveTo);
		board.executeMove(lastMove);
		System.out.println("Hash: " + board.getHash());

        Move bestMove = Evaluator.minMax(board, 3);


//		executeMove(bestMove.getPiece(), bestMove.moveTo);
	}

	public void computerMove() {
        this.lastMove = Evaluator.minMax(board, 5);
        board.executeMove(lastMove);
        board.printBoard();
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
