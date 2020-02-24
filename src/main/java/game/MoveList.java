package game;

import java.util.*;
import java.util.function.Consumer;

public class MoveList implements Iterable<Move> {


    private static int MVV_LVA_SCORES[][] = {
            // x=Attacker y=Victim
        //      E, P, N, B, R, Q, K, p, n, b, r, q, k
        /*E*/ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*P*/ { 0, 0, 0, 0, 0, 0, 0, 5, 4, 3, 2, 1, 0},
        /*N*/ { 0, 0, 0, 0, 0, 0, 0,11,10, 9, 8, 7, 6},
        /*B*/ { 0, 0, 0, 0, 0, 0, 0,17,16,15,14,13,12},
        /*R*/ { 0, 0, 0, 0, 0, 0, 0,23,22,21,20,19,18},
        /*Q*/ { 0, 0, 0, 0, 0, 0, 0,29,28,27,26,25,24},
        /*K*/ { 0, 0, 0, 0, 0, 0, 0,35,34,33,32,31,30},
        /*p*/ { 0, 5, 4, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0},
        /*n*/ { 0,11,10, 9, 8, 7, 6, 0, 0, 0, 0, 0, 0},
        /*b*/ { 0,17,16,15,14,13,12, 0, 0, 0, 0, 0, 0},
        /*r*/ { 0,23,22,21,20,19,18, 0, 0, 0, 0, 0, 0},
        /*q*/ { 0,29,28,27,26,25,24, 0, 0, 0, 0, 0, 0},
        /*k*/ { 0,35,34,33,32,31,30, 0, 0, 0, 0, 0, 0},
    };


    private LinkedList<Move> capturingMoves = new LinkedList<>();
    private LinkedList<Move> quietMoves = new LinkedList<>();
    private Move[][] killerMoves;
    private int[][][] historyMoves;
    private Move cacheMove;
    private boolean cacheMoveAvailable;

    private Board board;

    boolean capturingMovesGenerated, quietMovesGenerated;

    public MoveList(Board board, TranspositionTable transpositionTable, Move[][] killerMoves, int[][][] historyMoves) {
        this.board = board;

        State cachedState = transpositionTable.lookup(board.getHash());
        if (cachedState != null && cachedState.bestMove != null) {
            this.cacheMove = cachedState.bestMove;
            this.cacheMoveAvailable = true;
        }

        this.killerMoves = killerMoves;
        this.historyMoves = historyMoves;
    }

    public MoveList(Board board) {
        this.board = board;
        this.cacheMoveAvailable = false;
    }

    //TODO fix or dont use
    public int size() {
        int size = capturingMoves.size() + quietMoves.size();
        size = size + (cacheMove == null ? 0 : 1);
        return size;
    }

    public boolean isEmpty() {
        return false;
    }

    public Move getNextMove() {
        if (cacheMoveAvailable) {
            cacheMoveAvailable = false;
            return cacheMove;
        }

//        if (promotingMoves.size() != 0) {
//            if (!promotingMovesSorted) {
//                //TODO: remove rook and bishop promotion
//                promotingMoves.sort(Comparator.comparing(m -> PIECE_VALUES[m.getPromotingPiece()] + PIECE_VALUES[m.getCapturedPiece()], Comparator.reverseOrder()));
////                promotingMoves.stream().filter(m -> m.getPromotingPiece() == WHITE_QUEEN || m.getPromotingPiece() == BLACK_QUEEN || m.getPromotingPiece() == WHITE_KNIGHT || m.getPromotingPiece() == BLACK_KNIGHT);
//                promotingMovesSorted = true;
//            }
//            return promotingMoves.pop();
//        }

        if (!capturingMovesGenerated) {
            this.capturingMoves = MoveGenerator.generateMoves(board, MoveType.CAPTURING);
            if (cacheMove != null) {
                capturingMoves.remove(cacheMove);
            }
            capturingMoves.sort(Comparator.comparing(m -> mvvLva(m), Comparator.reverseOrder()));
            capturingMovesGenerated = true;
        }
        if (!capturingMoves.isEmpty()) {
            return capturingMoves.pop();
        }

        if (!quietMovesGenerated) {
            this.quietMoves = MoveGenerator.generateMoves(board, MoveType.QUIET);
            if (cacheMove != null) {
                quietMoves.remove(cacheMove);
            }
            sortQuietMoves();
            quietMovesGenerated = true;
        }
        if (quietMoves.isEmpty()) return null;
        return quietMoves.pop();
    }

    public Move getNextCapturingMove() {
        if (!capturingMovesGenerated) {
            this.capturingMoves = MoveGenerator.generateMoves(board, MoveType.CAPTURING);
            if (cacheMove != null) {
                capturingMoves.remove(cacheMove);
            }
            capturingMoves.sort(Comparator.comparing(m -> mvvLva(m), Comparator.reverseOrder()));
            capturingMovesGenerated = true;
        }
        if (capturingMoves.isEmpty()) return null;
        return capturingMoves.pop();
    }

    private void sortQuietMoves() {
        quietMoves.sort(Comparator.comparing(m -> {
            if (killerMoves != null && isKillerMove(m, board.getMoveNumber())) {
                return Integer.MAX_VALUE;
            } else {
                return 0;
//                return boardValueAfterMove(m)  * board.getPlayerToMove().getValue();
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

    //TODO dont want to look if empty
    @Override
    public Iterator<Move> iterator() {
        return new Iterator<Move>() {

            @Override
            public boolean hasNext() {
                return !isEmpty();
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

    private int mvvLva(Move move) {

        byte attacker = move.getPiece();
        byte captured = move.getCapturedPiece();

        return MVV_LVA_SCORES[captured][attacker];
    }
}

enum MoveType {
    QUIET, CAPTURING, PROMOTING;
}
