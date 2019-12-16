package game;

import java.util.*;
import java.util.function.Consumer;

import static game.Pieces.*;

public class MoveList implements Iterable<Move> {


    private static int MVV_LVA_SCORES[][] = {
        //      E, P, N, B, R, Q, K, p, n, b, r, q, k
        /*E*/ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*P*/ { 0, 0, 0, 0, 0, 0, 0,30,24,18,12, 6, 0},
        /*N*/ { 0, 0, 0, 0, 0, 0, 0,31,25,19,13, 7, 1},
        /*B*/ { 0, 0, 0, 0, 0, 0, 0,32,26,20,14, 8, 2},
        /*R*/ { 0, 0, 0, 0, 0, 0, 0,33,27,21,15, 9, 3},
        /*Q*/ { 0, 0, 0, 0, 0, 0, 0,34,28,22,16, 10,4},
        /*K*/ { 0, 0, 0, 0, 0, 0, 0,35,29,23,17, 11,5},
        /*p*/ { 0,30,24,18,12, 6, 0, 0, 0, 0, 0, 0, 0},
        /*n*/ { 0,31,25,19,13, 7, 1, 0, 0, 0, 0, 0, 0},
        /*b*/ { 0,32,26,20,14, 8, 2, 0, 0, 0, 0, 0, 0},
        /*r*/ { 0,33,27,21,15, 9, 3, 0, 0, 0, 0, 0, 0},
        /*q*/ { 0,34,28,22,16, 10,4, 0, 0, 0, 0, 0, 0},
        /*k*/ { 0,35,29,23,17, 11,5, 0, 0, 0, 0, 0, 0},
    };


    private LinkedList<Move> promotingMoves = new LinkedList<>();
    private LinkedList<Move> capturingMoves = new LinkedList<>();
    private LinkedList<Move> quietMoves = new LinkedList<>();
    private Move[][] killerMoves;
    private int[][][] historyMoves;
    private Move cacheMove;

    private Board board;

    boolean promotingMovesSorted = true, capturingMovesSorted = true, quietMovesSorted = true;

    public MoveList() {
    }

    public void prepare(Board board, TranspositionTable transpositionTable, Move[][] killerMoves, int[][][] historyMoves) {
        State cachedState = transpositionTable.lookup(board.getHash());
        if (cachedState != null) {
            this.cacheMove = cachedState.bestMove;
            promotingMoves.remove(cacheMove);
            capturingMoves.remove(cacheMove);
            quietMoves.remove(cacheMove);
        }
        this.killerMoves = killerMoves;
        this.historyMoves = historyMoves;
        prepare(board);
    }

    public void prepare(Board board) {
        this.board = board;
        this.promotingMovesSorted = false;
        this.capturingMovesSorted = false;
        this.quietMovesSorted = false;
    }

    public void add(Move move, MoveType moveType) {
        if (moveType == MoveType.QUIET) {
            quietMoves.add(move);
        } else if (moveType == MoveType.CAPTURING) {
            capturingMoves.add(move);
        } else if (moveType == MoveType.PROMOTING) {
            promotingMoves.add(move);
        }
    }

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
        int size = promotingMoves.size() + capturingMoves.size() + quietMoves.size();
        size = size + (cacheMove == null ? 0 : 1);
        return size;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public Move getNextMove() {
        if (cacheMove != null) {
            Move cacheMoveTemp = cacheMove;
            cacheMove = null;
            return cacheMoveTemp;
        }

        if (promotingMoves.size() != 0) {
            if (!promotingMovesSorted) {
                promotingMoves.sort(Comparator.comparing(m -> PIECE_VALUES[m.getPromotingPiece()]*board.getPlayerToMove().getValue() + (PIECE_VALUES[m.getCapturedPiece()]*board.getPlayerToMove().getValue()), Comparator.reverseOrder()));
//                promotingMoves.stream().filter(m -> m.getPromotingPiece() == WHITE_QUEEN || m.getPromotingPiece() == BLACK_QUEEN);
                promotingMovesSorted = true;
            }
            return promotingMoves.pop();
        } else if (capturingMoves.size() != 0) {
            if (!capturingMovesSorted) {
                capturingMoves.sort(Comparator.comparing(m -> pieceAndCapturedPieceValueDif(m), Comparator.reverseOrder()));
                capturingMovesSorted = true;
            }
            return capturingMoves.pop();
        } else {
            if (!quietMovesSorted) {
                sortQuietMoves();
                quietMovesSorted = true;
            }
            return quietMoves.pop();
        }
    }

    private void sortQuietMoves() {
        quietMoves.sort(Comparator.comparing(m -> {
            if (killerMoves != null && isKillerMove(m, board.getMoveNumber())) {
                return Integer.MAX_VALUE;
            } else {
                return boardValueAfterMove(m)  * board.getPlayerToMove().getValue();
            }
        }, Comparator.reverseOrder()));
    }

    private boolean isKillerMove(Move move, int ply) {
        for (int i = 0; i < killerMoves[ply].length; i++) {
            if (move.equals(killerMoves[ply][i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Move> iterator() {
        return new Iterator<Move>() {

            @Override
            public boolean hasNext() {
                return size() > 0;
            }

            @Override
            public Move next() {
                return getNextMove();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public void forEach(Consumer<? super Move> action) {
        Objects.requireNonNull(action);
        for (Move move : this) {
            action.accept(move);
        }
    }

    @Override
    public Spliterator<Move> spliterator() {
        return null;
    }

    //TODO use a lightweight executeMove instead (no need to update hash etc.)
    private int boardValueAfterMove(Move move) {
        board.executeMove(move);
        int value = board.getValue();
        board.executeInvertedMove(move);

        return value;
    }

    //TODO: MVV/LVA
    private int pieceAndCapturedPieceValueDif(Move move) {

        byte attacker = move.getPiece();
        byte captured = move.getCapturedPiece();

        return MVV_LVA_SCORES[captured][attacker];

//        return (-PIECE_VALUES[move.getPiece()] - PIECE_VALUES[move.getCapturedPiece()]) * board.getPlayerToMove().getValue();
    }

    public void addAll(MoveList moves) {
        for (Move move : moves.quietMoves) {
            this.quietMoves.add(move);
        }
        for (Move move : moves.capturingMoves) {
            this.capturingMoves.add(move);
        }
        for (Move move : moves.promotingMoves) {
            this.promotingMoves.add(move);
        }
    }
}

enum MoveType {
    QUIET, CAPTURING, PROMOTING;
}
