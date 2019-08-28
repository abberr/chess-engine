package game;

import piece.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {

//    private static final int a=0, b=1, c=2, d=3, e=4, f=5, g=6, h=7;

    private Piece[][] board = new Piece[8][8];
    private long [][] zobristTable = new long[64][12];

    private long hash;

    public Board() {
        Random rnd = new Random(1);
        //Init zobrist
        for (int i = 0; i < zobristTable.length; i++) {
            for (int j = 0; j < zobristTable[0].length; j++) {
                zobristTable[i][j] = rnd.nextLong();
            }
        }


        // Init pieces
        board[0][1] = new Pawn(Player.BLACK);
        board[1][1] = new Pawn(Player.BLACK);
        board[2][1] = new Pawn(Player.BLACK);
        board[3][1] = new Pawn(Player.BLACK);
        board[4][1] = new Pawn(Player.BLACK);
        board[5][1] = new Pawn(Player.BLACK);
        board[6][1] = new Pawn(Player.BLACK);
        board[7][1] = new Pawn(Player.BLACK);

        board[0][0] = new Rook(Player.BLACK);
        board[7][0] = new Rook(Player.BLACK);
        board[1][0] = new Knight(Player.BLACK);
        board[6][0] = new Knight(Player.BLACK);
        board[2][0] = new Bishop(Player.BLACK);
        board[5][0] = new Bishop(Player.BLACK);
        board[3][0] = new Queen(Player.BLACK);
        board[4][0] = new King(Player.BLACK);


        board[0][6] = new Pawn(Player.WHITE);
        board[1][6] = new Pawn(Player.WHITE);
        board[2][6] = new Pawn(Player.WHITE);
        board[3][6] = new Pawn(Player.WHITE);
        board[4][6] = new Pawn(Player.WHITE);
        board[5][6] = new Pawn(Player.WHITE);
        board[6][6] = new Pawn(Player.WHITE);
        board[7][6] = new Pawn(Player.WHITE);

        board[0][7] = new Rook(Player.WHITE);
        board[7][7] = new Rook(Player.WHITE);
        board[1][7] = new Knight(Player.WHITE);
        board[6][7] = new Knight(Player.WHITE);
        board[2][7] = new Bishop(Player.WHITE);
        board[5][7] = new Bishop(Player.WHITE);
        board[3][7] = new Queen(Player.WHITE);
        board[4][7] = new King(Player.WHITE);



//        board[2][2] = new King(Player.BLACK);
//        board[1][2] = new Rook(Player.BLACK);
//
//        board[0][7] = new King(Player.WHITE);
//        board[0][0] = new Knight(Player.WHITE);



//        board[7][0] = new King(Player.BLACK);
//        board[1][1] = new Pawn(Player.BLACK);
//
//        board[0][7] = new King(Player.WHITE);
//        board[1][3] = new Pawn(Player.WHITE);
//        board[1][6] = new Knight(Player.WHITE);



//        board[2][2] = new King(Player.BLACK);
//        board[1][2] = new Rook(Player.BLACK);
//        board[3][5] = new Pawn(Player.BLACK);
//        board[4][4] = new Pawn(Player.BLACK);
//
//        board[0][7] = new King(Player.WHITE);
//        board[0][0] = new Knight(Player.WHITE);
//        board[5][7] = new Bishop(Player.WHITE);

        hash = generateZobristHash();
    }

    public void executeMove(Piece piece, Position moveTo) {
        Move move = new Move(piece, getPositionOfPiece(piece), moveTo);
        executeMove(move);
    }

    public void executeMove(Move move) {
        Piece movingPiece = move.getPiece();
        Piece capturedPiece = board[move.moveTo.x][move.moveTo.y];

        if (capturedPiece != null) {
            move.setCapturedPiece(capturedPiece);
        }

        if (movingPiece.getType() == Type.PAWN) {
            if (movingPiece.player == Player.WHITE) {
                if (move.moveTo.y == 0) {
                    move.promotingMove = true;
                }
            }
            else {
                if (move.moveTo.y == 7) {
                    move.promotingMove = true;
                }
            }
        }
        else if (movingPiece.getType() == Type.KING) {
            ((King) movingPiece).increaseMoveCounter();
            //Castling
            if(move.moveFrom.x == 4 && move.moveTo.x == 6) {
                move.setKingSideCastle(true);
                //move rook
                board[5][move.moveTo.y] = board[7][move.moveTo.y];
                board[7][move.moveTo.y] = null;
            }
            else if(move.moveFrom.x == 4 && move.moveTo.x == 1) {
                move.setQueenSideCastle(true);

                board[2][move.moveTo.y] = board[0][move.moveTo.y];
                board[0][move.moveTo.y] = null;
            }
        }

        board[move.moveFrom.x][move.moveFrom.y] = null;
        board[move.moveTo.x][move.moveTo.y] = movingPiece;

        if (move.promotingMove) {
            board[move.moveTo.x][move.moveTo.y] = new Queen(movingPiece.player);
        }

        updateHash(move);
    }


    public void executeInvertedMove(Move move) {
        Piece movingPiece = move.getPiece();

        board[move.moveFrom.x][move.moveFrom.y] = movingPiece;
        board[move.moveTo.x][move.moveTo.y] = move.getCapturedPiece();

        if (movingPiece.getType() == Type.KING) {
            ((King) movingPiece).decreaseMoveCounter();

            //Revert castling
            if (move.isKingSideCastle()) {
                board[7][move.moveTo.y] = board[5][move.moveTo.y];
                board[5][move.moveTo.y] = null;
            }
            else if (move.isQueenSideCastle()) {
                board[0][move.moveTo.y] = board[2][move.moveTo.y];
                board[2][move.moveTo.y] = null;
            }
        }

        //No need to reverse promotion



        updateHash(move);
    }

    private void updateHash(Move move) {
        int moveFromindex = move.moveFrom.x + (move.moveFrom.y*8);
        int moveToindex = move.moveTo.x + (move.moveTo.y*8);

        hash ^= zobristTable[moveFromindex][move.piece.getIndex()];
        hash ^= zobristTable[moveToindex][move.piece.getIndex()];

        if (move.capturedPiece != null) {
            hash ^= zobristTable[moveToindex][move.capturedPiece.getIndex()];
        }
        //TODO: promoting hash

        //TODO: castling hash
    }

    private Position getPositionOfPiece(Piece piece) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (piece.equals(board[i][j])) {
                    return new Position(i,j);
                }
            }
        }
        return null;
    }

    public Piece getPieceAt(Position pos) {
        return board[pos.x][pos.y];
    }

    public boolean isSquareUnderAttack(Position pos, Player player) {
       for(Move move : getAvailableMoves(player.getOpponent(), true)) {
           if (move.moveTo.equals(pos)) {
               return true;
           }
       }
       return false;
    }

    public List<Move> getMoves(Piece piece, boolean includeIllegal) {
        ArrayList<Move> moves = new ArrayList<>();
        for (Position m : piece.getAvailableMoves(getPositionOfPiece(piece), board)) {
            Move move = new Move(piece, getPositionOfPiece(piece), m);

            if (!includeIllegal) {
                //Check if move checks
                executeMove(move);
                if (!Evaluator.isChecked(piece.player, this)) {

                    if (move.isKingSideCastle() && !isSquareUnderAttack(new Position(5, move.moveTo.y), piece.player)) {
                        moves.add(move);
                    } else if (move.isQueenSideCastle() && !isSquareUnderAttack(new Position(3, move.moveTo.y), piece.player)
                            && !isSquareUnderAttack(new Position(2, move.moveTo.y), piece.player)) {
                        moves.add(move);
                    }
                    else {
                        moves.add(move);
                    }
                }

                //TODO: castling

                executeInvertedMove(move);
            } else {
                moves.add(move);
            }
        }
        return moves;
    }

    public Position getPositionOfKing(Player player) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[j][i] != null && board[j][i].player == player && board[j][i].getType() == Type.KING) {
                    return new Position(j, i);
                }
            }
        }

        return null;
    }

    //illegal = moves that cause check on self
    public List<Move> getAvailableMoves(Player player, boolean includeIllegal) {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] != null) {
                    Piece p = board[i][j];
                    if (p.player == player) {
                        moves.addAll(getMoves(p, includeIllegal));
                    }
                }
            }
        }
        return moves;
    }

    public int getValue() {
        int value = 0;

        //Add white values, negate black values
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] != null && board[i][j].player == Player.WHITE) {
                    value += board[i][j].getValue(new Position(i, j));
//                    value += 10 * getMoves(board[i][j], false).size();
                }
                else if (board[i][j] != null && board[i][j].player == Player.BLACK) {
                    value -= board[i][j].getValue(new Position(i, j));
//                    value -= 10 * getMoves(board[i][j], false).size();
                }
            }
        }

        //Checkmate
//        if (getAvailableMoves(Player.WHITE, false).size() == 0) {
//            value = Integer.MIN_VALUE;
//        } else if (getAvailableMoves(Player.BLACK, false).size() == 0) {
//            value = Integer.MAX_VALUE;
//        }

        return value;
    }


    private long generateZobristHash() {
        long hash = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null) {
                    int k = board[i][j].getIndex();
                    hash ^= zobristTable[i + (j*8)][k];
                }
            }
        }

        return hash;
    }

    public long getHash() {
        return hash;
    }

//    ⬛⬜
    public void printBoard() {
        for (int i = 0; i < board.length; i++) {
            System.out.println();
            for (int j = 0; j < board.length; j++) {
                Piece p = board[j][i];
                System.out.print("[");
                if (p == null)
                    System.out.print("  ");
                else {
                    System.out.print(p.getUnicodeSymbol());
                }
                System.out.print("]");
            }
        }
        System.out.println();
    }



}
