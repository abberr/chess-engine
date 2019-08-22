package game;

import piece.Player;

import java.util.List;

public class Evaluator {

    private static Move maxMove, minMove;
	
	public static void minMax(Board board, Player player, int depth) {
	    float minMax = minMax(Integer.MIN_VALUE, Integer.MAX_VALUE, depth, board, player);
		System.out.println(minMax);
        System.out.println("Max move:" + maxMove);
        System.out.println("Min move:" + minMove);
    }


    private static float minMax(float alpha, float beta, int depth, Board board, Player player) {
        //BaseCase
        if (depth <= 0) {
            return board.getValue();
        }
        else if (player == Player.WHITE) {
            float maxEval = alpha;
            Move maxEvalMove = null;
            for(Move move : board.getAvailableMoves(player, false)) {
                board.executeMove(move);
                float value = minMax(alpha, beta, depth-1, board, Player.BLACK);
                board.executeInvertedMove(move);

                if (value > maxEval) {
                    maxEval = value;
                    maxEvalMove = move;
                }
                if (maxEval > beta)
                    return beta;
            }

            maxMove = maxEvalMove;
            return maxEval;
        }
        else if (player == Player.BLACK) {
            float minEval = beta;
            Move minEvalMove = null;
            for(Move move : board.getAvailableMoves(player, false)) {
                board.executeMove(move);
                float value = minMax(alpha, beta, depth-1, board, Player.WHITE);
                board.executeInvertedMove(move);

                if (value < minEval) {
                    minEval = value;
                    minEvalMove = move;
                }
                if (minEval < alpha)
                    return alpha;
            }
            minMove = minEvalMove;
            return minEval;
        }


        return 0;
    }
	
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
	
	
}
