package game0x88;

import java.util.LinkedList;

import static game0x88.Pieces.EMPTY_SQUARE;

public class Evaluator {

    private static final int MAX_PLY = 4096;

    private static final int SEARCH_DEPTH_DEFAULT = 6;
    private static final int MATE_SCORE = Integer.MAX_VALUE - 1;

    private static final int KILLER_MOVES_TO_STORE = 2;

    private static long sortingTime;
    private static long moveGenTime;
    private static long evalTime;
    private static long quiscenceTime;

    private static int searchDepth = SEARCH_DEPTH_DEFAULT;

    private static boolean useHash;
    private static long moveCounter;
    private static long cacheHitCounter;

    private static TranspositionTable transpositionTable = new TranspositionTable();

    private static Move[][] killerMoves = new Move[MAX_PLY][KILLER_MOVES_TO_STORE];

    public static Move findBestMove(Board0x88 board) {

        transpositionTable.clear();

        useHash = true;

        int depth = 1;
        int value = 0;
        Move pvMove = null;

//        while (System.currentTimeMillis() - time < 15000) {
        while (depth <= searchDepth) {
            resetCounters();
            long time = System.currentTimeMillis();
            value = minMax(Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, depth, board, board.getPlayerToMove());
            long searchTime = System.currentTimeMillis() - time;

            pvMove = transpositionTable.lookup(board.getHash()).bestMove;

//            System.out.println("info currmove " + pvMove);
            StringBuilder sb = new StringBuilder();
            sb.append("info depth " + depth);
            sb.append(" time " + searchTime);
            sb.append(" nodes " + moveCounter);
            sb.append(" cacheHit " + cacheHitCounter);
            sb.append(" quiscenceTime " + quiscenceTime);
            sb.append(" movegenTime " + moveGenTime);
            sb.append(" evalTime " + evalTime);
            sb.append(" pv ");
            getPvMoves(board).forEach(m -> sb.append(m + " "));
            sb.append(" score cp " + value);
            System.out.println(sb);

            //Stop searching if check mate found
            if (value == Integer.MAX_VALUE - 1) {
                break;
            }

            depth++;
        }

//        long totalTime = System.currentTimeMillis() - time;
//        float evalsPerSecond = ((float) moveCounter /totalTime) * 1000;
//
//        System.out.println(moveCounter + " moves calculated in " + totalTime + "ms. Evaluations per second: " + evalsPerSecond);
//        System.out.println("Best move: " + pvMove + ", value: " + value);
//        System.out.println("Sorting time: " + sortingTime);
//        System.out.println("MoveGen time: " + moveGenTime);
//        System.out.println("Eval time: " + evalTime);

        return pvMove;
    }

//    private static void findMate(Board0x88 board, int depth) {
//        depth = depth - 2;
//        Move shortestMateMove = bestMove;
//        useHash = false;
//        while (depth > 0 ) {
//            int value = minMax(Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, depth, board, board.getPlayerToMove(), null);
//            if (value != Integer.MAX_VALUE - 1 ) {
//                break;
//            }
//            shortestMateMove = bestMove;
//            depth -= 2;
//        }
//        useHash = true;
//        bestMove = shortestMateMove;
//    }

    private static int minMax(int alpha, int beta, int depth, Board0x88 board, Player player) {
        moveCounter++;

        //Cache lookup - has this position been evaluated before?
        State lookUpState = transpositionTable.lookup(board.getHash());
        if (useHash && lookUpState != null && lookUpState.depth >= depth) {
            cacheHitCounter++;

//            bestMove = lookUpState.bestMove;        //TODO
            if (lookUpState.nodeType == NodeType.EXACT) {
                return lookUpState.score;
            } else if (lookUpState.nodeType == NodeType.ALPHA) {
                if (lookUpState.score < alpha) {
                    return alpha;
                } else {
//                    alpha = lookUpState.score;
                }
            } else if (lookUpState.nodeType == NodeType.BETA) {
                if (lookUpState.score > beta) {
                    return beta;
                } else {
//                    beta = lookUpState.score;
                }
            }

//            if (alpha >= beta) {
//                return lookUpState.score;
//            }
        }

        if (depth <= 0) {
            //Check cache
            if (lookUpState != null && lookUpState.nodeType == NodeType.EXACT) {
                return lookUpState.score;
            }

            MoveGenerator.setSearchModeQuiescence();
//            int value = board.getValue() * player.getValue();
            long time = System.currentTimeMillis();
            int value = quisence(alpha, beta, board, player);
            quiscenceTime += System.currentTimeMillis() - time;

            MoveGenerator.setSearchModeNormal();

            transpositionTable.saveState(board.getHash(), depth, value, null, NodeType.EXACT);
            return value;
        }

        //Move generation
        long time = System.currentTimeMillis();
        MoveList moves = board.getAvailableMoves(false);
        moveGenTime += System.currentTimeMillis() - time;

        //Mate or stalemate if no moves
        if (moves.isEmpty()) {
            //Min value if in check (check mate)
            int value = MoveGenerator.isInCheck(board.getSquares(), player) ? Integer.MIN_VALUE + 2 : 0;
            //set depth to +infinity since it's a terminal node anyways
            transpositionTable.saveState(board.getHash(), Integer.MAX_VALUE, value, null, NodeType.EXACT);
            return value;
        }

        NodeType nodeType = NodeType.ALPHA;
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        moves.prepare(board, transpositionTable, killerMoves);

        //Find best move
        while (!moves.isEmpty()) {
            Move move = moves.getNextMove();

            board.executeMove(move);
            bestValue = Math.max(bestValue, -minMax(-beta, -alpha, depth - 1, board, player.getOpponent()));
            board.executeInvertedMove(move);

            if (bestValue > alpha) {
                alpha = bestValue;
                nodeType = NodeType.EXACT;
                bestMove = move;
            }

            if (alpha >= beta) {
                if (move.getCapturedPiece() == EMPTY_SQUARE) {
                    storeKillerMove(move, board.getMoveNumber());
                }
                transpositionTable.saveState(board.getHash(), depth, beta, move, NodeType.BETA);
                return beta;
            }
        }
        transpositionTable.saveState(board.getHash(), depth, bestValue, bestMove, nodeType);
        return bestValue;
    }

    //TODO optimize
    private static void storeKillerMove(Move move, int ply) {
        //check if already exists
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
    }

    private static void resetCounters() {
//        bestMove= null;
        moveCounter = 0;
        sortingTime = 0;
        moveGenTime = 0;
        evalTime = 0;
        cacheHitCounter = 0;
        quiscenceTime = 0;
    }

    //https://www.chessprogramming.org/Quiescence_Search
    //Find a quiet position to evaluate
    private static int quisence(int alpha, int beta, Board0x88 board, Player player) {
        long time = System.currentTimeMillis();
        int boardValue = board.getValue() * player.getValue();
        evalTime += System.currentTimeMillis() - time;

        if (boardValue >= beta) {
            return beta;
        }
        if (alpha < boardValue) {
            alpha = boardValue;
        }

        time = System.currentTimeMillis();
        MoveList moves = board.getAvailableMoves(false);
        moveGenTime += System.currentTimeMillis() - time;

        moves.prepare(board);
        for (Move m : moves) {
            board.executeMove(m);
            int score = -quisence(-beta, -alpha, board, player.getOpponent());
            board.executeInvertedMove(m);

            if (score >= beta)
                return beta;
            if (score > alpha)
                alpha = score;
        }

        return alpha;
    }

    //Recursive
    public static LinkedList<Move> getPvMoves(Board0x88 board) {
        State state = transpositionTable.lookup(board.getHash());
        if (state == null || state.bestMove == null) {
            return new LinkedList<>();
        }
        Move bestMove = state.bestMove;
        board.executeMove(bestMove);
        LinkedList<Move> pvMoves = getPvMoves(board);
        board.executeInvertedMove(bestMove);
        pvMoves.addFirst(bestMove);

        return pvMoves;
    }

    public static void setSearchDepth(int depth) {
        searchDepth = depth;
    }
}
