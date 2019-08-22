package game;

import piece.*;
import util.Util;

import java.util.ArrayList;
import java.util.List;

public class Board {

//    private static final int a=0, b=1, c=2, d=3, e=4, f=5, g=6, h=7;

    private Piece[][] board = new Piece[8][8];

    public Board() {
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
    }

    public void executeMove(Move move) {
        // Check if checked
//        if (Evaluator.isChecked(move.getPiece().player, this)) {
//            System.out.println("CHECKED");
//        }
        Piece capturedPiece = board[move.moveTo.x][move.moveTo.y];
        if (capturedPiece != null) {
            move.setCapturedPiece(capturedPiece);
        }

        board[move.moveFrom.x][move.moveFrom.y] = null;
        board[move.moveTo.x][move.moveTo.y] = move.getPiece();
    }

    public void executeMove(Piece piece, Position moveTo) {
        Move move = new Move(piece, getPositionOfPiece(piece), moveTo);
        executeMove(move);
    }

    public void executeInvertedMove(Move move) {
        board[move.moveFrom.x][move.moveFrom.y] = move.getPiece();
        board[move.moveTo.x][move.moveTo.y] = move.getCapturedPiece();
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

    public List<Move> getMoves(Piece piece, boolean includeIllegal) {
        ArrayList<Move> moves = new ArrayList<>();
        for (Position m : piece.getAvailableMoves(getPositionOfPiece(piece), board)) {
            Move move = new Move(piece, getPositionOfPiece(piece), m);

            if (!includeIllegal) {
                //Check if move checks
                executeMove(move);
                if (!Evaluator.isChecked(piece.player, this)) {
                    moves.add(move);
                }
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

    public float getValue() {
        int value = 0;
        //Add white values, negate black values
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] != null && board[i][j].player == Player.WHITE) {
                    value += board[i][j].getValue();
                }
                else if (board[i][j] != null && board[i][j].player == Player.BLACK) {
                    value -= board[i][j].getValue();
                }
            }
        }

        if (getAvailableMoves(Player.WHITE, false).size() == 0) {
            value = Integer.MIN_VALUE;
        } else if (getAvailableMoves(Player.BLACK, false).size() == 0) {
            value = Integer.MAX_VALUE;
        }

        return value;
    }

}
