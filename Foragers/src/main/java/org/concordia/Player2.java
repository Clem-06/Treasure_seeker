package org.concordia;

public class Player2 {

    public char texture = '2';
    public int x;
    public int y;
    public int score;

    private int positionsSearched;

    public Player2(int x, int y) {
        this.x = x;
        this.y = y;
        this.score = 0;
    }


    public /*Student decides the return type*/ void findPath(GameState state) {
    }

    public /*Student decides the return type*/ void predictPath(GameState state) {
    }

    public Tile moveDecision(GameState state) {
        return null;
    }

    void applyMove(GameState s, Tile next) {
        s.p2_x = next.x;
        s.p2_y = next.y;

        if (next.treasurePresent) {
            s.p2_score += next.treasure.value;
        }
    }

    public int getTeleport() {
        return 0;
    }
}
