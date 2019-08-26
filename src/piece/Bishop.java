package piece;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import game.Position;
import util.Util;

public class Bishop extends Piece {

    private int[][] pieceSquareTable = {
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}};

    public Bishop(Player player) {
        super(player);
        if (player == Player.BLACK) {
            pieceSquareTable = Util.reverseArray(pieceSquareTable);
        }
    }

    @Override
    public Type getType() {
        return Type.BISHOP;
    }

    @Override
    public BufferedImage getImage() {
        try {
            if (player == Player.WHITE)
                return ImageIO.read(new File("src/res/bw.png"));
            else
                return ImageIO.read(new File("src/res/bb.png"));
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    @Override
    public Position[] getAvailableMoves(Position pos, Piece[][] board) {
        ArrayList<Position> moves = new ArrayList<>();

        // Traverse southeast
        int x = pos.x + 1;
        int y = pos.y + 1;
        while (!isOutOfBounds(x, y) && board[x][y] == null) {
            moves.add(new Position(x, y));
            y++;
            x++;
        }
        if (!isOutOfBounds(x, y) && board[x][y] != null && board[x][y].player != this.player) {
            moves.add(new Position(x, y));
        }

        // Traverse southwest
        y = pos.y + 1;
        x = pos.x - 1;
        while (!isOutOfBounds(x, y) && board[x][y] == null) {
            moves.add(new Position(x, y));
            y++;
            x--;
        }
        if (!isOutOfBounds(x, y) && board[x][y] != null && board[x][y].player != this.player) {
            moves.add(new Position(x, y));
        }

        // Traverse northwest
        x = pos.x - 1;
        y = pos.y - 1;
        while (!isOutOfBounds(x, y) && board[x][y] == null) {
            moves.add(new Position(x, y));
            x--;
            y--;
        }
        if (!isOutOfBounds(x, y) && board[x][y] != null && board[x][y].player != this.player) {
            moves.add(new Position(x, y));
        }

        // Traverse northeast
        x = pos.x + 1;
        y = pos.y - 1;
        while (!isOutOfBounds(x, y) && board[x][y] == null) {
            moves.add(new Position(x, y));
            x++;
            y--;
        }
        if (!isOutOfBounds(x, y) && board[x][y] != null && board[x][y].player != this.player) {
            moves.add(new Position(x, y));
        }

        return moves.toArray(new Position[0]);
    }

    @Override
    public int getValue(Position pos) {
        return 330 + pieceSquareTable[pos.y][pos.x];
    }

    @Override
    public int getIndex() {
        if (player == Player.WHITE) {
            return 3;
        } else {
            return 9;
        }
    }

}
