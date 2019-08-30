package game;

public class State {

    public enum NodeType{
        PV,     //Exact score
        CUT,    //Score is lower bound
        ALL;    //Score is iååer boumd
    }

    private long hash;
    private Move bestMove;
    private int depth;
    private int score;
    private NodeType nodeType;

    public State(long hash, Move bestMove, int depth, int score, NodeType nodeType) {
        this.hash = hash;
        this.bestMove = bestMove;
        this.depth = depth;
        this.score = score;
        this.nodeType = nodeType;
    }

}
