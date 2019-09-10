package main.game0x88;

import main.util.Util;

public class Move {

    private byte piece;
    private int moveFrom, moveTo;

    private byte capturedPiece;
    private byte promotingPiece;

    private boolean kingSideCastle;

    private boolean queenSideCastle;

    public Move(byte piece, int moveFrom, int moveTo) {
        this.piece = piece;
        this.moveFrom = moveFrom;
        this.moveTo = moveTo;
    }

    public byte getPiece() {
        return piece;
    }

    public int getMoveFrom() {
        return moveFrom;
    }

    public int getMoveTo() {
        return moveTo;
    }

    public byte getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(byte capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public void setPromotingPiece(byte promotingPiece) {
        this.promotingPiece = promotingPiece;
    }

    public void setKingSideCastle(boolean kingSideCastle) {
        this.kingSideCastle = kingSideCastle;
    }

    public void setQueenSideCastle(boolean queenSideCastle) {
        this.queenSideCastle = queenSideCastle;
    }

    public byte getPromotingPiece() {
        return promotingPiece;
    }

    public boolean isKingSideCastle() {
        return kingSideCastle;
    }

    public boolean isQueenSideCastle() {
        return queenSideCastle;
    }

    @Override
    public String toString() {
        return Util.indexToAlgebraicNotation(moveFrom) + "->" + Util.indexToAlgebraicNotation(moveTo);
    }
}
