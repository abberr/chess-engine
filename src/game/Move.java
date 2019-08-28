package game;

import piece.Piece;

public class Move {
	
	Piece piece;
	Piece capturedPiece;
	public Position moveFrom, moveTo;
	boolean promotingMove;

	boolean kingSideCastle;
	boolean queenSideCastle;
	
	public Move(Piece piece, Position moveFrom, Position moveTo) {
		this.piece = piece;
		this.moveFrom = moveFrom;
		this.moveTo = moveTo;
	}

	public Piece getCapturedPiece() {
		return capturedPiece;
	}

	public void setCapturedPiece(Piece capturedPiece) {
		this.capturedPiece = capturedPiece;
	}

	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}

	public Position getMoveTo() {
		return moveTo;
	}

	public void setMoveTo(Position moveTo) {
		this.moveTo = moveTo;
	}

	public boolean isKingSideCastle() {
		return kingSideCastle;
	}

	public void setKingSideCastle(boolean kingSideCastle) {
		this.kingSideCastle = kingSideCastle;
	}

	public boolean isQueenSideCastle() {
		return queenSideCastle;
	}

	public void setQueenSideCastle(boolean queenSideCastle) {
		this.queenSideCastle = queenSideCastle;
	}

	@Override
	public String toString() {
		return piece + " " + moveFrom +"->" + moveTo;
	}
	


}
