package main.piece;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import main.game.Player;
import main.game.Position;
import main.util.Util;

public class Queen extends Piece {

    private int[][] pieceSquareTable = {
            {-20, -10, -10, -5, -5, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {0, 0, 5, 5, 5, 5, 0, -5},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-20, -10, -10, -5, -5, -10, -10, -20}};

    public Queen(Player player) {
        super(player);
        if (player == Player.BLACK) {
            pieceSquareTable = Util.reverseArray(pieceSquareTable);
        }
    }

    @Override
    public Type getType() {
        return Type.QUEEN;
    }

    @Override
    public BufferedImage getImage() {
        try {
            if (player == Player.WHITE)
                return ImageIO.read(new File("src/main.res/qw.png"));
            else
                return ImageIO.read(new File("src/main.res/qb.png"));
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    @Override
    public char getUnicodeSymbol() {
        return this.player == Player.BLACK ? '♕' : '♛';
    }

    @Override
    public char getSymbol() {
        return this.player == Player.BLACK ? 'q' : 'Q';
    }

    @Override
    public Position[] getAvailableMoves(Position pos, Piece[][] board) {
        ArrayList<Position> moves = new ArrayList<>();

        //Create temporary rook and bishop and get their available moves
        Piece[][] tempBoard = Util.copyBoard(board);
        tempBoard[pos.x][pos.y] = new Rook(player);
        moves.addAll(new ArrayList<Position>(Arrays.asList(tempBoard[pos.x][pos.y].getAvailableMoves(pos, board))));
        tempBoard[pos.x][pos.y] = new Bishop(player);
        moves.addAll(new ArrayList<Position>(Arrays.asList(tempBoard[pos.x][pos.y].getAvailableMoves(pos, board))));

        return moves.toArray(new Position[0]);
    }

    @Override
    public int getValue(Position pos) {
        return 900 + pieceSquareTable[pos.y][pos.x];
    }

    @Override
    public int getIndex() {
        if (player == Player.WHITE) {
            return 4;
        } else {
            return 10;
        }
    }

}
