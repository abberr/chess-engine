package game;

import java.util.HashMap;

public class TranspositionTable {

    //0xFFFFFF = 2MB
    private static final int TABLE_SIZE = 0xFFFFFF;

    static final int NO_HIT = 12345;

    HashMap<Long, State> hashMap;

    public TranspositionTable() {
        hashMap = new HashMap<>();
    }


    public int lookup(long hash, int depth, int alpha, int beta) {

        if (depth != 0) {
            State state = hashMap.get(hash % TABLE_SIZE);

            if (state != null && state.hash == hash && state.depth >= depth) {
                if (state.nodeType == NodeType.EXACT) {
                    return state.score;
                } else if (state.nodeType == NodeType.ALPHA) {
                    if (state.score <= alpha) {
                        return alpha;
                    }
                } else if (state.nodeType == NodeType.BETA) {
                    if (state.score >= beta) {
                        return beta;
                    }
                }
            }
        }

        return NO_HIT;
    }

    public State lookup(long hash) {
        State state = hashMap.get(hash % TABLE_SIZE);
        if (state != null && state.hash == hash) {
            return state;
        }

        return null;
    }

    public void saveState(long hash, int depth, int value, Move bestMove, NodeType nodeType) {

        State existingEntry = hashMap.get(hash % TABLE_SIZE);
        if (existingEntry != null) {

            if (existingEntry.depth > depth) {
                return;
            }
        }

        State state = new State(hash, depth, value, bestMove, nodeType);
        hashMap.put((hash % TABLE_SIZE), state);
    }
}

enum NodeType {
    EXACT,     //Exact score
    ALPHA,    //Score is lower bound
    BETA    //Score is upper bound
}

class State {

    public long hash;
    public int depth;
    public int score;
    public Move bestMove;
    public NodeType nodeType;

    public State(long hash, int depth, int score, Move bestMove, NodeType nodeType) {
        this.hash = hash;
        this.depth = depth;
        this.score = score;
        this.bestMove = bestMove;
        this.nodeType = nodeType;
    }

}


