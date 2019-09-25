package controller;

import game0x88.*;

import java.util.LinkedList;
import java.util.List;

public class Controller {

	private Board0x88 board;

	private LinkedList<Move> moveList;

	public Controller(String fen) {
		moveList = new LinkedList<>();
		board = new Board0x88(fen);
	}

	public Controller() {
		moveList = new LinkedList<>();
        board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
//        board = new Board0x88("3kqb1r/1pp3pp/3p1n2/4pp2/rnPP4/4P3/PB3PPP/KBR1Q1NR b - -");      //Best move a4xa2 (only finds with depth 7?), also 2x slower when using quiescence
//        board = new Board0x88("rn2kb1r/pppq1pp1/3p1n1p/1N2p3/4P3/3PBN2/PPP2PPP/R2QK2R w qkQK -");
//		board = new Board0x88("rn1qkbnr/pppppppp/8/8/3PPPP1/N2Q1b1N/PPPB2BP/R3K2R w "); //Castling
//        board = new Board0x88("rkb2Q2/pp3p1p/3p4/8/1P1P1R2/2q3P1/PR4BP/K7 b ");		//Repeating
//        board = new Board0x88("rnbqkbnr/ppp1pppp/8/1B1p4/4P3/8/PPPP1PPP/RNBQK1NR b ");
//        board = new Board0x88("4r3/p4p1Q/P2k4/2np4/1p6/1B3q1P/2P1r3/5K2 w");          //Mate
//        board = new Board0x88("4r3/k1p2ppp/8/P7/6P1/3q4/1K6/8 b - -");          //Mate in 2
//        board = new Board0x88("r2qkb1r/pp2nppp/3p4/2pNN1B1/2BnP3/3P4/PPP2PPP/R2bK2R w qkQK -");          //cant find mate in 2
//        board = new Board0x88("8/k1p2ppp/8/P7/6P1/3q4/4r3/K7 b ");          //Mate in 1

//        System.out.println(board.getValue());
        Move bestMove = Evaluator.findBestMove(board);

//		Evaluator.perft(board, 6);
//		System.out.println("Hash: " + board.getHash());

//		computerMove();

	}

	public Board0x88 getBoard() {
	    return board;
    }


	public boolean executeMove(String move) {
		return board.executeMove(move);

	}

	public void computerMove() {
        Move move = Evaluator.findBestMove(board);
        board.executeMove(move);
//		moveList.add(move);

        board.printBoard();

//        computerMove();
    }

    public Move findBestMove() {
		return Evaluator.findBestMove(board);
	}

	public void revertLastMove() {
		board.revertLastMove();
	}
	
	public MoveList getAvailableMoves(Board0x88 board) {
		return board.getAvailableMoves(false);
	}

	public MoveList getMovesFromSquare(String square) {
		return board.getMovesOfPiece(square, false);
	}

	public void restart() {
		board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
	}

//	public Piece getPieceAt(int index) {
//		return board.getPieceAt(pos);
//	}

//	public List<Move> getMoves(Piece piece) {
//		return board.getMoves(piece, false);
//	}

}
