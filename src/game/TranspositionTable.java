package game;

import java.util.HashMap;

public class TranspositionTable {

    HashMap<Long, State> hashMap;

    public TranspositionTable() {
        hashMap = new HashMap<>();
    }


    public State lookup(long hash, int depth, int alpha, int beta) {
        State state = hashMap.get(hash & 0xFFFF);
        if (state != null && state.hash == hash && state.depth >= depth) {
            return state;
        }

        return null;
    }

    //TODO add replacement scheme
    public void saveState(long hash, int depth, int value, Move bestMove, NodeType nodeType) {
        State state = new State(hash, depth, value, bestMove, nodeType);
        hashMap.put((hash & 0xFFFF), state);
    }

    /*
    int ProbeHash(int depth, int alpha, int beta)
{

    HASHE * phashe = &hash_table[ZobristKey() % TableSize()];

    if (phashe->key == ZobristKey()) {
        if (phashe->depth >= depth) {
            if (phashe->flags == hashfEXACT)
                return phashe->val;
            if ((phashe->flags == hashfALPHA) &&
                (phashe->val <= alpha))
                return alpha;
            if ((phashe->flags == hashfBETA) &&
                (phashe->val >= beta))
                return beta;
        }
        RememberBestMove();
    }
    return valUNKNOWN;
}



void RecordHash(int depth, int val, int hashf)
    HASHE * phashe = &hash_table[ZobristKey() % TableSize()];
    phashe->key = ZobristKey();
    phashe->best = BestMove();
    phashe->val = val;
    phashe->hashf = hashf;
    phashe->depth = depth;
}
     */

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


