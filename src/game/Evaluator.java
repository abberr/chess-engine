package game;

import piece.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Evaluator {
	
	public static Move[] minMax(Board board, Player player, int depth) {

	    Move [] bestMoves = new Move[depth];

        long time = System.currentTimeMillis();
	    float minMax = minMax(Integer.MIN_VALUE, Integer.MAX_VALUE, depth, board, player, bestMoves);
        System.out.println("Calculating time: " + (System.currentTimeMillis() - time) + "ms");

        System.out.println();
        for (Move m: bestMoves) {
            System.out.print(m + ", ");
        }
//        System.out.println("maxnMove " + maxMove);
//        System.out.println("minMove " + minMove);

		System.out.println(minMax);
		return bestMoves;
    }

    private static float minMax(float alpha, float beta, int depth, Board board, Player player, Move [] bestMoves) {
        //BaseCase
        if (depth <= 0) {
            return board.getValue() * player.getValue();
        }
        float maxValue = Integer.MIN_VALUE;
        Move maxEvalMove = null;

        //Sort moves by heuristic value
        List<Move> moves = board.getAvailableMoves(player, false);
//        moves.sort(Comparator.comparing(m -> boardValueAfterMove(m, board)  * player.getValue()));
//        moves.sort(Comparator.comparing(m -> boardValueAfterMove(m, board)  * player.getValue(), Comparator.reverseOrder()));

        for(Move move : moves) {
            board.executeMove(move);
            float value = -minMax(-beta, -alpha, depth-1, board, player.getOpponent(), bestMoves);
            board.executeInvertedMove(move);

            if (value > maxValue) {
                maxValue = value;
                maxEvalMove = move;
            }

            if (maxValue > alpha) {
                alpha = maxValue;
            }

            if (alpha >= beta) {
//                System.out.print("P");
                break;
            }
        }
        bestMoves[bestMoves.length-depth] = maxEvalMove;
        return maxValue;

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
