package piece;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import game.Player;
import game.Position;
import util.Util;

public class Pawn extends Piece {

    private int[][] pieceSquareTable = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 5},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -20, -20, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}};

    public Pawn(Player player) {
        super(player);
        if (player == Player.BLACK) {
            pieceSquareTable = Util.reverseArray(pieceSquareTable);
        }
    }

    @Override
    public Type getType() {
        return Type.PAWN;
    }


    @Override
    public BufferedImage getImage() {
        try {
            if (player == Player.WHITE)
                return ImageIO.read(new File("src/res/pw.png"));
            else
                return ImageIO.read(new File("src/res/pb.png"));
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    @Override
    public char getUnicodeSymbol() {
        return this.player == Player.BLACK ? '♙' : '♟';
    }

    @Override
    public char getSymbol() {
        return this.player == Player.BLACK ? 'p' : 'P';
    }

    @Override
    public Position[] getAvailableMoves(Position pos, Piece[][] board) {
        ArrayList<Position> moves = new ArrayList<>();
        int direction = -1;

        boolean hasMoved = true;
        if ((player == Player.BLACK && pos.y == 1) || (player == Player.WHITE && pos.y == 6)) {
            hasMoved = false;
        }

        if (player == Player.BLACK)
            direction = 1;
        if (pos.y + direction < 8 && pos.y + direction >= 0) {
            //Straigt forward
            if (board[pos.x][pos.y + direction] == null) {
                moves.add(new Position(pos.x, pos.y + direction));

                //If startpos, two moves available
                if (!hasMoved && pos.y + 2 * direction < 6 && pos.y + 2 * direction > 1 && board[pos.x][pos.y + 2 * direction] == null) {
                    moves.add(new Position(pos.x, pos.y + 2 * direction));
                }
            }

            //Capturing moves
            if (pos.x > 0 && board[pos.x - 1][pos.y + direction] != null && board[pos.x - 1][pos.y + direction].player != player) {
                moves.add(new Position(pos.x - 1, pos.y + direction));
            }
            if (pos.x < 7 && board[pos.x + 1][pos.y + direction] != null && board[pos.x + 1][pos.y + direction].player != player) {
                moves.add(new Position(pos.x + 1, pos.y + direction));
            }

            //TODO en passant
        }

        return moves.toArray(new Position[0]);
    }

    @Override
    public int getValue(Position pos) {
        return 100 + pieceSquareTable[pos.y][pos.x];
    }

    @Override
    public int getIndex() {
        if (player == Player.WHITE) {
            return 0;
        } else {
            return 6;
        }
    }

}
