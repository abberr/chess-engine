package main.controller;

import main.game.Player;
import main.game0x88.Board0x88;
import main.game0x88.Evaluator;
import main.game0x88.Move;
import main.piece.Piece;

import java.util.LinkedList;
import java.util.List;

public class Controller {

	private Board0x88 board;

	private LinkedList<Move> moveList;


	public Controller() {
		moveList = new LinkedList<>();
        board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w ");
//        board = new Board("rn2kb1r/pppq1pp1/3p1n1p/1N2p3/4P3/3PBN2/PPP2PPP/R2QK2R w ");
//        board = new Board("rn2k2r/ppp2pp1/7p/3qp3/1b6/3PB3/PPQN1PPP/R4RK1 b");	//Slow evaluation (>50s depth 5)
//		board = new Board("rn1qkbnr/pppppppp/8/8/3PPPP1/N2Q1b1N/PPPB2BP/R3K2R w "); //Castling
//        board = new Board("rkb2Q2/pp3p1p/3p4/8/1P1P1R2/2q3P1/PR4BP/K7 b ");		//Repeating
//        board = new Board("rnbqkbnr/ppp1pppp/8/1B1p4/4P3/8/PPPP1PPP/RNBQK1NR b ");
//        board = new Board("4r3/p4p1Q/P2k4/2np4/1p6/1B3q1P/2P1r3/5K2 w");          //Mate
//        board = new Board("4r3/k1p2ppp/8/P7/6P1/3q4/1K6/8 b ");          //Mate in 2
//        board = new Board("8/k1p2ppp/8/P7/6P1/3q4/4r3/K7 b ");          //Mate in 1

//        System.out.println(board.getValue());
//        Move bestMove = Evaluator.findBestMove(board);

//		Evaluator.perft(board, 5, Player.WHITE);
//		System.out.println("Hash: " + board.getHash());

//		computerMove();
	}

	public Board0x88 getBoard() {
	    return board;
    }


    //TODO return false if not legal
	public boolean executeMove(String move) {
		return board.executeMove(move);
	}

	public void computerMove() {
        Move move = Evaluator.findBestMove(board);
        board.executeMove(move);
//		moveList.add(move);

//        board.printBoard();

//        computerMove();
    }

	public void revertLastMove() {
		board.revertLastMove();
	}
	
	public List<Move> getAvailableMoves(Board0x88 board) {
		return board.getAvailableMoves(false);
	}

	public List<Move> getMovesFromSquare(String square) {
		return board.getMovesOfPiece(square, false);
	}

//	public Piece getPieceAt(int index) {
//		return board.getPieceAt(pos);
//	}

//	public List<Move> getMoves(Piece piece) {
//		return board.getMoves(piece, false);
//	}

}
