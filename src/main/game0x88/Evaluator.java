package main.game0x88;

import main.game.Player;

import java.util.Comparator;
import java.util.List;

public class Evaluator {

    private static long sortingTime;
    private static long moveGenTime;
    private static long evalTime;

    private static int searchDepth = 5;

    private static boolean useHash;
    private static int counter;
    private static Move bestMove;

    private static TranspositionTable transpositionTable = new TranspositionTable();

    public static Move findBestMove(Board0x88 board) {

        bestMove= null;
        useHash = true;
        counter = 0;
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
        float evalsPerSecond = ((float)counter/totalTime) * 1000;

        System.out.println(counter + " moves calculated in " + totalTime + "ms. Evaluations per second: " + evalsPerSecond);
        System.out.println("Best move: " + bestMove + ", value: " + value);
        System.out.println("Sorting time: " + sortingTime);
        System.out.println("MoveGen time: " + moveGenTime);
        System.out.println("Eval time: " + evalTime);


        return bestMove;
    }

    private static int minMax(int alpha, int beta, int depth,Board0x88 board, Player player) {
        counter++;

        NodeType nodeType = NodeType.ALPHA;

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
                if (lookUpState.score >= beta ) {
                    return beta;
                }
            }
        }

        //Move generation
        long time = System.currentTimeMillis();
        List<Move> moves = board.getAvailableMoves( false);
        moveGenTime += System.currentTimeMillis() - time;

        //Mate or stalemate if no moves
        //TODO
//        if (moves.isEmpty()) {
//            //Min value if check
//            int value = isChecked(player, board) ? Integer.MIN_VALUE + 1 : 0;
//            value *= player.getValue();
//            transpositionTable.saveState(board.getHash(), depth, value, null, NodeType.EXACT );
//            return value;
//        }


        if (depth <= 0) {
            //TODO: Quiescence Search
            time = System.currentTimeMillis();
            int value = board.getValue() * player.getValue();
            evalTime += System.currentTimeMillis() - time;

            transpositionTable.saveState(board.getHash(), depth, value, null, NodeType.EXACT );
            return value;
        }
        int maxValue = Integer.MIN_VALUE;
        Move maxEvalMove = null;



        //Sort moves by heuristic value to increase pruning
        time = System.currentTimeMillis();
        moves.sort(Comparator.comparing(m -> boardValueAfterMove(m, board)  * player.getValue(), Comparator.reverseOrder()));
        sortingTime += System.currentTimeMillis() - time;

        //Find best move
        for(Move move : moves) {

            board.executeMove(move);
            int value = -minMax(-beta, -alpha, depth-1, board, player.getOpponent());
            board.executeInvertedMove(move);

            if (value > maxValue) {
                maxValue = value;
                maxEvalMove = move;
            }

            if (value > alpha) {
                alpha = maxValue;
                nodeType = NodeType.EXACT;
            }

//            String spaces = "|";
//            for (int i = 0; i < 5-depth; i++) {
//                spaces += "--";
//            }
//            if (alpha >= beta) {
//                System.out.println(spaces + move + " | " + value + " | alpha=" + alpha + " beta=" + beta + " PRUNE");
//            } else {
//                System.out.println(spaces + move + " | " + value + " | alpha=" + alpha + " beta=" + beta);
//            }

            if (value >= beta) {
                bestMove = maxEvalMove;
                transpositionTable.saveState(board.getHash(), depth, beta, bestMove, NodeType.BETA);
                return beta;
//                break;
            }
        }

        transpositionTable.saveState(board.getHash(), depth, maxValue, maxEvalMove, nodeType);
        bestMove = maxEvalMove;
        return maxValue;
    }

    private static int boardValueAfterMove(Move move, Board0x88 board) {
        board.executeMove(move);
        int value = board.getValue();
        board.executeInvertedMove(move);

        return value;
    }


    public static void perft(Board0x88 board, int depth, Player player) {
        long time = System.currentTimeMillis();
        long calculations = perftRecursive(board, depth, player);

        long evalTime = System.currentTimeMillis() - time;
        float evalsPerSecond = ((float)calculations/evalTime) * 1000;

        System.out.println(calculations + " moves calculated in " + evalTime + "ms. Evaluations per second: " + evalsPerSecond);
    }

    public static long perftRecursive(Board0x88 board, int depth, Player player) {
        if (depth == 0) return 1;
        int nodes = 0;
        List<Move> moves = board.getAvailableMoves(false);
        for (Move move : moves) {
            board.executeMove(move);
            nodes += perftRecursive(board, depth -1, player.getOpponent());
            board.executeInvertedMove(move);
        }

        return nodes;
    }

}
