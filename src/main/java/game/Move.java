package game;

import util.Util;

import static game.Pieces.*;

//TODO make static instead
//http://mediocrechess.blogspot.com/2007/02/plan-changing-moves-and-general.html
public class Move {

    private byte piece;
    private int moveFrom, moveTo;
    private byte capturedPiece;
    private byte promotingPiece;
    private boolean kingSideCastle;
    private boolean queenSideCastle;
    private boolean pawnDoublePush;
    private boolean enPassant;

    public Move(byte piece, int moveFrom, int moveTo) {
        this.piece = piece;
        this.moveFrom = moveFrom;
        this.moveTo = moveTo;
    }

    //Used for the 50 move rule
    public boolean isReversibleMove() {
        return capturedPiece == EMPTY_SQUARE && piece != WHITE_PAWN && piece != BLACK_PAWN;
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

    public boolean isPawnDoublePush() {
        return pawnDoublePush;
    }

    public void setPawnDoublePush(boolean pawnDoublePush) {
        this.pawnDoublePush = pawnDoublePush;
    }

    public boolean isEnPassant() {
        return enPassant;
    }

    public void setEnPassant(boolean enPassant) {
        this.enPassant = enPassant;
    }

    @Override
    public String toString() {
        //UCI want regular notation
//        if (isKingSideCastle()) return "O-O";
//        if (isQueenSideCastle()) return "O-O-O";
        String moveString = Util.indexToAlgebraicNotation(moveFrom);
        moveString = this.capturedPiece == 0 ? moveString : moveString + "x";
        moveString += Util.indexToAlgebraicNotation(moveTo);
        if(promotingPiece != EMPTY_SQUARE) {
            moveString += Character.toLowerCase(Pieces.PIECE_CHAR[promotingPiece]);
        }
        return  moveString;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        Move otherMove = (Move) object;
        return this.moveFrom == otherMove.moveFrom && this.moveTo == otherMove.moveTo && this.piece == otherMove.piece;
    }
}
