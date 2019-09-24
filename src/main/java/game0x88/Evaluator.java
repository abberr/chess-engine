package game0x88;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Evaluator {

    private static final int SEARCH_DEPTH_DEFAULT = 6;

    private static long sortingTime;
    private static long moveGenTime;
    private static long evalTime;

    private static int searchDepth = SEARCH_DEPTH_DEFAULT;

    private static boolean useHash;
    private static long moveCounter;
    private static Move bestMove;

    private static TranspositionTable transpositionTable = new TranspositionTable();

    //PV list
    private static Move[] pv = new Move[SEARCH_DEPTH_DEFAULT ];

    public static Move findBestMove(Board0x88 board) {

        useHash = true;

        bestMove= null;
        moveCounter = 0;
        sortingTime = 0;
        moveGenTime = 0;
        evalTime = 0;

        long time = System.currentTimeMillis();

        int value = minMax(Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, searchDepth, board, board.getPlayerToMove());

        //If forced mate is found, try to find mate in less moves
        if (value == Integer.MAX_VALUE - 1) {
            System.out.println("MATE FOUND");

            int depth = searchDepth - 2;
            Move shortestMateMove = bestMove;
            useHash = false;
            while (depth > 0 ) {
                value = minMax(Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, depth, board, board.getPlayerToMove());
                if (value != Integer.MAX_VALUE - 1 ) {
                    break;
                }
                shortestMateMove = bestMove;
                depth -= 2;
            }

            bestMove = shortestMateMove;
        }

        long totalTime = System.currentTimeMillis() - time;
        float evalsPerSecond = ((float) moveCounter /totalTime) * 1000;

        System.out.println(moveCounter + " moves calculated in " + totalTime + "ms. Evaluations per second: " + evalsPerSecond);
        System.out.println("Best move: " + bestMove + ", value: " + value);
        System.out.println("Sorting time: " + sortingTime);
        System.out.println("MoveGen time: " + moveGenTime);
        System.out.println("Eval time: " + evalTime);

//        System.out.println();
//        for(Move m : pv) {
//            System.out.print(m + ", ");
//        }

        return bestMove;
    }

    private static int minMax(int alpha, int beta, int depth,Board0x88 board, Player player) {
        moveCounter++;

        NodeType nodeType = NodeType.ALPHA;

        //Cache lookup - has this position been evaluated before?
        State lookUpState = transpositionTable.lookup(board.getHash(), depth, alpha, beta);
        if (useHash && lookUpState != null) {
            bestMove = lookUpState.bestMove;        //TODO
            if (lookUpState.nodeType == NodeType.EXACT) {
                return lookUpState.score;
            }
            else if (lookUpState.nodeType == NodeType.ALPHA) {
                if (lookUpState.score <= alpha ) {
                    return alpha;
                }
            }

            else if (lookUpState.nodeType == NodeType.BETA) {
                if (lookUpState.score >= beta) {
                    return beta;
                }
            }
        }

        if (depth <= 0) {
            MoveGenerator.setSearchModeQuiescence();
//            int value = board.getValue() * player.getValue();
            int value = quisence(alpha, beta, board, player);
            MoveGenerator.setSearchModeNormal();

            transpositionTable.saveState(board.getHash(), depth, value, null, NodeType.EXACT );
            return value;
        }

        //Move generation
        long time = System.currentTimeMillis();
        LinkedList<Move> moves = board.getAvailableMoves( false);
        moveGenTime += System.currentTimeMillis() - time;

        //Mate or stalemate if no moves
        if (moves.isEmpty()) {
            //Min value if check
            int value = MoveGenerator.isInCheck(board.getSquares(), player) ? Integer.MIN_VALUE + 2 : 0;
            //TODO set depth to +infinity? Since it's a terminal node anyways
            transpositionTable.saveState(board.getHash(), Integer.MAX_VALUE, value, null, NodeType.EXACT );
            return value;
        }

        int maxValue = Integer.MIN_VALUE;
        Move maxEvalMove = null;

        //Sort moves by heuristic value to increase pruning
        time = System.currentTimeMillis();
//        moves.sort(Comparator.comparing(m -> boardValueAfterMove(m, board)  * player.getValue(), Comparator.reverseOrder()));
        MoveOrderer mo = new MoveOrderer(moves, board);
        sortingTime += System.currentTimeMillis() - time;

        int value = Integer.MIN_VALUE;



        //Find best move
        while (!mo.isEmpty()){
            Move move = mo.getNextMove();

            board.executeMove(move);
            value = Math.max(value, -minMax(-beta, -alpha, depth-1, board, player.getOpponent()));
            board.executeInvertedMove(move);

            if (value > alpha) {
                alpha = value;
                nodeType = NodeType.EXACT;
                maxEvalMove = move;
            }

            if (alpha >= beta) {
                transpositionTable.saveState(board.getHash(), depth, beta, bestMove, NodeType.BETA);
                return beta;
            }
        }

        transpositionTable.saveState(board.getHash(), depth, maxValue, maxEvalMove, nodeType);
        bestMove = maxEvalMove;
        pv[pv.length-depth] = maxEvalMove;
        return value;
    }

    private static int boardValueAfterMove(Move move, Board0x88 board) {
        board.executeMove(move);
        int value = board.getValue();
        board.executeInvertedMove(move);

        return value;
    }

    //https://www.chessprogramming.org/Quiescence_Search
    //Find a quiet position to evaluate
    //TODO needs to faster, maybe use hashing?
    private static int quisence(int alpha, int beta, Board0x88 board, Player player) {
        long time = System.currentTimeMillis();
        int stand_pat = board.getValue() * player.getValue();
        evalTime += System.currentTimeMillis() - time;

        if( stand_pat >= beta ) {
            return beta;
        }
        if( alpha < stand_pat ) {
            alpha = stand_pat;
        }

        time = System.currentTimeMillis();
        List<Move> moves = board.getAvailableMoves(false);
        moveGenTime += System.currentTimeMillis() - time;


        time = System.currentTimeMillis();
        moves.sort(Comparator.comparing(m -> boardValueAfterMove(m, board)  * player.getValue(), Comparator.reverseOrder()));
        sortingTime += System.currentTimeMillis() - time;

        for (Move m : moves) {
            board.executeMove(m);
            int score = -quisence(-beta, -alpha, board, player.getOpponent());
            board.executeInvertedMove(m);

            if( score >= beta )
                return beta;
            if( score > alpha )
                alpha = score;
        }

        return alpha;
    }

    public static void setSearchDepth(int depth) {
        searchDepth = depth;
    }
}
