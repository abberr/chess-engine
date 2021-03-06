package game;

import util.Util;

public final class Pieces {
    public static final byte EMPTY_SQUARE = 0;

    static final byte PIECES_SIZE = 13;


    static final byte WHITE_PAWN = 1;
    static final byte WHITE_KNIGHT = 2;
    static final byte WHITE_BISHOP = 3;
    static final byte WHITE_ROOK = 4;
    static final byte WHITE_QUEEN = 5;
    static final byte WHITE_KING = 6;

    static final byte BLACK_PAWN = 7;
    static final byte BLACK_KNIGHT = 8;
    static final byte BLACK_BISHOP = 9;
    static final byte BLACK_ROOK = 10;
    static final byte BLACK_QUEEN = 11;
    static final byte BLACK_KING = 12;

    static final int[] PIECE_VALUES = {0, 100, 320, 330, 500, 900, 20000, 100, 320, 330, 500, 900, 20000};

    public static final char[] PIECE_CHAR = {' ', 'P', 'N', 'B', 'R', 'Q', 'K', 'p', 'n', 'b', 'r', 'q', 'k'};
    static final char[] PIECE_UNICODE = {' ', '♟', '♞', '♝', '♜', '♛', '♚', '♙', '♘', '♗', '♖', '♕', '♔'};


    static final int[] BLACK_PAWN_VALUE_TABLE =
                    {0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	
					50,	50,	50,	50,	50,	50,	50,	5,	0,	0,	0,	0,	0,	0,	0,	0,
					10,	10,	20,	30,	30,	20,	10,	10,	0,	0,	0,	0,	0,	0,	0,	0,
					5,	5,	10,	25,	25,	10,	5,	5,	0,	0,	0,	0,	0,	0,	0,	0,
					0,	0,	0,	20,	20,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,
					5,	-5,	-10,0,	0,	-10,-5,	5,	0,	0,	0,	0,	0,	0,	0,	0,
					5,	10,	10,	-20,-20,10,	10,	5,	0,	0,	0,	0,	0,	0,	0,	0,
					0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0};

    static final int[] BLACK_PAWN_VALUE_TABLE_LATE =
                    {0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,
                    100,100,100,100,100,100,100,100,0,	0,	0,	0,	0,	0,	0,	0,
                    20,	20,	40,	60,	60,	40,	20,	20,	0,	0,	0,	0,	0,	0,	0,	0,
                    10,	10,	20,	50,	50,	20,	10,	10,	0,	0,	0,	0,	0,	0,	0,	0,
                    5 ,  5, 10, 30, 30, 10, 5,   5,	0,	0,	0,	0,	0,	0,	0,	0,
                    5 ,	 5,	 5,-20,-20,	5,   5,	 5,	0,	0,	0,	0,	0,	0,	0,	0,
                    -10 ,-10,-10,-10,-10,-10,-10,-10,0,	0,	0,	0,	0,	0,	0,	0,
                    0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0};

    static final int[] BLACK_KNIGHT_VALUE_TABLE =
                    {-50,-40,-30,-30,-30,-30,-40,-50, 0,0,	0,	0,	0,	0,	0,	0,
                    -40,-20,  0,  0,  0,  0,-20,-40, 0,	0,	0,	0,	0,	0,	0,	0,
                    -30,  0, 10, 15, 15, 10,  0,-30, 0,	0,	0,	0,	0,	0,	0,	0,
                    -30,  5, 15, 20, 20, 15,  5,-30, 0,	0,	0,	0,	0,	0,	0,	0,
                    -30,  0, 15, 20, 20, 15,  0,-30, 0,	0,	0,	0,	0,	0,	0,	0,
                    -30,  5, 10, 15, 15, 10,  5,-30, 0,	0,	0,	0,	0,	0,	0,	0,
                    -40,-20,  0,  5,  5,  0,-20,-40, 0,	0,	0,	0,	0,	0,	0,	0,
                    -50,-40,-30,-30,-30,-30,-40,-50, 0,	0,	0,	0,	0,	0,	0,	0,};

    static final int[] BLACK_BISHOP_VALUE_TABLE =
                   {-20,-10,-10,-10,-10,-10,-10,-20, 0,0,	0,	0,	0,	0,	0,	0,
                    -10,  0,  0,  0,  0,  0,  0,-10, 0,	0,	0,	0,	0,	0,	0,	0,
                    -10,  0,  5, 10, 10,  5,  0,-10, 0,	0,	0,	0,	0,	0,	0,	0,
                    -10,  5,  5, 10, 10,  5,  5,-10, 0,	0,	0,	0,	0,	0,	0,	0,
                    -10,  0, 10, 10, 10, 10,  0,-10, 0,	0,	0,	0,	0,	0,	0,	0,
                    -10, 10, 10, 10, 10, 10, 10,-10, 0,	0,	0,	0,	0,	0,	0,	0,
                    -10,  5,  0,  0,  0,  0,  5,-10, 0,	0,	0,	0,	0,	0,	0,	0,
                    -20,-10,-10,-10,-10,-10,-10,-20, 0,	0,	0,	0,	0,	0,	0,	0,};

    static final int[] BLACK_ROOK_VALUE_TABLE =
                   {  0,  0,  0,  0,  0,  0,  0,  0, 0,0,	0,	0,	0,	0,	0,	0,
                      5, 10, 10, 10, 10, 10, 10,  5, 0,	0,	0,	0,	0,	0,	0,	0,
                     -5,  0,  0,  0,  0,  0,  0, -5, 0,	0,	0,	0,	0,	0,	0,	0,
                     -5,  0,  0,  0,  0,  0,  0, -5, 0,	0,	0,	0,	0,	0,	0,	0,
                     -5,  0,  0,  0,  0,  0,  0, -5, 0,	0,	0,	0,	0,	0,	0,	0,
                     -5,  0,  0,  0,  0,  0,  0, -5, 0,	0,	0,	0,	0,	0,	0,	0,
                     -5,  0,  0,  0,  0,  0,  0, -5, 0,	0,	0,	0,	0,	0,	0,	0,
                      0,  0,  0,  5,  5,  0,  0,  0, 0,	0,	0,	0,	0,	0,	0,	0,};


    static final int[] BLACK_QUEEN_VALUE_TABLE =
                   {-20,-10,-10, -5, -5,-10,-10,-20, 0,0,	0,	0,	0,	0,	0,	0,
                    -10,  0,  0,  0,  0,  0,  0,-10, 0,	0,	0,	0,	0,	0,	0,	0,
                    -10,  0,  5,  5,  5,  5,  0,-10, 0,	0,	0,	0,	0,	0,	0,	0,
                     -5,  0,  5,  5,  5,  5,  0, -5, 0,	0,	0,	0,	0,	0,	0,	0,
                      0,  0,  5,  5,  5,  5,  0, -5, 0,	0,	0,	0,	0,	0,	0,	0,
                    -10,  5,  5,  5,  5,  5,  0,-10, 0,	0,	0,	0,	0,	0,	0,	0,
                    -10,  0,  5,  0,  0,  0,  0,-10, 0,	0,	0,	0,	0,	0,	0,	0,
                    -20,-10,-10, -5, -5,-10,-10,-20, 0,	0,	0,	0,	0,	0,	0,	0,};

    static final int[] BLACK_KING_VALUE_TABLE =
                    {-30,-40,-40,-50,-50,-40,-40,-30, 0,0,	0,	0,	0,	0,	0,	0,
                    -30,-40,-40,-50,-50,-40,-40,-30, 0,	0,	0,	0,	0,	0,	0,	0,
                    -30,-40,-40,-50,-50,-40,-40,-30, 0,	0,	0,	0,	0,	0,	0,	0,
                    -30,-40,-40,-50,-50,-40,-40,-30, 0,	0,	0,	0,	0,	0,	0,	0,
                    -20,-30,-30,-40,-40,-30,-30,-20, 0,	0,	0,	0,	0,	0,	0,	0,
                    -10,-20,-20,-20,-20,-20,-20,-10, 0,	0,	0,	0,	0,	0,	0,	0,
                    20, 20,  0,  0,  0,  0, 20, 20, 0,	0,	0,	0,	0,	0,	0,	0,
                    20, 30, 10,  0,  0, 10, 30, 20, 0,	0,	0,	0,	0,	0,	0,	0,};

    static final int[] BLACK_KING_VALUE_TABLE_LATE =
                    {-50,-40,-30,-20,-20,-30,-40,-50,  0,	0,	0,	0,	0,	0,	0,	0,
                    -30,-20,-10,  0,  0,-10,-20,-30,  0,	0,	0,	0,	0,	0,	0,	0,
                    -30,-10, 20, 30, 30, 20,-10,-30,  0,	0,	0,	0,	0,	0,	0,	0,
                    -30,-10, 30, 40, 40, 30,-10,-30,  0,	0,	0,	0,	0,	0,	0,	0,
                    -30,-10, 30, 40, 40, 30,-10,-30,  0, 	0,	0,	0,	0,	0,	0,	0,
                    -30,-10, 20, 30, 30, 20,-10,-30,  0,	0,	0,	0,	0,	0,	0,	0,
                    -30,-30,  0,  0,  0,  0,-30,-30,  0,	0,	0,	0,	0,	0,	0,	0,
                    -50,-30,-30,-30,-30,-30,-30,-50,  0,	0,	0,	0,	0,	0,	0,	0};

    static final int [] WHITE_PAWN_VALUE_TABLE;
    static final int [] WHITE_PAWN_VALUE_TABLE_LATE;
    static final int [] WHITE_KNIGHT_VALUE_TABLE;
    static final int [] WHITE_BISHOP_VALUE_TABLE;
    static final int [] WHITE_ROOK_VALUE_TABLE;
    static final int [] WHITE_QUEEN_VALUE_TABLE;
    static final int [] WHITE_KING_VALUE_TABLE;
    static final int [] WHITE_KING_VALUE_TABLE_LATE;



    static {
        WHITE_PAWN_VALUE_TABLE = Util.invertSquareValueTable(BLACK_PAWN_VALUE_TABLE);
        WHITE_PAWN_VALUE_TABLE_LATE = Util.invertSquareValueTable(BLACK_PAWN_VALUE_TABLE_LATE);
        WHITE_KNIGHT_VALUE_TABLE= Util.invertSquareValueTable(BLACK_KNIGHT_VALUE_TABLE);
        WHITE_BISHOP_VALUE_TABLE= Util.invertSquareValueTable(BLACK_BISHOP_VALUE_TABLE);
        WHITE_ROOK_VALUE_TABLE= Util.invertSquareValueTable(BLACK_ROOK_VALUE_TABLE);
        WHITE_QUEEN_VALUE_TABLE= Util.invertSquareValueTable(BLACK_QUEEN_VALUE_TABLE);
        WHITE_KING_VALUE_TABLE= Util.invertSquareValueTable(BLACK_KING_VALUE_TABLE);
        WHITE_KING_VALUE_TABLE_LATE= Util.invertSquareValueTable(BLACK_KING_VALUE_TABLE_LATE);


    }
    static final int [][] PST_OPENING = {null,
            WHITE_PAWN_VALUE_TABLE, WHITE_KNIGHT_VALUE_TABLE, WHITE_BISHOP_VALUE_TABLE, WHITE_ROOK_VALUE_TABLE, WHITE_QUEEN_VALUE_TABLE, WHITE_KING_VALUE_TABLE,
            BLACK_PAWN_VALUE_TABLE, BLACK_KNIGHT_VALUE_TABLE, BLACK_BISHOP_VALUE_TABLE, BLACK_ROOK_VALUE_TABLE, BLACK_QUEEN_VALUE_TABLE, BLACK_KING_VALUE_TABLE};

    static final int [][] PST_ENDING = {null,
            WHITE_PAWN_VALUE_TABLE_LATE, WHITE_KNIGHT_VALUE_TABLE, WHITE_BISHOP_VALUE_TABLE, WHITE_ROOK_VALUE_TABLE, WHITE_QUEEN_VALUE_TABLE, WHITE_KING_VALUE_TABLE_LATE,
            BLACK_PAWN_VALUE_TABLE_LATE, BLACK_KNIGHT_VALUE_TABLE, BLACK_BISHOP_VALUE_TABLE, BLACK_ROOK_VALUE_TABLE, BLACK_QUEEN_VALUE_TABLE, BLACK_KING_VALUE_TABLE_LATE};

    static boolean isWhite(byte piece) {
        return  (piece > 0 && piece <= 6);
    }
}
