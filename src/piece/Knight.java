package piece;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import game.Position;
import util.Util;

public class Knight extends Piece {

    private int[][] pieceSquareTable = {
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}};

    public Knight(Player player) {
        super(player);
        if (player == Player.BLACK) {
            pieceSquareTable = Util.reverseArray(pieceSquareTable);
        }
    }

    @Override
    public Type getType() {
        return Type.KNIGHT;
    }

    @Override
    public BufferedImage getImage() {
        try {
            if (player == Player.WHITE)
                return ImageIO.read(new File("src/res/kw.png"));
            else
                return ImageIO.read(new File("src/res/kb.png"));
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    @Override
    public Position[] getAvailableMoves(Position pos, Piece[][] board) {
        ArrayList<Position> moves = new ArrayList<>();

        moves.add(new Position(pos.x + 2, pos.y + 1));
        moves.add(new Position(pos.x + 2, pos.y - 1));
        moves.add(new Position(pos.x + 1, pos.y + 2));
        moves.add(new Position(pos.x + 1, pos.y - 2));
        moves.add(new Position(pos.x - 1, pos.y + 2));
        moves.add(new Position(pos.x - 1, pos.y - 2));
        moves.add(new Position(pos.x - 2, pos.y + 1));
        moves.add(new Position(pos.x - 2, pos.y - 1));

        removeIllegalMoves(moves, board);

        return moves.toArray(new Position[0]);
    }

    @Override
    public int getValue(Position pos) {
        return 320 + pieceSquareTable[pos.y][pos.x];
    }

    @Override
    public int getIndex() {
        if (player == Player.WHITE) {
            return 2;
        } else {
            return 8;
        }
    }

}
