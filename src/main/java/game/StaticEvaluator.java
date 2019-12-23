package game;

import static game.MoveGenerator.QUEEN_DIRECTIONS;
import static game.Pieces.*;
import static game.Pieces.BLACK_KING_VALUE_TABLE;

//https://github.com/vinc/purplehaze/blob/master/src/eval.cpp
//https://www.chessprogramming.org/Tapered_Eval
public class StaticEvaluator {

//    private static final int ENDAGE_VALUE_THRESHOLD_WHITE = 21300;
//    private static final int ENDAGE_VALUE_THRESHOLD_BLACK = -ENDAGE_VALUE_THRESHOLD_WHITE;

    private static final int PHASE_COEF[] = {0, 1, 2, 2, 4, 8, 0, 1, 2, 2, 4, 8, 0};
    private static final int PHASE_MAX =
                    PHASE_COEF[WHITE_PAWN] * 16 +
                    PHASE_COEF[WHITE_KNIGHT] * 4 +
                    PHASE_COEF[WHITE_BISHOP] * 4 +
                    PHASE_COEF[WHITE_ROOK] * 4 +
                    PHASE_COEF[WHITE_QUEEN] * 2;    //64

    private static final int[] PENALTY_DOUBLED_PAWNS = {0, 0, 20, 50, 100, 100, 100};
    private static final int BONUS_KING_SECURITY = 10;
    private static final int BONUS_ROOK_ON_HALF_OPEN_FILE = 15;
    private static final int BONUS_ROOK_ON_OPEN_FILE = 30;
    private static final int BISHOP_PAIR_BONUS = 30;

    public static int getValue(byte[] squares) {

        int blackMaterial = 0, whiteMaterial = 0;
        int wkIndex = 0, bkIndex = 0;
        int[] wrIndex = {-1, -1};
        int[] brIndex = {-1, -1};
        int positionalScoreBlackOpening = 0, positionalScoreWhiteOpening = 0;
        int positionalScoreBlackEnding = 0, positionalScoreWhiteEnding = 0;
        int[] wPawnRanks = new int[8];
        int[] bPawnRanks = new int[8];
        int[] wPawnFiles = new int[8];
        int[] bPawnFiles = new int[8];
        int wBishopCount = 0, bBishopCount = 0;
        int phase = 0;

        for (int i = 0; i < 120; i++) {
            if ((i & 0x88) != 0) continue;
            byte piece = squares[i];
            if (piece != EMPTY_SQUARE) {
                phase += PHASE_COEF[piece];

                if (Pieces.isWhite(piece)) {
                    whiteMaterial += PIECE_VALUES[piece];
                    positionalScoreWhiteOpening += PST_OPENING[piece][i];
                    positionalScoreWhiteEnding += PST_ENDING[piece][i];

                    if (piece == WHITE_PAWN) {
                        wPawnRanks[i >> 4]++;
                        wPawnFiles[i & 7]++;
                    }
                    else if (piece == WHITE_BISHOP) {
                        wBishopCount++;
                    }
                    else if (piece == WHITE_ROOK) {
                        if (wrIndex[0] == -1) {
                            wrIndex[0] = i;
                        } else {
                            wrIndex[1] = i;
                        }
                    } else if (piece == WHITE_KING) {
                        wkIndex = i;
                    }
                } else {
                    blackMaterial += PIECE_VALUES[piece];
                    positionalScoreBlackOpening += PST_OPENING[piece][i];
                    positionalScoreBlackEnding += PST_ENDING[piece][i];

                    if (piece == BLACK_PAWN) {
                        bPawnRanks[i >> 4]++;
                        bPawnFiles[i & 7]++;
                    } else if (piece == BLACK_BISHOP) {
                        bBishopCount++;
                    }
                    else if (piece == BLACK_ROOK) {
                        if (brIndex[0] == -1) {
                            brIndex[0] = i;
                        } else {
                            brIndex[1] = i;
                        }
                    } else if (piece == BLACK_KING) {
                        bkIndex = i;
                    }
                }

            }
        }
        //Bishop pair bonuis
        if (wBishopCount >= 2) {
            whiteMaterial += BISHOP_PAIR_BONUS;
        }
        if (bBishopCount >= 2) {
            blackMaterial += BISHOP_PAIR_BONUS;
        }

        //King security
        positionalScoreWhiteOpening += kingSecurity(Player.WHITE, squares, wkIndex);
        positionalScoreBlackOpening += kingSecurity(Player.BLACK, squares, bkIndex);

        //Doubled pawns penalty
        for (int i = 0; i < 8; i++) {
            positionalScoreWhiteOpening -= PENALTY_DOUBLED_PAWNS[wPawnFiles[i]];
            positionalScoreBlackOpening -= PENALTY_DOUBLED_PAWNS[bPawnFiles[i]];
        }

        //Rook on open files
        for (int i : wrIndex) {
            if (i != -1) {
                int pawnsOnFile = wPawnFiles[i & 7] + bPawnFiles[i & 7];
                if (pawnsOnFile == 0) {
                    positionalScoreWhiteOpening += BONUS_ROOK_ON_OPEN_FILE;
                } else if (pawnsOnFile == 1) {
                    positionalScoreWhiteOpening += BONUS_ROOK_ON_HALF_OPEN_FILE;
                }
            }
        }
        for (int i : brIndex) {
            if (i != -1) {
                int pawnsOnFile = wPawnFiles[i & 7] + bPawnFiles[i & 7];
                if (pawnsOnFile == 0) {
                    positionalScoreBlackOpening += BONUS_ROOK_ON_OPEN_FILE;
                } else if (pawnsOnFile == 1) {
                    positionalScoreBlackOpening += BONUS_ROOK_ON_HALF_OPEN_FILE;
                }
            }
        }

        final int opening = positionalScoreWhiteOpening - positionalScoreBlackOpening;
        final int ending = positionalScoreWhiteEnding - positionalScoreBlackEnding;

        phase = ((phase > PHASE_MAX) ? PHASE_MAX : ((phase < 0) ? 0 : phase));  //TODO: remove?
        int posScore = (opening * phase + ending * (PHASE_MAX - phase)) / PHASE_MAX;

        return whiteMaterial  - blackMaterial + posScore;
    }

    //Check adjacent squares for same colored pawns
    private static int kingSecurity(Player player, byte[] squares, int kingPos) {
        byte pawnOfSameColor = player == Player.WHITE ? WHITE_PAWN : BLACK_PAWN;

        int kingSecurity = 0;

        for (byte direction : QUEEN_DIRECTIONS) {
            if (((kingPos + direction) & 0x88) == 0 && squares[kingPos + direction] == pawnOfSameColor) {
                kingSecurity += BONUS_KING_SECURITY;
            }
        }

        return kingSecurity;
    }
}
