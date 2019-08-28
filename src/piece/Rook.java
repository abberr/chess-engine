package piece;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import game.Player;
import game.Position;
import util.Util;

public class Rook extends Piece {

    private int[][] pieceSquareTable = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}};

    public Rook(Player player) {
        super(player);
        if (player == Player.BLACK) {
            pieceSquareTable = Util.reverseArray(pieceSquareTable);
        }
    }

    @Override
    public Type getType() {
        return Type.ROOK;
    }

    @Override
    public BufferedImage getImage() {
        try {
            if (player == Player.WHITE)
                return ImageIO.read(new File("src/res/rw.png"));
            else
                return ImageIO.read(new File("src/res/rb.png"));
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    @Override
    public char getUnicodeSymbol() {
        return this.player == Player.BLACK ? '♖' : '♜';
    }

    @Override
    public Position[] getAvailableMoves(Position pos, Piece[][] board) {
        ArrayList<Position> moves = new ArrayList<>();

        // Traverse y-axis downward
        int y = pos.y + 1;
        while (y <= 7 && board[pos.x][y] == null) {
            moves.add(new Position(pos.x, y));
            y++;
        }
        if (y <= 7 && board[pos.x][y] != null && board[pos.x][y].player != this.player) {
            moves.add(new Position(pos.x, y));
        }

        // Traverse y-axis upward
        y = pos.y - 1;
        while (y >= 0 && board[pos.x][y] == null) {
            moves.add(new Position(pos.x, y));
            y--;
        }
        if (y >= 0 && board[pos.x][y] != null && board[pos.x][y].player != this.player) {
            moves.add(new Position(pos.x, y));
        }

        // Traverse x-axis right
        int x = pos.x + 1;
        while (x <= 7 && board[x][pos.y] == null) {
            moves.add(new Position(x, pos.y));
            x++;
        }
        if (x <= 7 && board[x][pos.y] != null && board[x][pos.y].player != this.player) {
            moves.add(new Position(x, pos.y));
        }

        // Traverse x-axis left
        x = pos.x - 1;
        while (x >= 0 && board[x][pos.y] == null) {
            moves.add(new Position(x, pos.y));
            x--;
        }
        if (x >= 0 && board[x][pos.y] != null && board[x][pos.y].player != this.player) {
            moves.add(new Position(x, pos.y));
        }

        return moves.toArray(new Position[0]);
    }

    @Override
    public int getValue(Position pos) {
        return 500 + pieceSquareTable[pos.y][pos.x];
    }

    @Override
    public int getIndex() {
        if (player == Player.WHITE) {
            return 1;
        } else {
            return 7;
        }
    }
}
