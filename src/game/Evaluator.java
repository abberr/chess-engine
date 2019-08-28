package game;

import java.util.List;

public class Evaluator {

    private static int counter;
    private static Move bestMove;
	
	public static Move[] minMax(Board board, Player player, int depth) {

	    Move [] bestMoves = new Move[depth];

        long time = System.currentTimeMillis();
        counter = 0;
	    float minMax = minMax(Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, depth, board, player, bestMoves);

	    long evalTime = System.currentTimeMillis() - time;
	    float evalsPerSecond = ((float)counter/evalTime) * 1000;

        System.out.println(counter + " moves calculated. Time: " + evalTime + "ms. Evaluations per second: " + evalsPerSecond);
        System.out.println("Best move: " + bestMove);

//        System.out.println();
//        for (Move m: bestMoves) {
//            System.out.print(m + ", ");
//        }
//        System.out.println("maxnMove " + maxMove);
//        System.out.println("minMove " + minMove);
		System.out.println(minMax);


		return bestMoves;
    }

    private static int minMax(int alpha, int beta, int depth, Board board, Player player, Move [] bestMoves) {
        counter++;

        if (depth <= 0) {
            return board.getValue() * player.getValue();
        }
        int maxValue = Integer.MIN_VALUE;
        Move maxEvalMove = null;

        //TODO: Sort moves by heuristic value to increase pruning
        List<Move> moves = board.getAvailableMoves(player, false);
//        moves.sort(Comparator.comparing(m -> boardValueAfterMove(m, board)  * player.getValue()));
//        moves.sort(Comparator.comparing(m -> boardValueAfterMove(m, board)  * player.getValue(), Comparator.reverseOrder()));

        for(Move move : moves) {
            
            board.executeMove(move);
            int value = -minMax(-beta, -alpha, depth-1, board, player.getOpponent(), bestMoves);
            board.executeInvertedMove(move);

            if (value > maxValue) {
                maxValue = value;
                maxEvalMove = move;
            }

            if (maxValue > alpha) {
                alpha = maxValue;
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

            if (alpha >= beta) {
//                System.out.print(" P");
                break;
            }
        }
        bestMoves[bestMoves.length-depth] = maxEvalMove;

        bestMove = maxEvalMove;
        return maxValue;
    }

    public static int perft(Board board, int depth, Player player) {
        if (depth == 0) return 1;
        int nodes = 0;
        List<Move> moves = board.getAvailableMoves(player, false);
        for (Move move : moves) {
            board.executeMove(move);
            nodes += perft(board, depth -1, player.getOpponent());
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
	    Player opponentPlayer = Player.WHITE;
		if (player == Player.WHITE) {
            opponentPlayer = Player.BLACK;
		}

		Position kingPos = board.getPositionOfKing(player);

		List<Move> moves = board.getAvailableMoves(opponentPlayer, true);
		//Check if opposing player can take king
		for (Move m  : moves) {
			if (m.moveTo.equals(kingPos)) {
				return true;
			}
		}
		return false;
	}

	private static float boardValueAfterMove(Move move, Board board) {
        board.executeMove(move);
        float value = board.getValue();
        board.executeInvertedMove(move);

        return value;
    }
	
	
}
