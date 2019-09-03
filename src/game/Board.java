package game;

import piece.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {

//    private static final int a=0, b=1, c=2, d=3, e=4, f=5, g=6, h=7;

    private Player playerToMove;
    private Piece[][] board = new Piece[8][8];
    private long [][] zobristTable = new long[64][12];

    private long hash;

    public Board(String fen) {
        Random rnd = new Random(1);
        //Init zobrist
        for (int i = 0; i < zobristTable.length; i++) {
            for (int j = 0; j < zobristTable[0].length; j++) {
                zobristTable[i][j] = rnd.nextLong();
            }
        }

        int x = 0, y = 0;
        for (char c : fen.split(" ")[0].toCharArray()) {
            if (c == 'P')  board[x][y] = new Pawn(Player.WHITE);
            else if (c == 'p')  board[x][y] = new Pawn(Player.BLACK);
            else if (c == 'N')  board[x][y] = new Knight(Player.WHITE);
            else if (c == 'n')  board[x][y] = new Knight(Player.BLACK);
            else if (c == 'B')  board[x][y] = new Bishop(Player.WHITE);
            else if (c == 'b')  board[x][y] = new Bishop(Player.BLACK);
            else if (c == 'R')  board[x][y] = new Rook(Player.WHITE);
            else if (c == 'r')  board[x][y] = new Rook(Player.BLACK);
            else if (c == 'Q')  board[x][y] = new Queen(Player.WHITE);
            else if (c == 'q')  board[x][y] = new Queen(Player.BLACK);
            else if (c == 'K')  board[x][y] = new King(Player.WHITE);
            else if (c == 'k')  board[x][y] = new King(Player.BLACK);
            else if (c >= '0' && c <= '8') x += (c - '0') - 1;
            else if (c == '/') {
                y++;
                x = 0;
                continue;
            }

            x++;
        }

        playerToMove = Player.WHITE;
        if (fen.split(" ")[1].charAt(0) == 'b') {
            playerToMove = Player.BLACK;
        }

    }

    public Board() {
        Random rnd = new Random(1);
        //Init zobrist
        for (int i = 0; i < zobristTable.length; i++) {
            for (int j = 0; j < zobristTable[0].length; j++) {
                zobristTable[i][j] = rnd.nextLong();
            }
        }

        playerToMove = Player.WHITE;

        // Init pieces
        for (int i = 0; i < board.length; i++) {
            board[i][1] = new Pawn(Player.BLACK);
            board[i][6] = new Pawn(Player.WHITE);
        }

        board[0][0] = new Rook(Player.BLACK);
        board[7][0] = new Rook(Player.BLACK);
        board[1][0] = new Knight(Player.BLACK);
        board[6][0] = new Knight(Player.BLACK);
        board[2][0] = new Bishop(Player.BLACK);
        board[5][0] = new Bishop(Player.BLACK);
        board[3][0] = new Queen(Player.BLACK);
        board[4][0] = new King(Player.BLACK);

        board[0][7] = new Rook(Player.WHITE);
        board[7][7] = new Rook(Player.WHITE);
        board[1][7] = new Knight(Player.WHITE);
        board[6][7] = new Knight(Player.WHITE);
        board[2][7] = new Bishop(Player.WHITE);
        board[5][7] = new Bishop(Player.WHITE);
        board[3][7] = new Queen(Player.WHITE);
        board[4][7] = new King(Player.WHITE);

        //Check castling
//        board[4][7] = new King(Player.WHITE);
//        board[0][7] = new Rook(Player.WHITE);
//        board[7][7] = new Rook(Player.WHITE);
//        board[4][0] = new King(Player.BLACK);
//        board[0][0] = new Rook(Player.BLACK);
//        board[5][0] = new Rook(Player.BLACK);




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
                    move.promotingPiece = new Queen(movingPiece.player);
                }
            }
            else {
                if (move.moveTo.y == 7) {
                    move.promotingPiece = new Queen(movingPiece.player);
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

        if (move.promotingPiece != null) {
            board[move.moveTo.x][move.moveTo.y] = move.promotingPiece;
        }

        updateHash(move);

        playerToMove = playerToMove.getOpponent();
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

        //TODO check if reversing castling works
        updateHash(move);

        playerToMove = playerToMove.getOpponent();
    }

    private void updateHash(Move move) {
        int moveFromindex = move.moveFrom.x + (move.moveFrom.y*8);
        int moveToindex = move.moveTo.x + (move.moveTo.y*8);

        hash ^= zobristTable[moveFromindex][move.piece.getIndex()];             //Remove piece from origin
        //If promoting move
        if (move.promotingPiece != null) {
            hash ^= zobristTable[moveToindex][move.promotingPiece.getIndex()];  //Add promoting piece to new square
        }
        //If regular move
        else {
            hash ^= zobristTable[moveToindex][move.piece.getIndex()];           //Add origin piece to new square
        }

        //If capturing move
        if (move.capturedPiece != null) {
            hash ^= zobristTable[moveToindex][move.capturedPiece.getIndex()];   //Remove captured piece from new square
        }

        //If castling move
        if (move.isKingSideCastle() || move.isQueenSideCastle()) {
            int rookFromX = move.isKingSideCastle() ? 7 : 0;
            int rookToX = move.isKingSideCastle() ? 5 : 2;
//            Piece rook = board[rookToX][move.moveTo.y];
            Piece rook = new Rook(move.piece.player);
            int rookMoveFromindex = rookFromX + (move.moveTo.y*8);
            int rookMoveToindex = rookToX + (move.moveTo.y*8);

            hash ^= zobristTable[rookMoveFromindex][rook.getIndex()];           //Remove rook from corner
            hash ^= zobristTable[rookMoveToindex][rook.getIndex()];             //Add rook to new location
        }
    }

    public Position getPositionOfPiece(Piece piece) {
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

                    if (move.isKingSideCastle()) {
                        if (!isSquareUnderAttack(new Position(5, move.moveTo.y), piece.player))
                            moves.add(move);
                    } else if (move.isQueenSideCastle()) {
                        if (!isSquareUnderAttack(new Position(3, move.moveTo.y), piece.player)
                                && !isSquareUnderAttack(new Position(2, move.moveTo.y), piece.player))
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

    public String generateFen() {
        String fen = "";

        //Board
        for (int y = 0; y < board.length; y++) {
            int emptySpaces = 0;
            for (int x = 0; x < board.length; x++) {
                Piece p = board[x][y];
                if (p != null) {
                    fen = emptySpaces == 0 ? fen + p.getSymbol() : fen + emptySpaces + p.getSymbol();
                    emptySpaces = 0;
                } else {
                    emptySpaces++;
                }
            }
            fen = emptySpaces == 0 ? fen : fen + emptySpaces;
            fen = y < board.length-1 ? fen + "/" : fen + "";

        }

        fen += playerToMove == Player.WHITE ? " w " : " b ";

        //TODO Castling rights

        return fen;
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

    public Player getPlayerToMove() {
        return playerToMove;
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
        System.out.println("\n" + generateFen());
    }



}
