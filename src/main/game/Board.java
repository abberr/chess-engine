package main.game;

import main.piece.*;

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

            hash = generateZobristHash();
        }

        playerToMove = Player.WHITE;
        if (fen.split(" ")[1].charAt(0) == 'b') {
            playerToMove = Player.BLACK;
        }

    }

    public Board() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w ");
    }

    public void executeMove(Piece piece, Position moveTO) {
        Move move = new Move(piece, getPositionOfPiece(piece), moveTO);
        executeMove(move);
    }

    public void executeMove(Move move) {
        Piece movingPiece = move.getPiece();
        Piece capturedPiece = board[move.getMoveTo().x][move.getMoveTo().y];

        if (capturedPiece != null) {
            move.setCapturedPiece(capturedPiece);
        }

        if (movingPiece.getType() == Type.PAWN) {
            if (movingPiece.player == Player.WHITE) {
                if (move.getMoveTo().y == 0) {
                    move.setPromotingPiece(new Queen(movingPiece.player));
                }
            }
            else {
                if (move.getMoveTo().y == 7) {
                    move.setPromotingPiece(new Queen(movingPiece.player));
                }
            }
        }
        else if (movingPiece.getType() == Type.KING) {
            ((King) movingPiece).increaseMoveCounter();
            //Castling
            if(move.getMoveFrom().x == 4 && move.getMoveTo().x == 6) {
                move.setKingSideCastle(true);
                //move rook
                board[5][move.getMoveTo().y] = board[7][move.getMoveTo().y];
                board[7][move.getMoveTo().y] = null;
            }
            else if(move.getMoveFrom().x == 4 && move.getMoveTo().x == 2) {
                move.setQueenSideCastle(true);

                board[3][move.getMoveTo().y] = board[0][move.getMoveTo().y];
                board[0][move.getMoveTo().y] = null;
            }
        }

        board[move.getMoveFrom().x][move.getMoveFrom().y] = null;
        board[move.getMoveTo().x][move.getMoveTo().y] = movingPiece;

        if (move.getPromotingPiece() != null) {
            board[move.getMoveTo().x][move.getMoveTo().y] = move.getPromotingPiece();
        }

        updateHash(move);

        playerToMove = playerToMove.getOpponent();
    }


    public void executeInvertedMove(Move move) {
        Piece movingPiece = move.getPiece();

        board[move.getMoveFrom().x][move.getMoveFrom().y] = movingPiece;
        board[move.getMoveTo().x][move.getMoveTo().y] = move.getCapturedPiece();

        if (movingPiece.getType() == Type.KING) {
            ((King) movingPiece).decreaseMoveCounter();

            //Revert castling
            if (move.isKingSideCastle()) {
                board[7][move.getMoveTo().y] = board[5][move.getMoveTo().y];
                board[5][move.getMoveTo().y] = null;
            }
            else if (move.isQueenSideCastle()) {
                board[0][move.getMoveTo().y] = board[3][move.getMoveTo().y];
                board[3][move.getMoveTo().y] = null;
            }
        }

        //No need to reverse promotion

        updateHash(move);

        playerToMove = playerToMove.getOpponent();
    }

    private void updateHash(Move move) {
        int moveFromindex = move.getMoveFrom().x + (move.getMoveFrom().y*8);
        int moveToindex = move.getMoveTo().x + (move.getMoveTo().y*8);

        hash ^= zobristTable[moveFromindex][move.getPiece().getIndex()];             //Remove main.piece from origin
        //If promoting move
        if (move.getPromotingPiece() != null) {
            hash ^= zobristTable[moveToindex][move.getPromotingPiece().getIndex()];  //Add promoting main.piece to new square
        }
        //If regular move
        else {
            hash ^= zobristTable[moveToindex][move.getPiece().getIndex()];           //Add origin main.piece to new square
        }

        //If capturing move
        if (move.getCapturedPiece() != null) {
            hash ^= zobristTable[moveToindex][move.getCapturedPiece().getIndex()];   //Remove captured main.piece from new square
        }

        //If castling move
        if (move.isKingSideCastle() || move.isQueenSideCastle()) {
            int rookFromX = move.isKingSideCastle() ? 7 : 0;
            int rookToX = move.isKingSideCastle() ? 5 : 3;
//            Piece rook = board[rookToX][move.getMoveTo().y];
            Piece rook = new Rook(move.getPiece().player);
            int rookMoveFromindex = rookFromX + (move.getMoveTo().y*8);
            int rookMoveToIndex = rookToX + (move.getMoveTo().y*8);

            hash ^= zobristTable[rookMoveFromindex][rook.getIndex()];           //Remove rook from corner
            hash ^= zobristTable[rookMoveToIndex][rook.getIndex()];             //Add rook to new location
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
           if (move.getMoveTo().equals(pos)) {
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
                        if (!isSquareUnderAttack(new Position(5, move.getMoveTo().y), piece.player))
                            moves.add(move);
                    } else if (move.isQueenSideCastle()) {
                        if (!isSquareUnderAttack(new Position(3, move.getMoveTo().y), piece.player))
                            moves.add(move);
                    }
                    else {
                        moves.add(move);
                    }
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

    //pseudo legal = moves that cause check on self
    public List<Move> getAvailableMoves(Player player, boolean includePseudoLegal) {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] != null) {
                    Piece p = board[i][j];
                    if (p.player == player) {
                        moves.addAll(getMoves(p, includePseudoLegal));
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
        System.out.println("Hash: " + hash);
    }



}
