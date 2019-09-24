package game0x88;

import java.util.LinkedList;

public class MoveList {

    private LinkedList<Move> promotingMoves = new LinkedList<>();
    private LinkedList<Move> capturingMoves = new LinkedList<>();
    private LinkedList<Move> quietMoves = new LinkedList<>();

    public MoveList() {}

    public void addPromoMove(Move move) {
        promotingMoves.add(move);
    }

    public void addCapturingMove(Move move) {
        capturingMoves.add(move);
    }

    public void addQuietMove(Move move) {
        quietMoves.add(move);
    }

    public int size() {
        return promotingMoves.size() + capturingMoves.size() + quietMoves.size();
    }

}
