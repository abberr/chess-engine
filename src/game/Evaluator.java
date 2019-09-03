package game;

import java.util.Comparator;
import java.util.List;

public class Evaluator {

    private static int counter;
    private static int startingDepth;
    private static Move bestMove;

    private static TranspositionTable transpositionTable = new TranspositionTable();

	
	public static Move minMax(Board board, int depth) {

	    startingDepth = depth;
        bestMove= null;
        counter = 0;

        long time = System.currentTimeMillis();
	    int minMax = minMax(Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, startingDepth, board, board.getPlayerToMove());

	    long evalTime = System.currentTimeMillis() - time;
	    float evalsPerSecond = ((float)counter/evalTime) * 1000;

        System.out.println(counter + " moves calculated. Time: " + evalTime + "ms. Evaluations per second: " + evalsPerSecond);
        System.out.println("Best move: " + bestMove);
		System.out.println(minMax);


		return bestMove;
    }

    private static int minMax(int alpha, int beta, int depth, Board board, Player player) {
        counter++;

        NodeType nodeType = NodeType.ALPHA;

        //TODO: Transposition table lookup
//        State lookUpState = transpositionTable.lookup(board.getHash(), depth, alpha, beta);
//        if (lookUpState != null) {
//
//            if (lookUpState.nodeType == NodeType.EXACT) {
//                return lookUpState.score;
//            }
//            else if (lookUpState.nodeType == NodeType.ALPHA) {
//                if (lookUpState.score <= alpha ) {
//                    return alpha;
//                }
//            }
//
//            else if (lookUpState.nodeType == NodeType.BETA) {
//                if (lookUpState.score >= beta ) {
//                    return beta;
//                }
//            }
//
//            else {
//                System.out.println("ERROR?");
//            }
//        }

        List<Move> moves = board.getAvailableMoves(player, false);

        //Mate or stalemate if no moves
        if (moves.isEmpty()) {
            //Stalemate = max value?
            if (isChecked(player, board)) {
                int value = (Integer.MIN_VALUE+1) * player.getValue();
                transpositionTable.saveState(board.getHash(), depth, value, null, NodeType.EXACT );
                return value;
            }
            //Mate
            else {
                int value = (Integer.MAX_VALUE-1) * player.getValue();
                transpositionTable.saveState(board.getHash(), depth, value, null, NodeType.EXACT );
                return value;
            }
        }


        if (depth <= 0) {
            //TODO: Quiescence Search
            int value = board.getValue() * player.getValue();
            transpositionTable.saveState(board.getHash(), depth, value, null, NodeType.EXACT );
            return value;
        }
        int maxValue = Integer.MIN_VALUE;
        Move maxEvalMove = null;



        //Sort moves by heuristic value to increase pruning
        moves.sort(Comparator.comparing(m -> boardValueAfterMove(m, board)  * player.getValue(), Comparator.reverseOrder()));

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
                transpositionTable.saveState(board.getHash(), depth, beta, null, NodeType.BETA);
                return beta;
//                break;
            }
        }


        //TODO: Transposition table insert
        transpositionTable.saveState(board.getHash(), depth, maxValue, maxEvalMove, nodeType);
        bestMove = maxEvalMove;
        return maxValue;
    }

    public static void perft(Board board, int depth, Player player) {
        long time = System.currentTimeMillis();
        long calculations = perftRecursive(board, depth, player);

        long evalTime = System.currentTimeMillis() - time;
        float evalsPerSecond = ((float)calculations/evalTime) * 1000;

        System.out.println(calculations + " moves calculated. Time: " + evalTime + "ms. Evaluations per second: " + evalsPerSecond);
    }

    public static long perftRecursive(Board board, int depth, Player player) {
        if (depth == 0) return 1;
        int nodes = 0;
        List<Move> moves = board.getAvailableMoves(player, false);
        for (Move move : moves) {
            board.executeMove(move);
            nodes += perftRecursive(board, depth -1, player.getOpponent());
            board.executeInvertedMove(move);
        }

        return nodes;
    }

//
//    private static float minMax(float alpha, float beta, int depth, Board board, Player player, Move [] bestMoves) {
//        //BaseCase
//        if (depth <= 0) {
//            return board.getValue();
//        }
//        else if (player == Player.WHITE) {
//            float maxEval = alpha;
//            Move maxEvalMove = null;
//
//            //Sort moves by heuristic value
//            List<Move> moves = board.getAvailableMoves(player, false);
//            moves.sort(Comparator.comparing(m -> boardValueAfterMove(m, board)));
//
//            for(Move move : moves) {
//                board.executeMove(move);
//                float value = minMax(alpha, beta, depth-1, board, Player.BLACK, bestMoves);
//                board.executeInvertedMove(move);
//
//                //TODO förenkla, typ såhär https://stackoverflow.com/questions/9964496/alpha-beta-move-ordering
//                if (value > maxEval) {
//                    maxEval = value;
//                    maxEvalMove = move;
//                }
//                if (alpha > maxEval) {
//                    alpha = maxEval;
//                }
//                if (beta <= alpha) {
//                    System.out.println("PRUNE");
//                    break;
//                }
//            }
//
//            maxMove = maxEvalMove;
//            bestMoves[bestMoves.length-depth] = maxEvalMove;
//
//            return maxEval;
//        }
//        else if (player == Player.BLACK) {
//            float minEval = beta;
//            Move minEvalMove = null;
//            for(Move move : board.getAvailableMoves(player, false)) {
//                board.executeMove(move);
//                float value = minMax(alpha, beta, depth-1, board, Player.WHITE, bestMoves);
//                board.executeInvertedMove(move);
//
//                //TODO förenkla
//                if (value < minEval) {
//                    minEval = value;
//                    minEvalMove = move;
//                }
//                if (beta < minEval) {
//                    beta = minEval;
//                }
//                if (beta <= alpha) {
//                    System.out.println("PRUNE");
//                    break;
//                }
//            }
//            minMove = minEvalMove;
//            bestMoves[bestMoves.length-depth] = minEvalMove;
//
//            return minEval;
//        }
//
//
//        return 0;
//    }


	
	public static boolean isChecked(Player player, Board board) {
		Position kingPos = board.getPositionOfKing(player);
		return board.isSquareUnderAttack(kingPos, player);
	}

	private static float boardValueAfterMove(Move move, Board board) {
        board.executeMove(move);
        float value = board.getValue();
        board.executeInvertedMove(move);

        return value;
    }
	
	
}
