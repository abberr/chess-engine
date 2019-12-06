package game;

import java.util.HashMap;

public class TranspositionTable {

    private static final int TABLE_SIZE = 0xFFFF;

    HashMap<Long, State> hashMap;

    public TranspositionTable() {
        hashMap = new HashMap<>(TABLE_SIZE);
    }


    public State lookup(long hash) {
        State state = hashMap.get(hash % TABLE_SIZE);
        if (state != null && state.hash == hash) {
            return state;
        }

        return null;
    }

    //TODO add replacement scheme
    public void saveState(long hash, int depth, int value, Move bestMove, NodeType nodeType) {

        State existingEntry = hashMap.get(hash % TABLE_SIZE);
        if (existingEntry != null) {
            //TODO >= or > ?
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


