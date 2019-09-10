package main.game0x88;

import main.game.Player;
import main.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static main.game0x88.Pieces.*;

public class Board0x88 {

    private Player playerToMove;
    private byte[] squares = new byte[128];
    private byte castlingRights;        //0bqkQK

    private long [][] zobristTable = new long[64][12];


    private long hash;

    public Board0x88(String fen) {

        //Init zobrist
        Random rnd = new Random(1);
        for (int i = 0; i < zobristTable.length; i++) {
            for (int j = 0; j < zobristTable[0].length; j++) {
                zobristTable[i][j] = rnd.nextLong();
            }
        }

        int x = 0, y = 0;
        for(char c : fen.split(" ")[0].toCharArray()) {
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
            squares[x + (7*16 - y*16)] = piece;

            x++;
        }

        playerToMove = Player.WHITE;
        if (fen.split(" ")[1].charAt(0) == 'b') {
            playerToMove = Player.BLACK;
        }

        //TODO castling rights from fen
        castlingRights = 0b1111;

        hash = generateZobristHash();
    }

    public Player getPlayerToMove() {
        return playerToMove;
    }


    public long getHash() {
        return hash;
    }


    public void executeMove(String move){
        int moveFrom = Util.algebraicNotationToIndex(move.substring(0,2));
        int moveTo = Util.algebraicNotationToIndex(move.substring(2,4));
        byte piece = squares[moveFrom];

        executeMove(new Move(piece, moveFrom, moveTo));
    }

    public void executeMove(Move move) {
        squares[move.getMoveFrom()] = EMPY_SQUARE;
        squares[move.getMoveTo()] = move.getPiece();

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

        playerToMove = playerToMove.getOpponent();
    }

    public void executeInvertedMove(Move move) {
        squares[move.getMoveFrom()] = move.getPiece();
        squares[move.getMoveTo()] = move.getCapturedPiece();

        //Revert castling
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
    }

    public List<Move> getMovesOfPiece(String position, boolean includePseudoLegal) {

        int index = Util.algebraicNotationToIndex(position);
        return MoveGenerator.generateMovesOfPiece(squares, index, castlingRights, includePseudoLegal);
    }

    public List<Move> getAvailableMoves(boolean includePseudoLegal) {
        return MoveGenerator.generateMoves(squares, playerToMove, castlingRights, includePseudoLegal);
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

    //TODO
    private void updateHash(Move move) {
        int moveFromindex = 0;
        int moveToindex = 0;

        hash ^= zobristTable[moveFromindex][move.getPiece() - 1];             //Remove piece from origin
        //If promoting move
        if (move.getPromotingPiece() != EMPY_SQUARE) {
            hash ^= zobristTable[moveToindex][move.getPromotingPiece() - 1];  //Add promoting piece to new square
        }
        //If regular move
        else {
            hash ^= zobristTable[moveToindex][move.getPiece() - 1];           //Add origin piece to new square
        }

        //If capturing move
        if (move.getCapturedPiece() != EMPY_SQUARE) {
            hash ^= zobristTable[moveToindex][move.getCapturedPiece() - 1];   //Remove captured piece from new square
        }

        //If castling move
//        if (move.isKingSideCastle() || move.isQueenSideCastle()) {
//            int rookFromX = move.isKingSideCastle() ? 7 : 0;
//            int rookToX = move.isKingSideCastle() ? 5 : 3;
////            Piece rook = board[rookToX][move.getMoveTo().y];
//            Piece rook = new Rook(move.getPiece().player);
//            int rookMoveFromindex = rookFromX + (move.getMoveTo().y*8);
//            int rookMoveToIndex = rookToX + (move.getMoveTo().y*8);
//
//            hash ^= zobristTable[rookMoveFromindex][rook.getIndex()];           //Remove rook from corner
//            hash ^= zobristTable[rookMoveToIndex][rook.getIndex()];             //Add rook to new location
//        }
    }


    private long generateZobristHash() {
        long hash = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                byte piece = squares[j + (i*16)];
                if (piece != 0) {
                    hash ^= zobristTable[j + (i*8)][piece - 1];
                }
            }
        }

        return hash;
    }


    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            System.out.println();
            for (int j = 0; j < 8; j++) {
                byte p = squares[j + (7*16 - i*16)];
                System.out.print("[");
                if (p == 0)
                    System.out.print(" ");
                else {
                    System.out.print(PIECE_UNICODE[p]);
                }
                System.out.print("]");
            }
        }
        System.out.println("\nhash: [" + hash + "]");
        System.out.println("InCheck: " + MoveGenerator.isInCheck(squares, playerToMove));
    }

    public static void main(String [] args) {
        Board0x88 board = new Board0x88("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w");
//        Board0x88 board = new Board0x88("rnbqkbnr/pppppppp/8/3p4/4K3/1B6/PPPPPPPP/RNBQ1BNR w");
//        Board0x88 board = new Board0x88("1nbqkbnr/Pppp0ppp/8/2ppp3/2PPP3/8/PPP1PPPP/RNBQKBNR w");   //Test pawn capture
//        Board0x88 board = new Board0x88("rnbqkbnr/pppppppp/3p4/8/4N3/8/PPPPPPPP/RNBQKBNR w");       //Test rook moves
//        Board0x88 board = new Board0x88("r3k2r/pppppppp/8/8/3PPPP1/N2Q1b1N/PPPB2BP/R3K2R w ");       //Castling
//        List<Move> list = board.getMovesOfPiece("e8", false);
//        for(Move m : list) {
//            System.out.println(m);
//        }
//        board.executeMove("b1c3");
//        board.executeMove(list.get(2));
//        board.executeInvertedMove(list.get(2));

//        board.getAvailableMoves(false).stream().forEach(System.out::println);
//        Evaluator.perft(board, 5, Player.WHITE);

        System.out.println(board.getValue());
        Evaluator.findBestMove(board);
        board.printBoard();
    }
}
