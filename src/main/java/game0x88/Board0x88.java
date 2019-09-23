package game0x88;

import util.Util;

import java.util.List;
import java.util.Random;

import static game0x88.Pieces.*;

public class Board0x88 {

    private final static int BOARD_SIZE = 128;
    private final static int PIECES_SIZE = 12;
    private final static int COLORS_SIZE = 2;

    private final static int MAXIMUM_NUMBER_OF_MOVES = 4096;

    private final static int NO_EN_PASSANT_AVAILABLE = 15;

    private Player playerToMove;
    private byte[] squares = new byte[BOARD_SIZE];

    private long [][][] zobristTable = new long[BOARD_SIZE][COLORS_SIZE][PIECES_SIZE];
    private long [] zobristCastlingRights = new long[16];
    private long [] zobristEnPassant = new long[8];

    private int moveNumber = 0;
    private int[] castlingRightsHistory = new int[MAXIMUM_NUMBER_OF_MOVES];     //MASK: qkQK
    private int[] enPassantHistory = new int[MAXIMUM_NUMBER_OF_MOVES];


    Move lastMove;

    private long hash;

    public Board0x88(String fen) {

        String [] fields = fen.split(" ");

        initZobrist();

        int x = 0, y = 0;
        for(char c : fields[0].toCharArray()) {
            byte piece = EMPTY_SQUARE;
            if (c == 'P')  piece = WHITE_PAWN;
            else if (c == 'p') piece = BLACK_PAWN;
            else if (c == 'N') piece = WHITE_KNIGHT;
            else if (c == 'n') piece = BLACK_KNIGHT;
            else if (c == 'B') piece = WHITE_BISHOP;
            else if (c == 'b') piece = BLACK_BISHOP;
            else if (c == 'R') piece = WHITE_ROOK;
            else if (c == 'r') piece = BLACK_ROOK;
            else if (c == 'Q') piece = WHITE_QUEEN;
            else if (c == 'q') piece = BLACK_QUEEN;
            else if (c == 'K') piece = WHITE_KING;
            else if (c == 'k') piece = BLACK_KING;
            else if (c >= '0' && c <= '8') x += (c - '0') - 1;
            else if (c == '/') {
                y++;
                x = 0;
                continue;
            }
//            squares[y * 16 + x] = piece;
            squares[x + (7*16 - y*16)] = piece;

            x++;
        }

        playerToMove = Player.WHITE;
        if (fields[1].charAt(0) == 'b') {
            playerToMove = Player.BLACK;
        }

        castlingRightsHistory[moveNumber] = 0b0000;
        if (fields[2].charAt(0) != '-') {
            if (fields[2].contains("q")) castlingRightsHistory[moveNumber] |= 0b1000;
            if (fields[2].contains("k")) castlingRightsHistory[moveNumber] |= 0b0100;
            if (fields[2].contains("Q")) castlingRightsHistory[moveNumber] |= 0b0010;
            if (fields[2].contains("K")) castlingRightsHistory[moveNumber] |= 0b0001;
        }

        if (fields[3].charAt(0) != '-') {
            enPassantHistory[moveNumber] = (fields[3].charAt(0) - 'a');
        } else {
            enPassantHistory[moveNumber] = NO_EN_PASSANT_AVAILABLE;
        }

        hash = generateZobristHash();
    }

    public Player getPlayerToMove() {
        return playerToMove;
    }


    public long getHash() {
        return hash;
    }


    public boolean executeMove(String move){
        String moveFrom = move.substring(0,2);
        int moveToIndex = Util.algebraicNotationToIndex(move.substring(2,4));

        for(Move m : getMovesOfPiece(moveFrom, false)) {
            if (moveToIndex == m.getMoveTo()) {
                executeMove(m);

                Evaluator.findBestMove(this);

                return true;
            }
        }
        return false;
    }

    public void executeMove(Move move) {
        int oldCastlingRights = castlingRightsHistory[moveNumber];

        moveNumber++;

        squares[move.getMoveFrom()] = EMPTY_SQUARE;
        squares[move.getMoveTo()] = move.getPiece();

        int newCastlingRights = updateCastlingRights(move.getMoveFrom(), move.getMoveTo(), oldCastlingRights);

        if (move.getPromotingPiece() != EMPTY_SQUARE) {
            squares[move.getMoveTo()] = move.getPromotingPiece();
        }
        else if (move.isKingSideCastle()) {
            if (move.getPiece() == WHITE_KING) {
                squares[0x07] = EMPTY_SQUARE;
                squares[0x05] = WHITE_ROOK;
            } else {
                squares[0x77] = EMPTY_SQUARE;
                squares[0x75] = BLACK_ROOK;
            }
        } else if (move.isQueenSideCastle()) {
            if (move.getPiece() == WHITE_KING) {
                squares[0x00] = EMPTY_SQUARE;
                squares[0x03] = WHITE_ROOK;
            } else {
                squares[0x70] = EMPTY_SQUARE;
                squares[0x73] = BLACK_ROOK;
            }
        }

        if (move.isEnPassant()) {
            if (move.getPiece() == WHITE_PAWN) {
                squares[move.getMoveTo() + MoveGenerator.SOUTH] = EMPTY_SQUARE;
            } else {
                squares[move.getMoveTo() + MoveGenerator.NORTH] = EMPTY_SQUARE;
            }
        }

        enPassantHistory[moveNumber] = move.isPawnDoublePush() ? move.getMoveFrom()&0b0111 : NO_EN_PASSANT_AVAILABLE;

        castlingRightsHistory[moveNumber] = newCastlingRights;

        int oldAvailableEnPassant = enPassantHistory[moveNumber - 1];
        int newAvailableEnPassant = enPassantHistory[moveNumber];

        updateHash(move, oldCastlingRights, newCastlingRights, oldAvailableEnPassant, newAvailableEnPassant);


        playerToMove = playerToMove.getOpponent();
        lastMove = move;
    }

    private int updateCastlingRights(int moveFrom, int moveTo, int newCastlingRights) {
        //if move involved rook or king startPos; update castling rights
        //TODO make constants
        if (moveFrom == 0x00 || moveTo == 0x00) {
            newCastlingRights &= 0b1101;
        } if (moveFrom == 0x07 || moveTo == 0x07) {
            newCastlingRights &= 0b1110;
        } if (moveFrom == 0x70 || moveTo == 0x70) {
            newCastlingRights &= 0b0111;
        } if (moveFrom == 0x77 || moveTo == 0x77) {
            newCastlingRights &= 0b1011;
        } if (moveFrom == 0x04 || moveTo == 0x04) {
            newCastlingRights &= 0b1100;
        } if (moveFrom == 0x74 || moveTo == 0x74) {
            newCastlingRights &= 0b0011;
        }
        
        return newCastlingRights;
    }

    //TODO make sure it can only be called on last move
    public void executeInvertedMove(Move move) {

        squares[move.getMoveFrom()] = move.getPiece();
        if (move.isEnPassant()) {
            int capturedPieceDirection = move.getPiece() == WHITE_PAWN ? MoveGenerator.SOUTH : MoveGenerator.NORTH;
            squares[move.getMoveTo() + capturedPieceDirection] = move.getCapturedPiece();
            squares[move.getMoveTo()] = EMPTY_SQUARE;
        } else {
            squares[move.getMoveTo()] = move.getCapturedPiece();
        }


        //Revert castling
        //TODO simplify
        if (move.isKingSideCastle()) {
            if (move.getPiece() == WHITE_KING) {
                squares[0x07] = WHITE_ROOK;
                squares[0x05] = EMPTY_SQUARE;
            } else {
                squares[0x77] = BLACK_ROOK;
                squares[0x75] = EMPTY_SQUARE;
            }
        } else if (move.isQueenSideCastle()) {
            if (move.getPiece() == WHITE_KING) {
                squares[0x00] = WHITE_ROOK;
                squares[0x03] = EMPTY_SQUARE;
            } else {
                squares[0x70] = BLACK_ROOK;
                squares[0x73] = EMPTY_SQUARE;
            }
        }

        playerToMove = playerToMove.getOpponent();

        int oldCastlingRights = castlingRightsHistory[moveNumber - 1];
        int newCastlingRights = castlingRightsHistory[moveNumber];
        int oldAvailableEnPassant = enPassantHistory[moveNumber - 1];
        int newAvailableEnPassant = enPassantHistory[moveNumber];

        updateHash(move, oldCastlingRights, newCastlingRights, oldAvailableEnPassant, newAvailableEnPassant);   //Has to be after player change to work
        moveNumber--;
    }

    public void revertLastMove() {
        executeInvertedMove(lastMove);
    }
    
    

    public List<Move> getMovesOfPiece(String position, boolean includePseudoLegal) {
        int index = Util.algebraicNotationToIndex(position);

        if (squares[index] == EMPTY_SQUARE) {
            return null;
        }

        return MoveGenerator.generateMovesOfPiece(squares, index, castlingRightsHistory[moveNumber], enPassantHistory[moveNumber], includePseudoLegal);
    }

    public List<Move> getAvailableMoves(boolean includePseudoLegal) {
        return MoveGenerator.generateMoves(squares, playerToMove, castlingRightsHistory[moveNumber], enPassantHistory[moveNumber], includePseudoLegal);
    }

    public int getValue() {
        int value = 0;

        for (int i = 0; i < 120; i++) {
            if ((i&0x88) != 0) continue;
            byte piece = squares[i];
            value += PIECE_VALUES[piece];
            if (piece == WHITE_PAWN ) {
                value += WHITE_PAWN_VALUE_TABLE[i];
            } else if (piece == BLACK_PAWN) {
                value -= BLACK_PAWN_VALUE_TABLE[i];
            } else if (piece == WHITE_KNIGHT ) {
                value += WHITE_KNIGHT_VALUE_TABLE[i];
            } else if (piece == BLACK_KNIGHT) {
                value -= BLACK_KNIGHT_VALUE_TABLE[i];
            } else if (piece == WHITE_BISHOP ) {
                value += WHITE_BISHOP_VALUE_TABLE[i];
            } else if (piece == BLACK_BISHOP) {
                value -= BLACK_BISHOP_VALUE_TABLE[i];
            } else if (piece == WHITE_ROOK) {
                value += WHITE_ROOK_VALUE_TABLE[i];
            } else if (piece == BLACK_ROOK) {
                value -= BLACK_ROOK_VALUE_TABLE[i];
            } else if (piece == WHITE_QUEEN) {
                value += WHITE_QUEEN_VALUE_TABLE[i];
            } else if (piece == BLACK_QUEEN) {
                value -= BLACK_QUEEN_VALUE_TABLE[i];
            } else if (piece == WHITE_KING) {
                value += WHITE_KING_VALUE_TABLE[i];
            } else if (piece == BLACK_KING) {
                value -= BLACK_KING_VALUE_TABLE[i];
            }
        }

        return value;
    }

    private void updateHash(Move move, int oldCastlingRights, int newCastlingRights, int oldAvailableEnPassant, int newAvailableEnPassant) {
        int moveFromindex = move.getMoveFrom();
        int moveToindex = move.getMoveTo();

        hash ^= zobristTable[moveFromindex][playerToMove.getHashValue()][move.getPiece() - 1];             //Remove piece from origin
        //If promoting move
        if (move.getPromotingPiece() != EMPTY_SQUARE) {
            hash ^= zobristTable[moveToindex][playerToMove.getHashValue()][move.getPromotingPiece() - 1];  //Add promoting piece to new square
        }
        //If regular move
        else {
            hash ^= zobristTable[moveToindex][playerToMove.getHashValue()][move.getPiece() - 1];           //Add origin piece to new square
        }

        //If capturing move
        if (move.getCapturedPiece() != EMPTY_SQUARE) {
            hash ^= zobristTable[moveToindex][playerToMove.getHashValue()][move.getCapturedPiece() - 1];   //Remove captured piece from new square
        }

        //If castling move
        if (move.isKingSideCastle() || move.isQueenSideCastle()) {
            int rookFromX = move.isKingSideCastle() ? 7 : 0;
            int rookToX = move.isKingSideCastle() ? 5 : 3;
            int rookY = move.getMoveFrom() >> 4;

            int rookMoveFromindex = rookY*16 + rookFromX;
            int rookMoveToIndex = rookY*16 + rookToX;

            byte rook = playerToMove == Player.WHITE ? WHITE_ROOK : BLACK_ROOK;

            hash ^= zobristTable[rookMoveFromindex][playerToMove.getHashValue()][rook - 1];           //Remove rook from corner
            hash ^= zobristTable[rookMoveToIndex][playerToMove.getHashValue()][rook - 1];             //Add rook to new location
        }

        //Update castlingRights hash
        hash ^= zobristCastlingRights[newCastlingRights ^ oldCastlingRights];

        //Update en passant hash
        if (oldAvailableEnPassant != NO_EN_PASSANT_AVAILABLE) hash ^= zobristEnPassant[oldAvailableEnPassant];
        if (newAvailableEnPassant != NO_EN_PASSANT_AVAILABLE) hash ^= zobristEnPassant[newAvailableEnPassant];

    }


    private void initZobrist() {
        //Init zobrist
        Random rnd = new Random(1);
        for (int i = 0; i < zobristTable.length; i++) {
            for (int j = 0; j < zobristTable[0].length; j++) {
                for (int k = 0; k < zobristTable[0][0].length; k++) {
                    zobristTable[i][j][k] = rnd.nextLong();
                }
            }
        }

        for (int i = 0; i < zobristCastlingRights.length; i++) {
            zobristCastlingRights[i] = rnd.nextLong();
        }

        for (int i = 0; i < zobristEnPassant.length; i++) {
            zobristEnPassant[i] = rnd.nextLong();
        }
    }

    private long generateZobristHash() {
        //Pieces
        long hash = 0;
        for (int i = 0; i < 128; i++) {
                byte piece = squares[i];
                if (piece != 0) {
                    hash ^= zobristTable[i][playerToMove.getHashValue()][piece - 1];
                }
        }

        //Castling rights
        int castlingRights = castlingRightsHistory[moveNumber];
        hash ^= zobristCastlingRights[castlingRights];

        //Available en passant
        int enPassantSquare = enPassantHistory[moveNumber];
        if (enPassantSquare != NO_EN_PASSANT_AVAILABLE) {
            hash ^= zobristEnPassant[enPassantSquare];
        }

        return hash;
    }

    public boolean isInCheck() {
        return MoveGenerator.isInCheck(squares, playerToMove);
    }

    public byte[] getSquares() {
        return squares;
    }

    public void printBoard() {
        System.out.print("\n--------------------------");
        for (int i = 0; i < 8; i++) {
            System.out.println();
            for (int j = 0; j < 8; j++) {
                byte p = squares[j + (7*16 - i*16)];
                System.out.print("[");
                if (p == EMPTY_SQUARE)
                    System.out.print(" ");
                else {
                    System.out.print(PIECE_CHAR[p]);
                }
                System.out.print("]");
            }
        }
        System.out.println("\nhash: [" + hash + "]");
        System.out.println("fen: [" + generateFen() + "]");
        System.out.println("value: [" + getValue() + "]");
        System.out.println("InCheck: " + MoveGenerator.isInCheck(squares, playerToMove));

        System.out.println("--------------------------");
    }

    public String getCastlingRights() {
        String castlingRights = "";
        int currentCastlingRights = castlingRightsHistory[moveNumber];
        if (currentCastlingRights != 0b0000) {
            if ((currentCastlingRights&0b1000) != 0) castlingRights += "q";
            if ((currentCastlingRights&0b0100) != 0) castlingRights += "k";
            if ((currentCastlingRights&0b0010) != 0) castlingRights += "Q";
            if ((currentCastlingRights&0b0001) != 0) castlingRights += "K";
        } else {
            castlingRights += "-";
        }

        return castlingRights;
    }

    private String generateFen() {
        String fen = "";
        for (int i = 0; i < 8; i++) {
            int emptySpaces = 0;
            for (int j = 0; j < 8; j++) {
                int index = j + (7*16 - i*16);
                if (squares[index] == EMPTY_SQUARE) {
                    emptySpaces++;
                } else {
                    if(emptySpaces > 0) {
                        fen += emptySpaces;
                    }
                    fen += PIECE_CHAR[squares[index]];
                    emptySpaces = 0;
                }
            }
            if(emptySpaces > 0) {
                fen += emptySpaces;
            }
            fen += "/";
        }

        fen = fen.substring(0,fen.length() - 1);

        fen = playerToMove == Player.WHITE ? fen + " w " : fen + " b ";

        fen += getCastlingRights() + " ";

        int currentEnPassantPossibility = enPassantHistory[moveNumber];
        if (currentEnPassantPossibility != NO_EN_PASSANT_AVAILABLE) {
            fen += (char)('a' + currentEnPassantPossibility);
            if (playerToMove == Player.WHITE) {
                fen += 6;
            } else {
                fen += 3;
            }
        } else {
            fen += "-";
        }

        //Todo half move counter
        fen += " 0 0";

        return fen;
    }

    public static void main(String [] args) {
        Board0x88 board = new Board0x88("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b qkQK e3");


//        board = new Board0x88("4r3/k1p2ppp/8/P7/6P1/3q4/1K6/8 b ");          //Mate in 2
//        board = new Board0x88("8/k1p2ppp/8/P7/6P1/3q4/4r3/K7 b ");          //Mate in 1
//        Board0x88 board = new Board0x88("1nbqkbnr/Pppp0ppp/8/2ppp3/2PPP3/8/PPP1PPPP/RNBQKBNR w");   //Test pawn capture
//        Board0x88 board = new Board0x88("rnbqkbnr/pppppppp/3p4/8/4N3/8/PPPPPPPP/RNBQKBNR w");       //Test rook moves
//        Board0x88 board = new Board0x88("r3k2r/pppppppp/8/8/3PPPP1/N2Q1b1N/PPPB2BP/R3K2R w ");       //Castling
//        List<Move> list = board.getMovesOfPiece("e8", false);
//        for(Move m : list) {
//            System.out.println(m);
//        }

//        board.getAvailableMoves(false).stream().forEach(System.out::println);
//        Evaluator.perft(board, 5, Player.WHITE);

//        board.getMovesOfPiece("b2", false).forEach(System.out::println);
//        Evaluator.findBestMove(board);

//        board.executeMove("e2e4");
//        board.executeMove("a7a5");
//        board.printBoard();
//        board.revertLastMove();
        board.printBoard();

//        board.executeMove("e2e4");
//        while (true) {
//            board.executeMove(Evaluator.findBestMove(board));
//            board.printBoard();
//        }


    }
}
