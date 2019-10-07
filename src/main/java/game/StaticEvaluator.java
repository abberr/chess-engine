package game;

import static game.MoveGenerator.QUEEN_DIRECTIONS;
import static game.Pieces.*;
import static game.Pieces.BLACK_KING_VALUE_TABLE;

public class StaticEvaluator {

    private static final int ENDAGE_VALUE_THRESHOLD_WHITE = 21300;
    private static final int ENDAGE_VALUE_THRESHOLD_BLACK = -ENDAGE_VALUE_THRESHOLD_WHITE;

    private static final int PENALTY_DOUBLED_PAWNS = 20;
    private static final int BONUS_KING_SECURITY = 10;

    public static int getValue(byte[] squares) {

        int value = 0;
        int blackMaterial = 0, whiteMaterial = 0;
        int wkIndex = 0, bkIndex = 0;
        int pieceSquareTableSumBlack = 0, pieceSquareTableSumWhite = 0;
        int wPenalty = 0, bPenalty = 0;
        int wKingSecurity = 0, bKingSecurity = 0;
        int[] wPawnRanks = new int[8];
        int[] bPawnRanks = new int[8];
        int[] wPawnFiles = new int[8];
        int[] bPawnFiles = new int[8];

        for (int i = 0; i < 120; i++) {
            if ((i & 0x88) != 0) continue;
            byte piece = squares[i];
            if (piece != EMPTY_SQUARE) {
                if (piece == WHITE_PAWN) {
                    whiteMaterial += PIECE_VALUES[piece];
                    pieceSquareTableSumWhite += WHITE_PAWN_VALUE_TABLE[i];
                    wPawnRanks[i >> 4]++;
                    wPawnFiles[i & 7]++;
                } else if (piece == BLACK_PAWN) {
                    blackMaterial += PIECE_VALUES[piece];
                    pieceSquareTableSumBlack -= BLACK_PAWN_VALUE_TABLE[i];
                    bPawnRanks[i >> 4]++;
                    bPawnFiles[i & 7]++;
                } else if (piece == WHITE_KNIGHT) {
                    whiteMaterial += PIECE_VALUES[piece];
                    pieceSquareTableSumWhite += WHITE_KNIGHT_VALUE_TABLE[i];
                } else if (piece == BLACK_KNIGHT) {
                    blackMaterial += PIECE_VALUES[piece];
                    pieceSquareTableSumBlack -= BLACK_KNIGHT_VALUE_TABLE[i];
                } else if (piece == WHITE_BISHOP) {
                    whiteMaterial += PIECE_VALUES[piece];
                    pieceSquareTableSumWhite += WHITE_BISHOP_VALUE_TABLE[i];
                } else if (piece == BLACK_BISHOP) {
                    blackMaterial += PIECE_VALUES[piece];
                    pieceSquareTableSumBlack -= BLACK_BISHOP_VALUE_TABLE[i];
                } else if (piece == WHITE_ROOK) {
                    whiteMaterial += PIECE_VALUES[piece];
                    pieceSquareTableSumWhite += WHITE_ROOK_VALUE_TABLE[i];
                } else if (piece == BLACK_ROOK) {
                    blackMaterial += PIECE_VALUES[piece];
                    pieceSquareTableSumBlack -= BLACK_ROOK_VALUE_TABLE[i];
                } else if (piece == WHITE_QUEEN) {
                    whiteMaterial += PIECE_VALUES[piece];
                    pieceSquareTableSumWhite += WHITE_QUEEN_VALUE_TABLE[i];
                } else if (piece == BLACK_QUEEN) {
                    blackMaterial += PIECE_VALUES[piece];
                    pieceSquareTableSumBlack -= BLACK_QUEEN_VALUE_TABLE[i];
                } else if (piece == WHITE_KING) {
                    whiteMaterial += PIECE_VALUES[piece];
                    wkIndex = i;
                } else if (piece == BLACK_KING) {
                    blackMaterial += PIECE_VALUES[piece];
                    bkIndex = i;
                }
            }
        }

        if (whiteMaterial < ENDAGE_VALUE_THRESHOLD_WHITE) {
            pieceSquareTableSumWhite += WHITE_KING_VALUE_TABLE_LATE[wkIndex];
//            pieceSquareTableSumWhite += WHITE_KING_VALUE_TABLE[wkIndex];
        } else {
            wKingSecurity = kingSecurity(Player.WHITE, squares, wkIndex);
            pieceSquareTableSumWhite += WHITE_KING_VALUE_TABLE[wkIndex];
        }
        if (blackMaterial > ENDAGE_VALUE_THRESHOLD_BLACK) {
            pieceSquareTableSumBlack -= BLACK_KING_VALUE_TABLE_LATE[bkIndex];
//            pieceSquareTableSumBlack += BLACK_KING_VALUE_TABLE[bkIndex];
        } else {
            bKingSecurity = -kingSecurity(Player.BLACK, squares, bkIndex);
            pieceSquareTableSumBlack -= BLACK_KING_VALUE_TABLE[bkIndex];
        }

        //Doubled pawns penalty
        for (int i = 0; i < 8; i++) {
            if (wPawnFiles[i] > 1) {
                wPenalty -= PENALTY_DOUBLED_PAWNS;
            }
            if (bPawnFiles[i] > 1) {
                bPenalty += PENALTY_DOUBLED_PAWNS;
            }
        }


        return whiteMaterial + pieceSquareTableSumWhite + blackMaterial + pieceSquareTableSumBlack + wPenalty + bPenalty + wKingSecurity + bKingSecurity;
    }

    //Check adjacent squares for same colored pawns
    private static int kingSecurity(Player player, byte[] squares, int kingPos) {
        byte pawnOfSameColor = player == Player.WHITE ? WHITE_PAWN : BLACK_PAWN;

        int kingSecurity = 0;

        for (byte direction : QUEEN_DIRECTIONS) {
            if (((kingPos + direction)& 0x88) == 0 && squares[kingPos + direction] == pawnOfSameColor) {
                kingSecurity += BONUS_KING_SECURITY;
            }
        }

        return kingSecurity;
    }
}
