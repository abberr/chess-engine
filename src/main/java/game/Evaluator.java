package game;

import java.util.LinkedList;

import static game.Pieces.EMPTY_SQUARE;
import static game.Pieces.PIECES_SIZE;
import static game.TranspositionTable.NO_HIT;

public class Evaluator {

    private static final int MAX_PLY = 4096;

    private static final int SEARCH_DEPTH_DEFAULT = 6;
    private static final int MATE_SCORE = 10000;
    private final static int DRAW_SCORE = 0;

    private static final int KILLER_MOVES_TO_STORE = 2;

    private static final int NULL_MOVE_DEPTH_REDUCE = 3;

    private static long sortingTime;
    private static long moveGenTime;
    private static long evalTime;
    private static long quiscenceTime;

    private static int searchDepth = SEARCH_DEPTH_DEFAULT;
    private static long endTime;

    private static boolean useHash;
    private static long moveCounter;
    private static long cacheHitCounter;

    private static TranspositionTable transpositionTable = new TranspositionTable();

    private static Move[][] killerMoves = new Move[MAX_PLY][KILLER_MOVES_TO_STORE];
    private static int[][][] historyMoves = new int[2][PIECES_SIZE][128];

    public static Move findBestMove(Board board, long timeToSearch) {
        endTime = System.currentTimeMillis() + timeToSearch - 50;
        searchDepth = Integer.MAX_VALUE;
        return findBestMove(board);
    }

    public static Move findBestMove(Board board, int depth) {
        searchDepth = depth;
        endTime = Long.MAX_VALUE;
        return findBestMove(board);
    }

    public static Move findBestMove(Board board) {

//        transpositionTable.clear();

        useHash = true;

        int depth = 1;
        int value = 0;

        while (System.currentTimeMillis() < endTime && depth <= searchDepth) {
            resetCounters();
            long time = System.currentTimeMillis();
            value = minMax(Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, depth, 1, board, false);   //TODO makeNullMove should be true?
            long searchTime = System.currentTimeMillis() - time;

            if (System.currentTimeMillis() > endTime)
                break;

//            System.out.println("info currmove " + pvMove);
            StringBuilder sb = new StringBuilder();
            sb.append("info depth " + depth);
            sb.append(" time " + searchTime);
            sb.append(" nodes " + moveCounter);
            sb.append(" cacheHit " + cacheHitCounter);
            sb.append(" quiscenceTime " + quiscenceTime);
            sb.append(" movegenTime " + moveGenTime);
            sb.append(" sortingTime " + sortingTime);
            sb.append(" evalTime " + evalTime);
            sb.append(" pv ");
            getPvMoves(board, depth).forEach(m -> sb.append(m + " "));
            sb.append(" score cp " + value);

            //Stop searching if check mate found
            if (value >= MATE_SCORE - MAX_PLY) {
                sb.append(" mate " + (10000-value)/2);
                System.out.println(sb);
                break;
            }
            System.out.println(sb);
            depth++;
        }

        return getPvMoves(board, depth).getFirst();
    }

    private static int minMax(int alpha, int beta, int depth, int ply, Board board, boolean makeNullMove) {

        int cacheScore = transpositionTable.lookup(board.getHash(), depth, alpha, beta);
        if (cacheScore != NO_HIT) {
            cacheHitCounter++;
            return cacheScore;
        }

        moveCounter++;

        // Quiscence search when reaching a leaf node
        if (depth <= 0) {

            MoveGenerator.setSearchModeQuiescence();
            long time = System.currentTimeMillis();
//            int value = board.getValue() * player.getValue();
            int value = quisence(alpha, beta, board);
            quiscenceTime += System.currentTimeMillis() - time;

            MoveGenerator.setSearchModeNormal();

            //Dont need to save state since we wont lookup at depth 0 anyways
//            transpositionTable.saveState(board.getHash(), depth, value, null, NodeType.EXACT);
            return value;
        }

        //Null move
        //TODO dont do null moves in endgame
        if (makeNullMove && depth >= NULL_MOVE_DEPTH_REDUCE && !board.isInCheck(board.getPlayerToMove())) {
            board.executeNullMove();
            int value = -minMax(-beta, -beta + 1, depth - NULL_MOVE_DEPTH_REDUCE, ply + 1, board,false);
            board.executeInvertedNullMove();

            if (value >= beta) {
                return beta;
            }
        }


        //Move generation
        long time = System.currentTimeMillis();
        MoveList moves = board.getAvailableMoves();
        moveGenTime += System.currentTimeMillis() - time;

        NodeType nodeType = NodeType.ALPHA;
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        moves.prepare(board, transpositionTable, killerMoves, historyMoves);

        //Find best move
        int nodesSearched = 0;
        while (!moves.isEmpty()) {

            time = System.currentTimeMillis();
            Move move = moves.getNextMove();
            sortingTime += System.currentTimeMillis() - time;

            board.executeMove(move);
            if (board.isInCheck(board.getPlayerToMove().getOpponent())) {
                board.executeInvertedMove(move);
                continue;
            }
            nodesSearched++;

            if (board.isRepetition()) {
                bestValue = DRAW_SCORE;
            } else {
                bestValue = Math.max(bestValue, -minMax(-beta, -alpha, depth - 1, ply + 1, board, true));
                if (System.currentTimeMillis() > endTime) {
                    board.executeInvertedMove(move);
                    return bestValue;
                }
            }
            board.executeInvertedMove(move);

            if (bestValue > alpha) {
                alpha = bestValue;
                nodeType = NodeType.EXACT;
                bestMove = move;

                if (alpha >= beta) {
                    if (move.getCapturedPiece() == EMPTY_SQUARE) {
                        storeKillerMove(move, board.getMoveNumber());
                        historyMoves[board.getPlayerToMove().getHashValue()][move.getPiece()][move.getMoveTo()] += depth*depth;
                    }
                    transpositionTable.saveState(board.getHash(), depth, beta, move, NodeType.BETA);
                    return beta;
                }
            }
        }

        //Mate or stalemate if no moves
        if (nodesSearched == 0 ) {
            //Min value if in check (check mate)
            int value = board.isInCheck(board.getPlayerToMove()) ? -(MATE_SCORE - ply) : 0;
            //set depth to MAX_PLY since it's a terminal node anyways
            transpositionTable.saveState(board.getHash(), MAX_PLY, value, null, NodeType.EXACT);
            return value;
        }

        transpositionTable.saveState(board.getHash(), depth, bestValue, bestMove, nodeType);
        return bestValue;
    }

    //TODO optimize
    private static void storeKillerMove(Move move, int ply) {
        //check if move already is killer move
        for (int i = 0; i < KILLER_MOVES_TO_STORE; i++) {
            if (move.equals(killerMoves[ply][i])) {
                return;
            }
        }
        //Shift right
        for (int i = KILLER_MOVES_TO_STORE - 2; i >= 0; i--) {
            killerMoves[ply][i + 1] = killerMoves[ply][i];
        }
        killerMoves[ply][0] = move;
    }

    public static void reset() {
        killerMoves = new Move[MAX_PLY][KILLER_MOVES_TO_STORE];
        historyMoves = new int[2][PIECES_SIZE][128];
        transpositionTable = new TranspositionTable();
    }

    private static void resetCounters() {
        moveCounter = 0;
        sortingTime = 0;
        moveGenTime = 0;
        evalTime = 0;
        cacheHitCounter = 0;
        quiscenceTime = 0;
    }

    //https://www.chessprogramming.org/Quiescence_Search
    //Find a quiet position to evaluate
    private static int quisence(int alpha, int beta, Board board) {
        long time = System.currentTimeMillis();
        int boardValue = board.getValue() * board.getPlayerToMove().getValue();
        evalTime += System.currentTimeMillis() - time;

        if (boardValue >= beta) {
            return beta;
        }
        if (alpha < boardValue) {
            alpha = boardValue;
        }

        time = System.currentTimeMillis();
        MoveList moves = board.getAvailableMoves();
        moveGenTime += System.currentTimeMillis() - time;

        moves.prepare(board);
        for (Move m : moves) {
            board.executeMove(m);
            if (board.isInCheck(board.getPlayerToMove().getOpponent())) {
                board.executeInvertedMove(m);
                continue;
            }

            int score = -quisence(-beta, -alpha, board);
            board.executeInvertedMove(m);

            if (score >= beta)
                return beta;
            if (score > alpha)
                alpha = score;
        }

        return alpha;
    }

    //Recursive
    public static LinkedList<Move> getPvMoves(Board board, int depth) {
        State state = transpositionTable.lookup(board.getHash());
        if (state == null || state.bestMove == null || depth == 0) {
            return new LinkedList<>();
        }
        Move bestMove = state.bestMove;
        board.executeMove(bestMove);
        LinkedList<Move> pvMoves = getPvMoves(board, depth - 1);
        board.executeInvertedMove(bestMove);
        pvMoves.addFirst(bestMove);

        return pvMoves;
    }}
