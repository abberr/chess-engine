package main.game0x88;

import main.util.Util;

import java.util.List;
import java.util.Random;

import static main.game0x88.Pieces.*;

public class Board0x88 {

    private final static int BOARD_SIZE = 128;
    private final static int PIECES_SIZE = 12;
    private final static int COLORS_SIZE = 2;

    private final static int MAXIMUM_NUMBER_OF_MOVES = 4096;

    private Player playerToMove;
    private byte[] squares = new byte[BOARD_SIZE];

    private long [][][] zobristTable = new long[BOARD_SIZE][COLORS_SIZE][PIECES_SIZE];

    private int moveNumber = 0;
    private int[] castlingRightsHistory = new int[MAXIMUM_NUMBER_OF_MOVES];     //MASK: qkQK
    private int[] enPassantHistory = new int[MAXIMUM_NUMBER_OF_MOVES];

    Move lastMove;

    private long hash;

    public Board0x88(String fen) {

        String [] fields = fen.split(" ");

        //Init zobrist
        Random rnd = new Random(1);
        for (int i = 0; i < zobristTable.length; i++) {
            for (int j = 0; j < zobristTable[0].length; j++) {
                for (int k = 0; k < zobristTable[0][0].length; k++) {
                    zobristTable[i][j][k] = rnd.nextLong();
                }
            }
        }

        int x = 0, y = 0;
        for(char c : fields[0].toCharArray()) {
            byte piece = EMPY_SQUARE;
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

        //TODO castling rights from fen
        if (fields[2].charAt(0) != '-') {
            if (fields[2].contains("q")) castlingRightsHistory[moveNumber] |= 0b1000;
            if (fields[2].contains("k")) castlingRightsHistory[moveNumber] |= 0b0100;
            if (fields[2].contains("Q")) castlingRightsHistory[moveNumber] |= 0b0010;
            if (fields[2].contains("K")) castlingRightsHistory[moveNumber] |= 0b0001;
        } else {
            castlingRightsHistory[moveNumber] = 0b1111;
        }

        //TODO en passant from fen
        enPassantHistory[moveNumber] = 15;

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
                return true;
            }
        }
        return false;
    }

    public void executeMove(Move move) {
        int newCastlingRights = castlingRightsHistory[moveNumber];

        moveNumber++;

        squares[move.getMoveFrom()] = EMPY_SQUARE;
        squares[move.getMoveTo()] = move.getPiece();

        newCastlingRights = updateCastlingRights(move.getMoveFrom(), move.getMoveTo(), newCastlingRights);

        if (move.getPromotingPiece() != EMPY_SQUARE) {
            squares[move.getMoveTo()] = move.getPromotingPiece();
        }
        else if (move.isKingSideCastle()) {
            if (move.getPiece() == WHITE_KING) {
                squares[0x07] = EMPY_SQUARE;
                squares[0x05] = WHITE_ROOK;
            } else {
                squares[0x77] = EMPY_SQUARE;
                squares[0x75] = BLACK_ROOK;
            }
        } else if (move.isQueenSideCastle()) {
            if (move.getPiece() == WHITE_KING) {
                squares[0x00] = EMPY_SQUARE;
                squares[0x03] = WHITE_ROOK;
            } else {
                squares[0x70] = EMPY_SQUARE;
                squares[0x73] = BLACK_ROOK;
            }
        }

        if (move.isEnPassant()) {
            if (move.getPiece() == WHITE_PAWN) {
                squares[move.getMoveTo() + MoveGenerator.SOUTH] = EMPY_SQUARE;
            } else {
                squares[move.getMoveTo() + MoveGenerator.NORTH] = EMPY_SQUARE;
            }
        }

        enPassantHistory[moveNumber] = move.isPawnDoublePush() ? move.getMoveFrom()&0b0111 : 15;

        castlingRightsHistory[moveNumber] = newCastlingRights;

        updateHash(move);

        playerToMove = playerToMove.getOpponent();
        lastMove = move;
    }

    private int updateCastlingRights(int moveFrom, int moveTo, int newCastlingRights) {
        //if move involved rook or king startPos; update castling rights
        //TODO make constants
        if (moveFrom == 0x00 || moveTo == 0x00) {
            newCastlingRights &= 0b1101;
        } else if (moveFrom == 0x07 || moveTo == 0x07) {
            newCastlingRights &= 0b1110;
        } else if (moveFrom == 0x70 || moveTo == 0x70) {
            newCastlingRights &= 0b0111;
        } else if (moveFrom == 0x77 || moveTo == 0x77) {
            newCastlingRights &= 0b1011;
        } else if (moveFrom == 0x04 || moveTo == 0x04) {
            newCastlingRights &= 0b1100;
        } else if (moveFrom == 0x74 || moveTo == 0x74) {
            newCastlingRights &= 0b1100;
        }
        
        return newCastlingRights;
    }

    //TODO make sure it can only be called on last move
    public void executeInvertedMove(Move move) {

        squares[move.getMoveFrom()] = move.getPiece();
        if (move.isEnPassant()) {
            int capturedPieceDirection = move.getPiece() == WHITE_PAWN ? MoveGenerator.SOUTH : MoveGenerator.NORTH;
            squares[move.getMoveTo() + capturedPieceDirection] = move.getCapturedPiece();
            squares[move.getMoveTo()] = EMPY_SQUARE;
        } else {
            squares[move.getMoveTo()] = move.getCapturedPiece();
        }


        //Revert castling
        //TODO simplify
        if (move.isKingSideCastle()) {
            if (move.getPiece() == WHITE_KING) {
                squares[0x07] = WHITE_ROOK;
                squares[0x05] = EMPY_SQUARE;
            } else {
                squares[0x77] = BLACK_ROOK;
                squares[0x75] = EMPY_SQUARE;
            }
        } else if (move.isQueenSideCastle()) {
            if (move.getPiece() == WHITE_KING) {
                squares[0x00] = WHITE_ROOK;
                squares[0x03] = EMPY_SQUARE;
            } else {
                squares[0x70] = BLACK_ROOK;
                squares[0x73] = EMPY_SQUARE;
            }
        }

        playerToMove = playerToMove.getOpponent();

        updateHash(move);   //Has to be after player change to work
        moveNumber--;
    }

    public void revertLastMove() {
        executeInvertedMove(lastMove);
    }
    
    

    public List<Move> getMovesOfPiece(String position, boolean includePseudoLegal) {
        int index = Util.algebraicNotationToIndex(position);

        if (squares[index] == EMPY_SQUARE) {
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

    private void updateHash(Move move) {
        int moveFromindex = move.getMoveFrom();
        int moveToindex = move.getMoveTo();

        hash ^= zobristTable[moveFromindex][playerToMove.getHashValue()][move.getPiece() - 1];             //Remove piece from origin
        //If promoting move
        if (move.getPromotingPiece() != EMPY_SQUARE) {
            hash ^= zobristTable[moveToindex][playerToMove.getHashValue()][move.getPromotingPiece() - 1];  //Add promoting piece to new square
        }
        //If regular move
        else {
            hash ^= zobristTable[moveToindex][playerToMove.getHashValue()][move.getPiece() - 1];           //Add origin piece to new square
        }

        //If capturing move
        if (move.getCapturedPiece() != EMPY_SQUARE) {
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
    }


    private long generateZobristHash() {
        long hash = 0;
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                byte piece = squares[j + (i*16)];
//                if (piece != 0) {
//                    hash ^= zobristTable[j + (i*8)][piece - 1];
//                }
//            }
//        }
        for (int i = 0; i < 128; i++) {
                byte piece = squares[i];
                if (piece != 0) {
                    hash ^= zobristTable[i][playerToMove.getHashValue()][piece - 1];
                }
        }

        return hash;
    }

    public byte[] getSquares() {
        return squares;
    }

    public void printBoard() {
        System.out.println("--------------------------");
        for (int i = 0; i < 8; i++) {
            System.out.println();
            for (int j = 0; j < 8; j++) {
                byte p = squares[j + (7*16 - i*16)];
                System.out.print("[");
                if (p == 0)
                    System.out.print("  ");
                else {
                    System.out.print(PIECE_UNICODE[p]);
                }
                System.out.print("]");
            }
        }
        System.out.println("\nhash: [" + hash + "]");
        System.out.println("fen: [" + generateFen() + "]");
        System.out.println("value: [" + getValue() + "]");
        System.out.println("En passant file: [" + enPassantHistory[moveNumber] + "]");
        System.out.println("InCheck: " + MoveGenerator.isInCheck(squares, playerToMove));

        System.out.println("--------------------------");
    }

    private String generateFen() {
        String fen = "";
        for (int i = 0; i < 8; i++) {
            int emptySpaces = 0;
            for (int j = 0; j < 8; j++) {
                int index = j + (7*16 - i*16);
                if (squares[index] == EMPY_SQUARE) {
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

        int currentCastlingRights = castlingRightsHistory[moveNumber];
        if (currentCastlingRights != 0b0000) {
            if ((currentCastlingRights&0b1000) != 0) fen += "q";
            if ((currentCastlingRights&0b0100) != 0) fen += "k";
            if ((currentCastlingRights&0b0010) != 0) fen += "Q";
            if ((currentCastlingRights&0b0001) != 0) fen += "K";
        } else {
            fen += "- ";
        }

        return fen;
    }

    public static void main(String [] args) {
        Board0x88 board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w ");


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
        Evaluator.findBestMove(board);

//        board.executeMove("e4d5");
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
