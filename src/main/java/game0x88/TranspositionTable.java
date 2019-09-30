package game0x88;

import java.util.HashMap;

public class TranspositionTable {

    HashMap<Long, State> hashMap;

    public TranspositionTable() {
        hashMap = new HashMap<>();
    }


    public State lookup(long hash) {
        State state = hashMap.get(hash & 0xFFFF);
        if (state != null && state.hash == hash) {
            return state;
        }

        return null;
    }

    public boolean exists(long hash) {
        return hashMap.containsValue(hash & 0xFFFF) && hashMap.get(hash & 0xFFFF).hash == hash;
    }

    //TODO add replacement scheme
    public void saveState(long hash, int depth, int value, Move bestMove, NodeType nodeType) {
//        if (!exists(hash) || depth >= lookup(hash).depth) {
            State state = new State(hash, depth, value, bestMove, nodeType);
            hashMap.put((hash & 0xFFFF), state);
//        }
    }

    public void clear() {
        hashMap = new HashMap<>();
    }
}

enum NodeType {
    EXACT,     //Exact score
    ALPHA,    //Score is lower bound
    BETA;    //Score is upper boumd
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


