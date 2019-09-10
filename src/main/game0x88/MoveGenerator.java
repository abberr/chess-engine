package main.game0x88;

import main.game.Player;

import java.util.ArrayList;
import java.util.List;

import static main.game0x88.Pieces.*;
import static main.game0x88.Pieces.EMPY_SQUARE;

public class MoveGenerator {

    private static final byte NORTH = 0x10, NORTH_EAST = 0x11, EAST = 0x01, SOUTH_EAST = 0x01 - 0x10, SOUTH = -0x10, SOUTH_WEST = -0x11, WEST = -0x01, NORTH_WEST = 0x10 - 0x01;

    public static List<Move> generateMoves(byte[] squares, Player player) {
        List<Move> moves = new ArrayList<>();


        return moves;
    }

    public static List<Move> generateMovesOfPiece(byte[] squares, int index) {

        byte piece = squares[index];
        if (piece == WHITE_PAWN || piece == BLACK_PAWN) {
            return generatePawnMoves(squares, index, piece);
        } else if (piece == WHITE_ROOK || piece == BLACK_ROOK) {
            return generateRookMoves(squares, index, piece);
        }

        return null;
    }

    private static List<Move> generatePawnMoves(byte[] squares, int index, byte piece) {
        List<Move> moves = new ArrayList<>();

        int moveToIndex = piece == WHITE_PAWN ? index + NORTH : index + SOUTH;
        int capturingMoveIndex1 = piece == WHITE_PAWN ? index + NORTH_WEST : index + SOUTH_WEST;
        int capturingMoveIndex2 = piece == WHITE_PAWN ? index + NORTH_EAST : index + SOUTH_EAST;

        //Regular move
        if (squares[moveToIndex] == EMPY_SQUARE) {
            Move move = new Move(piece, index, moveToIndex);

            //Promoting move if landing on last rank
            if (piece == WHITE_PAWN && moveToIndex >> 4 == 7) {
                move.setPromotingPiece(WHITE_QUEEN);
            } else if (piece == BLACK_PAWN && moveToIndex >> 4 == 0) {
                move.setPromotingPiece(BLACK_QUEEN);
            }

            moves.add(move);
        }

        //Move 2 squares if starting pos and no piece blocking
        if (piece == WHITE_PAWN && (index >> 4) == 1 && squares[index + NORTH] == EMPY_SQUARE) {
            moves.add(new Move(piece, index, index + NORTH + NORTH));
        } else if (piece == BLACK_PAWN && (index >> 4) == 6 && squares[index + SOUTH] == EMPY_SQUARE) {
            moves.add(new Move(piece, index, index + SOUTH + SOUTH));
        }

        //Capturing moves
        byte capturedPiece1 = squares[capturingMoveIndex1];
        byte capturedPiece2 = squares[capturingMoveIndex2];
        if ((piece == WHITE_PAWN && capturedPiece1 >= 7) || (piece == BLACK_PAWN && capturedPiece1 <= 6 && capturedPiece1 > 0)) {
            Move move = new Move(piece, index, capturingMoveIndex1);
            move.setCapturedPiece(capturedPiece1);
            moves.add(move);
        }
        if ((piece == WHITE_PAWN && capturedPiece2 >= 7) || (piece == BLACK_PAWN && capturedPiece2 <= 6 && capturedPiece2 > 0)) {
            Move move = new Move(piece, index, capturingMoveIndex2);
            move.setCapturedPiece(capturedPiece2);
            moves.add(move);
        }

        return moves;
    }

    private static List<Move> generateRookMoves(byte[] squares, int index, byte piece) {
        List<Move> moves = new ArrayList<>();

        //Traverse every direction
        byte [] directions = {NORTH, EAST, SOUTH, WEST};
        for (byte direction : directions){
            int moveToindex = index + direction;
            while (!isOutOfBounds(moveToindex)) {
                Move move = new Move(piece, index, moveToindex);
                byte pieceOnSquare = squares[moveToindex];
                if (pieceOnSquare == EMPY_SQUARE) {
                    moves.add(move);
                }
                //If capture
                else if ((piece == WHITE_ROOK && pieceOnSquare >= 7) || (piece == BLACK_ROOK && pieceOnSquare >= 7)) {
                    move.setCapturedPiece(pieceOnSquare);
                    moves.add(move);
                    break;
                }
                //If blocked by piece of same color
                else {
                    break;
                }

                moveToindex+=direction;
            }
        }


        return moves;
    }

    private static boolean isOutOfBounds(int index) {
        return (index & 0x88) != 0;
    }
}
