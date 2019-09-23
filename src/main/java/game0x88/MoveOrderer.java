package game0x88;

import java.util.Comparator;
import java.util.LinkedList;

public class MoveOrderer {

    private LinkedList<Move> moves;
    private Board0x88 board;

    public MoveOrderer(LinkedList<Move> moves, Board0x88 board) {
        this.moves = moves;
        this.board = board;
        moves.sort(Comparator.comparing(m -> boardValueAfterMove(m, board)  * board.getPlayerToMove().getValue(), Comparator.reverseOrder()));
    }

    public Move getNextMove() {
        return moves.pop();
    }

    public boolean isEmpty() {
        return moves.size() == 0;
    }

    private static int boardValueAfterMove(Move move, Board0x88 board) {
        board.executeMove(move);
        int value = board.getValue();
        board.executeInvertedMove(move);

        return value;
    }
}
