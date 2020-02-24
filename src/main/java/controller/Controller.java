package controller;

import game.*;

import java.util.List;

public class Controller {

	private Board board;


	public Controller(String fen) {
		board = new Board(fen);
	}

	public Controller() {
        board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
//        board = new Board("3kqb1r/1pp3pp/3p1n2/4pp2/rnPP4/4P3/PB3PPP/KBR1Q1NR b - -");      //Best move a4xa2 (only finds with depth 7?), also 2x slower when using quiescence
//        board = new Board("rn2kb1r/pppq1pp1/3p1n1p/1N2p3/4P3/3PBN2/PPP2PPP/R2QK2R w qkQK -");
//		board = new Board("rn1qkbnr/pppppppp/8/8/3PPPP1/N2Q1b1N/PPPB2BP/R3K2R w "); //Castling
//        board = new Board("rkb2Q2/pp3p1p/3p4/8/1P1P1R2/2q3P1/PR4BP/K7 b ");		//Repeating
//        board = new Board("rnbqkbnr/ppp1pppp/8/1B1p4/4P3/8/PPPP1PPP/RNBQK1NR b ");
//        board = new Board("4r3/p4p1Q/P2k4/2np4/1p6/1B3q1P/2P1r3/5K2 w");          //Mate
//        board = new Board("4r3/k1p2ppp/8/P7/6P1/3q4/1K6/8 b - -");          //Mate in 2
//        board = new Board("r2qkb1r/pp2nppp/3p4/2pNN1B1/2BnP3/3P4/PPP2PPP/R2bK2R w qkQK -");          //cant find mate in 2
//        board = new Board("8/k1p2ppp/8/P7/6P1/3q4/4r3/K7 b ");          //Mate in 1

//        System.out.println(board.getValue());
//        Move bestMove = Evaluator.findBestMove(board);

//		Evaluator.perft(board, 6);
//		System.out.println("Hash: " + board.getHash());

//		computerMove();

	}

	public Board getBoard() {
	    return board;
    }


	public boolean executeMove(String move) {
		return board.executeMove(move);

	}

	public void computerMove() {
        Move move = Evaluator.findBestMove(board, 3000L);
        board.executeMove(move);
    }

    public Move findBestMove(long searchTime) {
		return Evaluator.findBestMove(board, searchTime);
	}

	public Move findBestMove(int depth) {
		return Evaluator.findBestMove(board, depth);
	}

	public void revertLastMove() {
		board.revertLastMove();
	}
	
	public MoveList getAvailableMoves(Board board) {
		return board.getAvailableMoves();
	}

	public List<Move> getMovesFromSquare(String square) {
		return board.getMovesOfPiece(square);
	}

	public Player getPlayerToMove() {
		return board.getPlayerToMove();
	}

	public void restart() {
		board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
	}

//	public Piece getPieceAt(int index) {
//		return board.getPieceAt(pos);
//	}

//	public List<Move> getMoves(Piece piece) {
//		return board.getMoves(piece, false);
//	}

}
