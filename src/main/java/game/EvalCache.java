package game;

import java.util.HashMap;

public class EvalCache {

    private static final int EVAL_CACHE_SIZE = 0xFFF;
    private HashMap<Long, State> hashMap = new HashMap<>();

    public static int hits, misses;

    public Integer lookup(long hash) {
        State state = hashMap.get(hash % EVAL_CACHE_SIZE);
        if (state != null && state.hash == hash) {
            hits++;
            return state.value;
        }
        misses++;
        return null;
    }

    public void saveState(long hash, int value) {
        State state = new State(value, hash);
        hashMap.put((hash % EVAL_CACHE_SIZE), state);
    }


    private class State {

        public int value;
        public long hash;

        public State(int value, long hash) {
            this.value = value;
            this.hash = hash;
        }
    }
}


